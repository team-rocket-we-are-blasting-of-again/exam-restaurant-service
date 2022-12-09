package com.teamrocket.service;

import com.google.gson.Gson;
import com.teamrocket.entity.CamundaOrderTask;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Order;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.enums.Topic;
import com.teamrocket.model.*;
import com.teamrocket.model.camunda.*;
import com.teamrocket.repository.CamundaRepo;
import com.teamrocket.repository.ItemRepo;
import com.teamrocket.repository.OrderRepo;
import com.teamrocket.repository.RestaurantRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static java.lang.String.format;

@Service
public class OrderService implements IOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private Gson GSON = new Gson();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    RestaurantRepo restaurantRepo;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private CamundaRepo camundaRepo;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${camunda.bpm.client.base-url}")
    private String restEngine;


    @Override
    public void handleNewOrderCamunda(RestaurantOrder order) {
        LOGGER.info("RECEIVED ORDER: " + order.toString());
        try {
            sendNewOrderToRestaurant(order);
        } catch (Exception e) {
            kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), new OrderCancelled(order.getId(), "Unexpected error"));
        }
    }

    private void sendNewOrderToRestaurant(RestaurantOrder restaurantOrder) {
        String reason = "";
        try {
            Order orderEntity = saveNewOrder(restaurantOrder);
            LOGGER.info("New order saved with system_order id: {} and restaurant_order_id",
                    orderEntity.getSystemOrderId(), orderEntity.getId());

            simpMessagingTemplate.convertAndSend("/restaurant/"
                    + restaurantOrder.getRestaurantId()
                    + "/new-orders", restaurantOrder);
        } catch (NoSuchElementException e) {
            reason = "Could not find order items on restaurant's menu";
            LOGGER.info("Order with system_order id: {} cancelled due {}", restaurantOrder.getId(), reason);

            kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), new OrderCancelled(restaurantOrder.getId(), reason));
            try {
                completeCamundaTask(restaurantOrder.getId(), false, new DeliveryTask());
            } catch (Exception c) {
                LOGGER.info("No camunda task for order id {}", restaurantOrder.getId());
            }

        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Order with system_order_id {} already exists", restaurantOrder.getId());
            reason = format("Order with system_order_id %d already exists", restaurantOrder.getId());
            kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), new OrderCancelled(restaurantOrder.getId(), reason));
            try {
                completeCamundaTask(restaurantOrder.getId(), false, new DeliveryTask());
            } catch (Exception c) {
                LOGGER.info("No camunda task for order id {}", restaurantOrder.getId());
            }
        }
    }

    private Order saveNewOrder(RestaurantOrder restaurantOrder) {
        Map<Item, Integer> items = new HashMap<>();
        for (OrderItem item : restaurantOrder.getItems()) {
            Item itemEntity = itemRepo.findById(item.getMenuItemId())
                    .orElseThrow(() -> new NoSuchElementException("No MenuItem present with given ID: "
                            + item.getMenuItemId()));
            items.put(itemEntity, item.getQuantity());
        }
        Order order = new Order(restaurantOrder, items);
        order = orderRepo.save(order);
        LOGGER.info("SAVED ORDER WITH ID: {}", restaurantOrder.getId());
        return order;
    }

    @Override
    public void sendPendingOrdersToRestaurant(int restaurantId) {
        List<Order> pendingOrders = orderRepo.
                findAllByRestaurantIdAndStatusAndCreatedAtBefore(restaurantId, OrderStatus.PENDING, new Date());

        pendingOrders.forEach(order -> {
            RestaurantOrder restaurantOrder = new RestaurantOrder(order);
            simpMessagingTemplate.convertAndSend("/restaurant/"
                    + order.getRestaurantId()
                    + "/new-orders", restaurantOrder);
        });
    }

    @Override
    public String acceptOrder(OrderActionRequest acceptRequest) {
        String msg = format("Order with id %d for restaurant id %d could not be processed",
                acceptRequest.getOrderId(), acceptRequest.getRestaurantId());
        try {
            Order order = orderRepo.findBySystemOrderId(acceptRequest.getOrderId());

            if (!order.getStatus().equals(OrderStatus.PENDING)) {
                throw new ResponseStatusException(HttpStatus.valueOf(410), "Order most likely has already been accepted");
            } else {
                order.setStatus(OrderStatus.IN_PROGRESS);
                orderRepo.save(order);
                OrderKafkaMsg kafkaMsg = new OrderKafkaMsg(order);
                kafkaTemplate.send(Topic.ORDER_ACCEPTED.toString(), kafkaMsg);

                DeliveryTask deliveryTask = getDeliverData(
                        acceptRequest.getRestaurantId(),
                        acceptRequest.getOrderId(),
                        calculatePickupTime(acceptRequest.getPrepTime()));
                completeCamundaTask(acceptRequest.getOrderId(), true, deliveryTask);
                LOGGER.info("Order with id {} Accepted", acceptRequest.getOrderId());
                return "Order in Progress";
            }
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No Order present with given ID: "
                    + acceptRequest.getOrderId());
        }
    }

    private long calculatePickupTime(int minutes) {
        Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        return timeInSecs + (minutes * 60 * 1000);
    }

    @Override
    public String restaurantCancelsOrder(OrderActionRequest cancelRequest) {
        OrderCancelled orderCancelled = new OrderCancelled(cancelRequest.getOrderId(), cancelRequest.getMsg());
        try {
            Order order = orderRepo.findBySystemOrderId(cancelRequest.getOrderId());
            if (order.getStatus().equals(OrderStatus.PENDING)) {
                order.setStatus(OrderStatus.CANCELED);
                orderRepo.save(order);
                kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), orderCancelled);
                try {
                    completeCamundaTask(cancelRequest.getOrderId(), false, new DeliveryTask());
                } catch (Exception e) {
                    LOGGER.info("No camunda task for order id {}", cancelRequest.getOrderId());
                }
                return "Order cancelled";
            } else {
                throw new ResponseStatusException(HttpStatus.valueOf(410), "Order already in progress can not be cancelled");
            }
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No Order present with given ID: "
                    + cancelRequest.getOrderId());
        }
    }


    @Override
    public String orderCollected(OrderActionRequest completeRequest) {
        try {
            Order order = orderRepo.findBySystemOrderId(completeRequest.getOrderId());
            if (!order.getStatus().equals(OrderStatus.IN_PROGRESS)) {
                throw new ResponseStatusException(HttpStatus.valueOf(410), "Order not in progress can not be collected");
            } else {
                order.setStatus(OrderStatus.PICKED_UP);
                orderRepo.save(order);
                OrderKafkaMsg kafkaMsg = new OrderKafkaMsg(order);
                kafkaTemplate.send(Topic.ORDER_PICKED_UP.toString(), kafkaMsg);
                return "Order completed";
            }
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No Order present with given ID: "
                    + completeRequest.getOrderId());
        }
    }

    @Override
    public String orderReady(OrderActionRequest readyRequest) {
        try {
            Order order = orderRepo.findBySystemOrderId(readyRequest.getOrderId());
            if (!order.getStatus().equals(OrderStatus.IN_PROGRESS)) {
                throw new ResponseStatusException(HttpStatus.valueOf(410), "Order not in process can not be collected");
            } else {
                order.setStatus(OrderStatus.READY);
                orderRepo.save(order);
                OrderKafkaMsg kafkaMsg = new OrderKafkaMsg(order);
                kafkaTemplate.send(Topic.ORDER_READY.toString(), kafkaMsg);
                return "Order ready for pickup";
            }
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No Order present with given ID: "
                    + readyRequest.getOrderId());
        }
    }

    private void completeCamundaTask(int orderId, boolean accepted, DeliveryTask deliveryTask) {
        try {
            CamundaOrderTask task = camundaRepo.findById(orderId).
                    orElseThrow(() -> new NoSuchElementException("No task defined for orderId: " + orderId));

            String url = new StringBuilder(restEngine)
                    .append("/external-task/")
                    .append(task.getTaskId())
                    .append("/complete")
                    .toString();

            String requestBody = buildTaskVariables(task.getWorkerId(), accepted, deliveryTask);

            LOGGER.info("FIRE TASK URL: {}", url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> mediaTypeList = new ArrayList();
            mediaTypeList.add(MediaType.APPLICATION_JSON);
            headers.setAccept(mediaTypeList);
            HttpEntity<String> request =
                    new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(url, request, String.class);
            LOGGER.info("completeCamundaTask with variables: {}", requestBody);

        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private String buildTaskVariables(String workerId, boolean accepted, DeliveryTask deliveryTask) {
        OrderAccepted orderAccepted = new OrderAccepted(accepted);
        Variables variables = new Variables(orderAccepted, deliveryTask);
        TaskVariables taskVariables = new TaskVariables(workerId, variables);
        return GSON.toJson(taskVariables, TaskVariables.class);
    }

    private DeliveryTask getDeliverData(int restaurantId, int orderId, long pickupTime) {
        DeliveryTaskValue deliveryTask = new DeliveryTaskValue();
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new NoSuchElementException("Could not find Restaurant with id " + restaurantId));
        deliveryTask.setOrderId(orderId);
        deliveryTask.setRestaurantAddressId(restaurant.getAddressId());
        deliveryTask.setAreaId(restaurant.getAreaId());
        deliveryTask.setRestaurantName(restaurant.getName());
        deliveryTask.setPickupTime(pickupTime);
        return DeliveryTask.builder().value(deliveryTask.toJsonString()).build();
    }
}


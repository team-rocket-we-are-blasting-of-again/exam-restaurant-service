package com.teamrocket.service;

import com.google.gson.Gson;
import com.teamrocket.entity.CamundaOrderTask;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Order;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.enums.Topic;
import com.teamrocket.model.*;
import com.teamrocket.model.camunda.OrderAccepted;
import com.teamrocket.model.camunda.TaskVariables;
import com.teamrocket.model.camunda.Variables;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public void sendNewOrderToRestaurant(RestaurantOrder restaurantOrder) {
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
            cancelOrder(new OrderActionRequest(restaurantOrder.getId(),
                    restaurantOrder.getRestaurantId(), reason));


        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Order with system_order_id {} already exists", restaurantOrder.getId());
            reason = format("Order with system_order_id %d already exists", restaurantOrder.getId());
            cancelOrder(new OrderActionRequest(restaurantOrder.getId(),
                    restaurantOrder.getRestaurantId(), reason));
        }
    }

    @Override
    public Order saveNewOrder(RestaurantOrder restaurantOrder) {

        Map<Item, Integer> items = new HashMap<>();
        for (OrderItem item : restaurantOrder.getItems()) {
            try {
                itemRepo.findById(item.getMenuItemId()).get();
            } catch (NoSuchElementException e) {
                LOGGER.warn("No MenuItem present with given ID: " + item.getMenuItemId());
                throw new NoSuchElementException("No MenuItem present with given ID: " + item.getMenuItemId());
            }
        }
        restaurantOrder.setTotalPrice(calculateOrdersTotalPrice(restaurantOrder));
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
    public ResponseEntity acceptOrder(OrderActionRequest acceptRequest) {
        String msg = format("Order with id %d for restaurant id %d could not be processed",
                acceptRequest.getOrderId(), acceptRequest.getRestaurantId());
        try {
            Order order = orderRepo.findBySystemOrderId(acceptRequest.getOrderId());
            if (!order.getStatus().equals(OrderStatus.PENDING)) {
                return ResponseEntity.status(410).body("Order most likely has already been accepted");
            } else {
                order.setStatus(OrderStatus.IN_PROGRESS);
                orderRepo.save(order);
                OrderKafkaMsg kafkaMsg = new OrderKafkaMsg(order);
                kafkaTemplate.send(Topic.ORDER_ACCEPTED.toString(), kafkaMsg);
                completeCamundaTask(acceptRequest.getOrderId(), true);
                LOGGER.info("Order Accepted");
                return ResponseEntity.ok("Order in Progress");
            }
        } catch (NullPointerException e) {
            LOGGER.error("Exception {} occurred", e.getClass());
            return ResponseEntity.status(400).body(msg);
        } catch (KafkaException e) {
            return ResponseEntity.status(500).body(msg);
        }
    }

    @Override
    public ResponseEntity cancelOrder(OrderActionRequest cancelRequest) {
        String msg = format("Order with id %d for restaurant id %d could not be processed",
                cancelRequest.getOrderId(), cancelRequest.getRestaurantId());
        OrderCancelled orderCancelled = new OrderCancelled(cancelRequest.getOrderId(), cancelRequest.getMsg());
        try {
            Order order = orderRepo.findBySystemOrderId(cancelRequest.getOrderId());
            if (order.getStatus().equals(OrderStatus.PENDING)) {
                order.setStatus(OrderStatus.CANCELED);
                orderRepo.save(order);
                kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), orderCancelled);
                try {
                    completeCamundaTask(cancelRequest.getOrderId(), false);
                } catch (Exception e) {
                    LOGGER.info("No camunda task for order id {}", cancelRequest.getOrderId());
                }
                return ResponseEntity.ok("Order cancelled");
            } else {
                return ResponseEntity.status(410).body("Order already in process can not be cancelled");
            }
        } catch (NullPointerException e) {
            LOGGER.info("No order with id {} in DB", cancelRequest.getOrderId());
            return ResponseEntity.status(500).body(msg);
        }
    }


    @Override
    public ResponseEntity orderCollected(OrderActionRequest completeRequest) {
        String msg = format("Order with id %d for restaurant id %d could not be processed",
                completeRequest.getOrderId(), completeRequest.getRestaurantId());
        try {
            Order order = orderRepo.findBySystemOrderId(completeRequest.getOrderId());
            if (!order.getStatus().equals(OrderStatus.IN_PROGRESS)) {
                return ResponseEntity.status(410).body("Order not in process can not be collected");
            } else {
                order.setStatus(OrderStatus.PICKED_UP);
                orderRepo.save(order);
                OrderKafkaMsg kafkaMsg = new OrderKafkaMsg(order);
                kafkaTemplate.send(Topic.ORDER_PICKED_UP.toString(), kafkaMsg);
                return ResponseEntity.ok("Order completed");
            }
        } catch (NullPointerException e) {
            LOGGER.info("No order with id {} in DB", completeRequest.getOrderId());
            return ResponseEntity.status(500).body(msg);
        }
    }

    @Override
    public Object orderReady(OrderActionRequest readyRequest) {
            try {
            Order order = orderRepo.findBySystemOrderId(readyRequest.getOrderId());
            if (!order.getStatus().equals(OrderStatus.IN_PROGRESS)) {
                return ResponseEntity.status(410).body("Order not in process can not be collected");
            } else {
                order.setStatus(OrderStatus.READY);
                orderRepo.save(order);
                OrderKafkaMsg kafkaMsg = new OrderKafkaMsg(order);
                kafkaTemplate.send(Topic.ORDER_READY.toString(), kafkaMsg);
                return ResponseEntity.ok("Order ready for pickup");
            }
        } catch (NullPointerException e) {
            LOGGER.info("No order with id {} in DB", readyRequest.getOrderId());
            return ResponseEntity.status(500).body(format("Order with id %d for restaurant id %d could not be processed",
                    readyRequest.getOrderId(), readyRequest.getRestaurantId()));
        }
    }

    @Override
    public RestaurantOrder getOrderWithTotalPrice(RestaurantOrder order) {
        order.setTotalPrice(calculateOrdersTotalPrice(order));
        return order;
    }

    private void completeCamundaTask(int orderId, boolean accepted) {
        try {
            CamundaOrderTask task = camundaRepo.findById(orderId).
                    orElseThrow(() -> new NoSuchElementException("No task defined for orderId: " + orderId));

            String url = new StringBuilder(restEngine)
                    .append("/external-task/")
                    .append(task.getTaskId())
                    .append("/complete")
                    .toString();

            String requestBody = buildTaskVariables(task.getWorkerId(), accepted);

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

    private String buildTaskVariables(String workerId, boolean accepted) {
        OrderAccepted orderAccepted = new OrderAccepted(accepted);
        Variables variables = new Variables(orderAccepted);
        TaskVariables taskVariables = new TaskVariables(workerId, variables);
        return GSON.toJson(taskVariables, TaskVariables.class);
    }

    private double calculateOrdersTotalPrice(RestaurantOrder restaurantOrder) {
        double totalPrice = 0;

        Map<Integer, Double> itemPriceMap = mapItemPrice(restaurantOrder);
        for (OrderItem orderItem : restaurantOrder.getItems()) {
            totalPrice += (
                    itemPriceMap.get(orderItem.getMenuItemId()) * orderItem.getQuantity()
            );
        }
        return totalPrice;
    }

    private Map<Integer, Double> mapItemPrice(RestaurantOrder restaurantOrder) {
        Set<Item> menu = restaurantRepo.findByIdWithMenu(restaurantOrder.getRestaurantId()).getMenu();
        Map<Integer, Double> itemPriceMap = new HashMap<>();
        menu.forEach(item -> itemPriceMap.put(item.getId(), item.getPrice()));
        return itemPriceMap;
    }
}


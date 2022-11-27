package com.teamrocket.service;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Order;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.enums.Topic;
import com.teamrocket.model.OrderCancelled;
import com.teamrocket.model.OrderItem;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.repository.ItemRepo;
import com.teamrocket.repository.OrderRepo;
import com.teamrocket.repository.RestaurantRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.String.format;

@Service
public class OrderService implements IOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

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


    @KafkaListener(id = "order_service", topics = "NEW_ORDER_PLACED")
    @KafkaHandler
    @Override
    public void listenOnPlaceNewOrderKafka(@Payload RestaurantOrder order) {
        LOGGER.debug("RECEIVED ORDER: " + order.toString());
        sendNewOrderToRestaurant(order);
    }

    @Override
    public void handleNewOrderCamunda(RestaurantOrder order) {
        LOGGER.debug("RECEIVED ORDER: " + order.toString());
        sendNewOrderToRestaurant(order);
    }

    @Override
    public void sendNewOrderToRestaurant(RestaurantOrder restaurantOrder) {
        String reason = "";
        try {
            Order orderEntity = saveNewOrder(restaurantOrder);
            LOGGER.info("New order saved with system_order id: {} and restaurant_order_id", orderEntity.getSystemOrderId(), orderEntity.getId());

            simpMessagingTemplate.convertAndSend("/restaurant/" + restaurantOrder.getRestaurantId() + "/new-orders", restaurantOrder);
        } catch (NoSuchElementException e) {
            reason = "Could not find order items on restaurant's menu";
            LOGGER.info("Order with system_order id: {} cancelled", restaurantOrder.getId());

        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Order with system_order_id {} already exists", restaurantOrder.getId());
            reason = format("Order with system_order_id %d already exists", restaurantOrder.getId());
        } finally {
            cancelOrder(restaurantOrder, reason);

        }
    }

    @Override
    public void sendPendingOrdersToRestaurant(int restaurantId) {
        List<Order> pendingOrders = orderRepo.findAllByRestaurantIdAndStatusAndCreatedAtBefore(restaurantId, OrderStatus.PENDING, new Date());

        pendingOrders.forEach(order -> {
            System.out.println(order.getId());

            RestaurantOrder restaurantOrder = new RestaurantOrder(order);
            simpMessagingTemplate.convertAndSend("/restaurant/" + order.getRestaurantId() + "/new-orders", restaurantOrder);
        });
    }

    @Override
    public String acceptOrder(RestaurantOrder restaurantOrder) {
        String msg = "Order in Progress";
        try {
            Order order = orderRepo.findBySystemOrderId(restaurantOrder.getId());
            order.setStatus(OrderStatus.IN_PROGRESS);
            kafkaTemplate.send(Topic.ORDER_ACCEPTED.toString(), restaurantOrder);
            return msg;
        } catch (NullPointerException e) {
            LOGGER.error("Exception {} occurred", e.getClass());
            msg = format("No Order with id %f found", restaurantOrder.getRestaurantId());
            throw new NullPointerException(msg);
        } catch (KafkaException k) {
            msg = format("Order with id %f could not be processed with broker", restaurantOrder.getRestaurantId());
            cancelOrder(restaurantOrder, msg);
            throw new RuntimeException(msg);
        } catch (RuntimeException a) {
            LOGGER.error("Exception {} occurred", a.getClass());
            msg = format("Order with id %f could not be processed due system error", restaurantOrder.getRestaurantId());
            cancelOrder(restaurantOrder, msg);
            throw new RuntimeException(msg);
        }


    }

    @Override
    public String cancelOrder(RestaurantOrder restaurantOrder, String reason) {
        String msg = "Order cancelled";
        OrderCancelled orderCancelled = new OrderCancelled(restaurantOrder.getId(), reason);
        Order order = orderRepo.findBySystemOrderId(restaurantOrder.getId());
        order.setStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
        kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), orderCancelled);
        return msg;
    }

    @Override
    public Order saveNewOrder(RestaurantOrder restaurantOrder) {
        LOGGER.info("SAVED ORDER WITH ID: {}", restaurantOrder.getId());

        Map<Item, Integer> items = new HashMap<>();
        for (OrderItem item : restaurantOrder.getItems()) {
            itemRepo.findById(item.getMenuItemId()).orElseThrow(() -> new NoSuchElementException("No MenuItem present with given ID: " + item.getMenuItemId()));
        }
        restaurantOrder.setTotalPrice(calculateOrdersTotalPrice(restaurantOrder));
        Order order = new Order(restaurantOrder, items);
        order = orderRepo.save(order);
        return order;
    }

    @Override
    public double calculateOrdersTotalPrice(RestaurantOrder restaurantOrder) {
        double totalPrice = 0;

        Map<Integer, Double> itemPriceMap = mapItemPrice(restaurantOrder);
        for (OrderItem orderItem : restaurantOrder.getItems()) {
            totalPrice += (
                    itemPriceMap.get(orderItem.getMenuItemId()) * orderItem.getQuantity()
            );
        }
        return totalPrice;
    }

    @Override
    public Map<Integer, Double> mapItemPrice(RestaurantOrder restaurantOrder) {
        Set<Item> menu = restaurantRepo.findByIdWithMenu(restaurantOrder.getRestaurantId()).getMenu();
        Map<Integer, Double> itemPriceMap = new HashMap<>();
        menu.forEach(item -> itemPriceMap.put(item.getId(), item.getPrice()));

        return itemPriceMap;
    }


}
//    @Autowired
//    private RestaurantRepo restaurantRepo;
//
//    public void populate_order_items() {
//        Random random = new Random();
//        //for each restaurant
//        for (int i = 1; i < 101; i++) {
//            System.out.println("Restaurant : " + i);
//
//            //find all  orders of that restaurant
//            List<Order> orders = orderRepo.findAllByRestaurantId(i);
//
//            for (Order order : orders) {
//                System.out.println("\t\t order " + order.getId());
//                //how many items for order
//                int itemsPerOrder = random.nextInt(8);
//                Collection<Item> menu = restaurantRepo.findByIdWithMenu(i).getMenu();
//
//                Map<Item, Integer> orderItems = new HashMap<>();
//                System.out.println("\t\t\t\t items quantity: " + itemsPerOrder);
//
//                for (int j = 0; j < itemsPerOrder; j++) {
//                    int quantity = random.nextInt(8);
//                    int index = random.nextInt(menu.size());
//                    orderItems.put(menu.stream().toList().get(index), quantity);//
//                }
//
//                order.setOrderItems(orderItems);
//                orderRepo.save(order);
//                System.out.println("ORDER UPDATED");
//            }
//        }
//    }


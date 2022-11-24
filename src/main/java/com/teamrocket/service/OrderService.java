package com.teamrocket.service;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Order;
import com.teamrocket.enums.Topic;
import com.teamrocket.model.OrderItem;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.repository.ItemRepo;
import com.teamrocket.repository.OrderRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class OrderService implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(id = "order_service", topics = "NEW_ORDER_PLACED")
    @KafkaHandler
    @Override
    public void listenOnPlaceNewOrderKafka(@Payload RestaurantOrder order) {
        logger.debug("RECEIVED ORDER: " + order.toString());

        sendNewOrderToRestaurant(order);

    }

    @Override
    public void listenOnPlaceNewOrderCamunda(RestaurantOrder order) {
        logger.debug("RECEIVED ORDER: " + order.toString());

        sendNewOrderToRestaurant(order);

    }



    @Override
    public void sendOrderWithWebSocket(RestaurantOrder order) {

    }

    @Override
    public int sendPendingOrdersToRestaurant(int restaurantId) {
        return 0;
    }

    @Override
    public Order saveNewOrder(RestaurantOrder restaurantOrder) {
        Map<Item, Integer> items = new HashMap<>();
        for (OrderItem item : restaurantOrder.getItems()) {
            itemRepo.findById(item.getMenuItemId()).orElseThrow(() -> new NoSuchElementException("No MenuItem present with given ID: " + item.getMenuItemId()));
        }
        Order order = new Order(restaurantOrder, items);
        order = orderRepo.save(order);
        return order;
    }

    private void sendNewOrderToRestaurant(RestaurantOrder order) {
        try {
            Order orderEntity = saveNewOrder(order);
            logger.info("New order saved with system_order id: {} and restaurant_order_id", orderEntity.getSystemOrderId(), orderEntity.getId());

            simpMessagingTemplate.convertAndSend("/new-orders/" + order.getRestaurantId() + "/private", order);

        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), order);
            logger.info("Order with system_order id: {} cancelled", order.getId());
        }
    }
}

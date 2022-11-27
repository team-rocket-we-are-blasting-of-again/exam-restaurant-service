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

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

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
        LOGGER.debug("RECEIVED ORDER: " + order.toString());

        sendNewOrderToRestaurant(order);

    }

    @Override
    public void handleNewOrderCamunda(RestaurantOrder order) {
        LOGGER.debug("RECEIVED ORDER: " + order.toString());
        sendNewOrderToRestaurant(order);
    }

    @Override
    public void sendNewOrderToRestaurant(RestaurantOrder order) {
        try {
            Order orderEntity = saveNewOrder(order);
            LOGGER.info("New order saved with system_order id: {} and restaurant_order_id", orderEntity.getSystemOrderId(), orderEntity.getId());

            simpMessagingTemplate.convertAndSend("/new-orders/" + order.getRestaurantId() + "/private", order);

        } catch (NoSuchElementException e) {
            LOGGER.warn(e.getMessage());
            kafkaTemplate.send(Topic.ORDER_CANCELED.toString(), order);
            LOGGER.info("Order with system_order id: {} cancelled", order.getId());
        }
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


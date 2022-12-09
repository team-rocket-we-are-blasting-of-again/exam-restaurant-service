package com.teamrocket.service;

import com.teamrocket.entity.Item;
import com.teamrocket.proto.Order;
import com.teamrocket.proto.OrderItem;
import com.teamrocket.proto.RestaurantGrpc.RestaurantImplBase;
import com.teamrocket.repository.RestaurantRepo;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@GrpcService
public class RestaurantManagement extends RestaurantImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantManagement.class);

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Override
    public void calculateOrderPrice(Order order, StreamObserver<Order> responseObserver) {
        LOGGER.info("Received order: restid {}, total price{}, item size{}", order.getRestaurantId(), order.getTotalPrice(), order.getItemsList().size());
        LOGGER.info("items {}", order.getItemsList());
        try {
            order = calculateOrdersTotalPrice(order);
            responseObserver.onNext(order);
            responseObserver.onCompleted();
            LOGGER.info("Returned order: {}, {}, {}",
                    order.getRestaurantId(), order.getTotalPrice(), order.getItemsList().size());

        } catch (NoSuchElementException e) {
            LOGGER.warn(e.getMessage());
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private Order calculateOrdersTotalPrice(Order order) {
        double totalPrice = 0;
        int restaurantId = order.getRestaurantId();
        Map<Integer, Double> itemPriceMap = mapItemPrice(order);
        LOGGER.info("PRICES FROM DB: {}", itemPriceMap);
        if (itemPriceMap.size() == 0) {
            throw new NoSuchElementException("No menu for restaurant id " + order.getRestaurantId());
        }
        List<OrderItem> newItems = new ArrayList<>();
        for (OrderItem orderItem : order.getItemsList()) {
            try {
                int id = orderItem.getMenuItemId();
                int quantity = orderItem.getQuantity();
                double itemPrice = itemPriceMap.get(orderItem.getMenuItemId());

                orderItem = orderItem.newBuilderForType()
                        .setPrice(itemPrice)
                        .setMenuItemId(id)
                        .setQuantity(quantity)
                        .build();
                newItems.add(orderItem);

                totalPrice += (itemPrice * quantity);
            } catch (NullPointerException e) {
                throw new NoSuchElementException("No item with id " + orderItem.getMenuItemId()
                        + " on menu of restaurant with id " + order.getRestaurantId());
            }
        }
        LOGGER.info("Calculated total price {}", totalPrice);
        Iterable<OrderItem> iterator = newItems;
        order = order.newBuilderForType()
                .setTotalPrice(totalPrice)
                .setRestaurantId(restaurantId)
                .addAllItems(iterator)
                .build();
        LOGGER.info("order total price: {}, order first item price: {}", order.getTotalPrice(), order.getItems(0).getPrice());
        return order;

    }

    private Map<Integer, Double> mapItemPrice(Order order) {
        Set<Item> menu = restaurantRepo.findByIdWithMenu(order.getRestaurantId()).getMenu();
        Map<Integer, Double> itemPriceMap = new HashMap<>();
        menu.forEach(item -> itemPriceMap.put(item.getId(), item.getPrice()));
        return itemPriceMap;
    }
}

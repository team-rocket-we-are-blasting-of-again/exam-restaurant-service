package com.teamrocket.service;

import com.teamrocket.control.RestaurantController;
import com.teamrocket.entity.Item;
import com.teamrocket.proto.Order;
import com.teamrocket.proto.OrderItem;
import com.teamrocket.proto.RestaurantGrpc.RestaurantImplBase;
import com.teamrocket.repository.RestaurantRepo;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


@GrpcService
public class RestaurantManagement extends RestaurantImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Override
    public void calculateOrderPrice(Order order, StreamObserver<Order> responseObserver) {
        LOGGER.info("Received order: {}, {}, {}", order.getRestaurantId(), order.getTotalPrice(), order.getItemsList().size());
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

        Map<Integer, Double> itemPriceMap = mapItemPrice(order);
        if (itemPriceMap.size() == 0) {
            throw new NoSuchElementException("No menu for restaurant id " + order.getRestaurantId());
        }
        for (OrderItem orderItem : order.getItemsList()) {
            orderItem.newBuilderForType().setPrice(orderItem.getMenuItemId()).build();
            try {
                totalPrice += (
                        itemPriceMap.get(orderItem.getMenuItemId()) * orderItem.getQuantity()
                );
            } catch (NullPointerException e) {
                throw new NoSuchElementException("No item with id " + orderItem.getMenuItemId()
                        + " on menu of restaurant with id " + order.getRestaurantId());
            }
        }
        return order.newBuilderForType().setTotalPrice(totalPrice).build();

    }

    private Map<Integer, Double> mapItemPrice(Order order) {
        Set<Item> menu = restaurantRepo.findByIdWithMenu(order.getRestaurantId()).getMenu();
        LOGGER.info("Menu by restaurant id {} is {} long and is {}", order.getRestaurantId(), menu.size(), menu);
        Map<Integer, Double> itemPriceMap = new HashMap<>();
        menu.forEach(item -> itemPriceMap.put(item.getId(), item.getPrice()));
        return itemPriceMap;
    }
}

package com.teamrocket.service;

import com.teamrocket.control.RestaurantController;
import com.teamrocket.entity.Item;
import com.teamrocket.proto.Order;
import com.teamrocket.proto.OrderItem;
import com.teamrocket.proto.RestaurantGrpc.RestaurantImplBase;
import com.teamrocket.repository.RestaurantRepo;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RestaurantManagement extends RestaurantImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Override
    public void calculateOrderPrice(Order request, StreamObserver<Order> responseObserver) {
        LOGGER.info("Received order: {}, {}, {}", request.getRestaurantId(), request.getTotalPrice(), request.getItemsList().size());
        request = calculateOrdersTotalPrice(request);
        LOGGER.info("Received order: {}, {}, {}", request.getRestaurantId(), request.getTotalPrice(), request.getItemsList().size());

        super.calculateOrderPrice(request, responseObserver);
    }


    private Order calculateOrdersTotalPrice(Order order) {
        double totalPrice = 0;

        Map<Integer, Double> itemPriceMap = mapItemPrice(order);
        for (OrderItem orderItem : order.getItemsList()) {
            orderItem.newBuilderForType().setPrice(orderItem.getMenuItemId()).build();
            totalPrice += (
                    itemPriceMap.get(orderItem.getMenuItemId()) * orderItem.getQuantity()
            );
        }
        return order.newBuilderForType().setTotalPrice(totalPrice).build();

    }

    private Map<Integer, Double> mapItemPrice(Order order) {
        Set<Item> menu = restaurantRepo.findByIdWithMenu(order.getRestaurantId()).getMenu();
        Map<Integer, Double> itemPriceMap = new HashMap<>();
        menu.forEach(item -> itemPriceMap.put(item.getId(), item.getPrice()));
        return itemPriceMap;
    }

//    @Override
//    public void register(Order request, StreamObserver<Order> responseObserver) {
//
//        Restaurant restaurant = Restaurant.builder()
//                .email(request.getEmail())
//                .name(request.getName())
//                .phone(request.getPhone())
//                .build();
//
//        restaurant = restaurantRepo.save(restaurant);
//
//        RegisterRestaurantResponse.Builder regResResBuilder =
//                RegisterRestaurantResponse
//                        .newBuilder()
//                        .setId(restaurant.getId());
//
//        RegisterRestaurantResponse response = regResResBuilder.build();
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//
//    }


}

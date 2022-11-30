package com.teamrocket.service;

import com.teamrocket.entity.Order;
import com.teamrocket.model.RestaurantAcceptDeclineRequest;
import com.teamrocket.model.RestaurantOrder;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IOrderService {

    void handleNewOrderCamunda(RestaurantOrder order);

    void sendNewOrderToRestaurant(RestaurantOrder order);

    void sendPendingOrdersToRestaurant(int restaurantId);

    ResponseEntity acceptOrder(RestaurantAcceptDeclineRequest acceptRequest) throws Exception;

    ResponseEntity cancelOrder(RestaurantAcceptDeclineRequest acceptRequest);

    Order saveNewOrder(RestaurantOrder restaurantOrder);

    double calculateOrdersTotalPrice(RestaurantOrder restaurantOrder);

    Map<Integer, Double> mapItemPrice(RestaurantOrder restaurantOrder);
}

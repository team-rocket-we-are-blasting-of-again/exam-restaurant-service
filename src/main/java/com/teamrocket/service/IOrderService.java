package com.teamrocket.service;

import com.teamrocket.entity.Order;
import com.teamrocket.model.RestaurantOrder;

import java.util.Map;

public interface IOrderService {

    void listenOnPlaceNewOrderKafka(RestaurantOrder order);

    void handleNewOrderCamunda(RestaurantOrder order);

    void sendNewOrderToRestaurant(RestaurantOrder order);

    void sendPendingOrdersToRestaurant(int restaurantId);

    void acceptOrder(RestaurantOrder restaurantOrder);

    void cancelOrder(RestaurantOrder restaurantOrder, String reason);

    Order saveNewOrder(RestaurantOrder restaurantOrder);

    double calculateOrdersTotalPrice(RestaurantOrder restaurantOrder);

    Map<Integer, Double> mapItemPrice(RestaurantOrder restaurantOrder);
}

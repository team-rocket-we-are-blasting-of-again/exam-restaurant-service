package com.teamrocket.service;

import com.teamrocket.entity.Order;
import com.teamrocket.model.RestaurantOrder;

public interface IOrderService {

    void listenOnPlaceNewOrderKafka(RestaurantOrder order);

    void handleNewOrderCamunda(RestaurantOrder order);

    void sendOrderWithWebSocket(RestaurantOrder order);

    int sendPendingOrdersToRestaurant(int restaurantId);

    Order saveNewOrder(RestaurantOrder restaurantOrder);

}

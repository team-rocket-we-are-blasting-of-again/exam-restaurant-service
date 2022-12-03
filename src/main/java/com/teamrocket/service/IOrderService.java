package com.teamrocket.service;

import com.teamrocket.model.OrderActionRequest;
import com.teamrocket.model.RestaurantOrder;

public interface IOrderService {

    void handleNewOrderCamunda(RestaurantOrder order);

    void sendPendingOrdersToRestaurant(int restaurantId);

    String acceptOrder(OrderActionRequest acceptRequest) throws Exception;

    String restaurantCancelsOrder(OrderActionRequest acceptRequest);

    RestaurantOrder getOrderWithTotalPrice(RestaurantOrder order);

    String orderCollected(OrderActionRequest completeRequest);

    Object orderReady(OrderActionRequest readyRequest);
}

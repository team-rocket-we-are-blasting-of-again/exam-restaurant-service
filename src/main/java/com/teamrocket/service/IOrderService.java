package com.teamrocket.service;

import com.teamrocket.model.OrderActionRequest;
import com.teamrocket.model.RestaurantOrder;
import org.springframework.http.ResponseEntity;

public interface IOrderService {

    void handleNewOrderCamunda(RestaurantOrder order);

    void sendPendingOrdersToRestaurant(int restaurantId);

    String acceptOrder(OrderActionRequest acceptRequest) throws Exception;

    ResponseEntity cancelOrder(OrderActionRequest acceptRequest);

    RestaurantOrder getOrderWithTotalPrice(RestaurantOrder order);

    String orderCollected(OrderActionRequest completeRequest);

    Object orderReady(OrderActionRequest readyRequest);
}

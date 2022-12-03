package com.teamrocket.service;

import com.teamrocket.entity.Order;
import com.teamrocket.model.OrderActionRequest;
import com.teamrocket.model.RestaurantOrder;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IOrderService {

    void handleNewOrderCamunda(RestaurantOrder order);

    void sendNewOrderToRestaurant(RestaurantOrder order);

    void sendPendingOrdersToRestaurant(int restaurantId);

    ResponseEntity acceptOrder(OrderActionRequest acceptRequest) throws Exception;

    ResponseEntity cancelOrder(OrderActionRequest acceptRequest);

    Order saveNewOrder(RestaurantOrder restaurantOrder);

    RestaurantOrder getOrderWithTotalPrice(RestaurantOrder order);

    ResponseEntity orderCollected(OrderActionRequest completeRequest);

    Object orderReady(OrderActionRequest readyRequest);
}

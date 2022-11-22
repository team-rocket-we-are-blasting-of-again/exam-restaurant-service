package com.teamrocket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantOrder {
    int restaurantId;
    int systemOrderId;
    List<OrderItem> orderItems;
    Date receivedAt;
    String deliveryType;
    String courierName;
    String pickupTime;
    String deliveryTime;

}

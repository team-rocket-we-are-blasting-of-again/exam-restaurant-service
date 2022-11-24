package com.teamrocket.model;

import com.teamrocket.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestaurantOrder {
    private int id;
    private int restaurantId;
    private Map<String, Integer> orderItems = new HashMap();
    private Date createdAt;
    private OrderStatus status;
    private boolean withDelivery;
    private double totalPrice;
    private List<OrderItem> items = new ArrayList();

}
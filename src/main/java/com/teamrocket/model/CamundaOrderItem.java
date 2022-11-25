package com.teamrocket.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CamundaOrderItem {
    private Long id;
    private String name;
    private double price;
    private int amount;
    private Long orderId;

}

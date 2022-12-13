package com.teamrocket.model.camunda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CamundaOrderItem {
    private int id;
    private int menuItemId;
    private String name;
    private double price;
    private int amount;
    private int orderId;

}

package com.teamrocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderActionRequest {
    public int prepTime;
    int orderId;
    int restaurantId;
    String msg;
}

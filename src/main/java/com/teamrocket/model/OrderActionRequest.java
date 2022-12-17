package com.teamrocket.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderActionRequest {
    public int prepTime;
    int orderId;
    int restaurantId;
    String msg;
}

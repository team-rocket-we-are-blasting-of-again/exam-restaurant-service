package com.teamrocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestaurantAcceptDeclineRequest {
    int orderId;
    int restaurantId;
    String msg;
}

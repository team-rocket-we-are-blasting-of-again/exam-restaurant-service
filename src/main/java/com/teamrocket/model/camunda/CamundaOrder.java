package com.teamrocket.model.camunda;

import com.teamrocket.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CamundaOrder {
    private int id;
    private int customerId;
    private int restaurantId;
    private OrderStatus status;
    private List<CamundaOrderItem> items;
    private Timestamp createdAt;
    private Timestamp deliveryTime;
    private boolean withDelivery;
}

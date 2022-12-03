package com.teamrocket.model;

import com.teamrocket.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderKafkaMsg {
    private int systemOrderId;

    public OrderKafkaMsg(Order order) {
        this.systemOrderId = order.getSystemOrderId();
    }
}

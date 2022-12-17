package com.teamrocket.model;

import com.teamrocket.entity.Order;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderKafkaMsg {
    private int systemOrderId;

    public OrderKafkaMsg(Order order) {
        this.systemOrderId = order.getSystemOrderId();
    }
}

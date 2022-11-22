package com.teamrocket.service;

import com.teamrocket.dto.RestaurantOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class OrderService implements IOrderService {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Override
    public RestaurantOrder approveOrder() {
        return null;
    }
}

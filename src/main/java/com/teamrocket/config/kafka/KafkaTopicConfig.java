package com.teamrocket.config.kafka;

import com.teamrocket.enums.Topic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic NewOrderPlaced() {
        return TopicBuilder.name(Topic.NEW_ORDER_PLACED.toString()).build();
    }

    @Bean
    public NewTopic OrderAccepted() {
        return TopicBuilder.name(Topic.ORDER_ACCEPTED.toString()).build();
    }

    @Bean
    public NewTopic OrderReady() {
        return TopicBuilder.name(Topic.ORDER_READY.toString()).build();
    }

    @Bean
    public NewTopic OrderCanceled() {
        return TopicBuilder.name(Topic.ORDER_CANCELED.toString()).build();
    }

    @Bean
    public NewTopic OrderPickedUp() {
        return TopicBuilder.name(Topic.ORDER_PICKED_UP.toString()).build();
    }

    @Bean
    public NewTopic OrderDelivered() {
        return TopicBuilder.name(Topic.ORDER_DELIVERED.toString()).build();
    }

    @Bean
    public NewTopic OrderClaimed() {
        return TopicBuilder.name(Topic.ORDER_CLAIMED.toString()).build();
    }

}

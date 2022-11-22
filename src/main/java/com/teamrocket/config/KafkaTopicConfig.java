package com.teamrocket.config;

import com.teamrocket.enums.Topic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic newOrderReceived() {
        return TopicBuilder.name(Topic.newOrderReceived.toString()).build();
    }

    @Bean
    public NewTopic courierArrived() {
        return TopicBuilder.name(Topic.courierArrived.toString()).build();
    }

    @Bean
    public NewTopic orderSent() {
        return TopicBuilder.name(Topic.orderSent.toString()).build();
    }


}

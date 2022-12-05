package com.teamrocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.teamrocket"})

public class RestaurantServiceApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}

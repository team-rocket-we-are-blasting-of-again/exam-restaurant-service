package com.teamrocket.service;

import com.teamrocket.entity.Restaurant;
import com.teamrocket.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthClient.class);


    @Value("${grpc-service.host}")
    private String grpcHost;

    @Value("${grpc-service.port}")
    private int grpcPort;

    @Autowired
    UserGrpc.UserBlockingStub userBlockingStub;


    public int registerRestaurantUser(Restaurant restaurant) {
        LOGGER.info("host: {}, port: {}", grpcHost, grpcPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
        LOGGER.info("gRPC Channel {} ", channel.toString());

        CreateUserResponse response = userBlockingStub.createUser(CreateUserRequest
                .newBuilder()
                .setRole(Role.RESTAURANT)
                .setRoleId(restaurant.getId())
                .setEmail(restaurant.getEmail())
                .build());

        channel.shutdown();
        LOGGER.info("Response user id: ", response.getId());
        return response.getId();

    }
}
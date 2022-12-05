package com.teamrocket.service;

import com.teamrocket.entity.Restaurant;
import com.teamrocket.proto.CreateUserRequest;
import com.teamrocket.proto.CreateUserResponse;
import com.teamrocket.proto.Role;
import com.teamrocket.proto.UserGrpc;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthClient.class);

    @Autowired
    UserGrpc.UserBlockingStub userBlockingStub;

    @Autowired
    ManagedChannel managedChannel;


    public int registerRestaurantUser(Restaurant restaurant) {
        LOGGER.info("gRPC Channel: {} ", managedChannel.toString());

        CreateUserResponse response = userBlockingStub.createUser(CreateUserRequest
                .newBuilder()
                .setRole(Role.RESTAURANT)
                .setRoleId(restaurant.getId())
                .setEmail(restaurant.getEmail())
                .build());

        managedChannel.shutdown();
        LOGGER.info("New user with id {} created", response.getId());
        return response.getId();
    }
}
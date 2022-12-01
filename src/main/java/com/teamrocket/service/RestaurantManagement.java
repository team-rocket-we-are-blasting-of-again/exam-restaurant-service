package com.teamrocket.service;

import com.teamrocket.proto.*;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.proto.RestaurantManagementGrpc.RestaurantManagementImplBase;
import com.teamrocket.repository.RestaurantRepo;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantManagement extends RestaurantManagementImplBase {
    @Autowired
    private RestaurantRepo restaurantRepo;

    @Override
    public void register(RegisterRestaurantRequest request, StreamObserver<RegisterRestaurantResponse> responseObserver) {

        Restaurant restaurant = Restaurant.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        restaurant = restaurantRepo.save(restaurant);

        RegisterRestaurantResponse.Builder regResResBuilder =
                RegisterRestaurantResponse
                        .newBuilder()
                        .setId(restaurant.getId());

        RegisterRestaurantResponse response = regResResBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void setMenu(RegisterRestaurantRequest request, StreamObserver<RegisterRestaurantResponse> responseObserver) {
        super.setMenu(request, responseObserver);
    }

    @Override
    public void createItems(CUDItemsRequest request, StreamObserver<com.teamrocket.proto.Menu> responseObserver) {
        super.createItems(request, responseObserver);
    }

    @Override
    public void updateItems(CUDItemsRequest request, StreamObserver<com.teamrocket.proto.Menu> responseObserver) {
        super.updateItems(request, responseObserver);
    }

    @Override
    public void deleteItems(CUDItemsRequest request, StreamObserver<com.teamrocket.proto.Menu> responseObserver) {
        super.deleteItems(request, responseObserver);
    }

    @Override
    public void getMenu(com.teamrocket.proto.MenuRequest request, StreamObserver<com.teamrocket.proto.Menu> responseObserver) {
        super.getMenu(request, responseObserver);
    }

    @Override
    public void openOrClose(com.teamrocket.proto.OpenClose request, StreamObserver<com.teamrocket.proto.OpenClose> responseObserver) {
        super.openOrClose(request, responseObserver);
    }

}

package com.teamrocket.service;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.repository.RestaurantRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RestaurantServiceTest {

    @Autowired
    RestaurantService restaurantService;
    @Autowired
    RestaurantRepo restaurantRepo;

    private Restaurant restaurant;
    @Test
    void createNewRestaurant() {
        restaurant = restaurantService.createNewRestaurant("Milano");
    }

    @Test
    void addItemsToMenu() {
        restaurant = restaurantService.createNewRestaurant("Milano");
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());
        ItemsRequest request = new ItemsRequest(restaurant.getId(),items);
        System.out.println(restaurantService.addNewMenu(request));
        System.out.println();
    }
}
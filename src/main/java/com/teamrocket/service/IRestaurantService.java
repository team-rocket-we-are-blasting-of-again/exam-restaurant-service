package com.teamrocket.service;

import com.teamrocket.model.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Set;

public interface IRestaurantService {
    Restaurant createNewRestaurant(String name);

    Set<Item> addNewMenu(ItemsRequest request);

    Set<Item> addNewItems(ItemsRequest request);

    Collection<Item> editItems(ItemsRequest request);

    Collection<Item> deleteItems(ItemsRequest request);

    Collection<Item> getMenu(int id);

    ResponseEntity<String> openRestaurant(int id);

    ResponseEntity<String> closeRestaurant(int id);

}

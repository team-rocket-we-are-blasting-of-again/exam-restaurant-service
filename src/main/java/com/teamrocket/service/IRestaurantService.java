package com.teamrocket.service;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.MenuItem;
import com.teamrocket.entity.Restaurant;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Set;

public interface IRestaurantService {
    Restaurant createNewRestaurant(String name);

    Set<MenuItem> addNewMenu(ItemsRequest request);

    Set<MenuItem> addNewItems(ItemsRequest request);

    Collection<MenuItem> editItems(ItemsRequest request);

    Collection<MenuItem> deleteItems(ItemsRequest request);

    Collection<MenuItem> getMenu(int id);

    ResponseEntity<String> openRestaurant(int id);
    ResponseEntity<String> closeRestaurant(int id);

    ResponseEntity archiveRestaurant(int id);


}

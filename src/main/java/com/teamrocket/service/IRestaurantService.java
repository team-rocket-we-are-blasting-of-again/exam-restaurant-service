package com.teamrocket.service;

import com.teamrocket.entity.Order;
import com.teamrocket.model.items.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.model.RegisterRestaurantRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IRestaurantService {
    Restaurant createNewRestaurant(RegisterRestaurantRequest name);

    Set<Item> addNewMenu(ItemsRequest request);

    Set<Item> addNewItems(ItemsRequest request);

    Collection<Item> editItems(ItemsRequest request);

    Collection<Item> deleteItems(ItemsRequest request);

    Collection<Item> getMenu(int id);

    String openRestaurant(int id) throws Exception;

    String closeRestaurant(int id) throws Exception;

    String archiveRestaurant(int id) throws Exception;

    Set<Order> getOrdersForRestaurantByStatus(int restaurantId, List<String> statusList);

}

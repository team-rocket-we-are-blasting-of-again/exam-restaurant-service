package com.teamrocket.service;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.repository.RestaurantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RestaurantService implements IRestaurantService {
    @Autowired
    RestaurantRepo restaurantRepo;

    @Override
    public Restaurant createNewRestaurant(String name) {
        Restaurant restaurant = new Restaurant(name);
        return restaurantRepo.save(restaurant);
    }

    @Override
    public Set<Item> addNewMenu(ItemsRequest request) {

        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            Set<Item> items = new HashSet<>();
            items.addAll(request.getItems());
            restaurant.setMenu(items);
            return restaurantRepo.save(restaurant).getMenu();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Set<Item> addNewItems(ItemsRequest request) {
        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            restaurant.getMenu().addAll(request.getItems());
            return restaurantRepo.save(restaurant).getMenu();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Collection<Item> editItems(ItemsRequest request) {

        Set<Integer> itemIds = new HashSet<>();
        request.getItems().forEach(i -> {
            if (i.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Can not update items if no id provided"));
            } else {
                itemIds.add(i.getId());
            }
        });

        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            restaurant.getMenu().forEach(i -> {
                if (itemIds.contains(i.getId())) {
                    restaurant.getMenu().remove(i);
                }
            });
            restaurant.getMenu().addAll(request.getItems());
            return restaurantRepo.save(restaurant).getMenu();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Collection<Item> deleteItems(ItemsRequest request) {
        Set<Integer> itemIds = new HashSet<>();
        request.getItems().forEach(i -> {
            if (i.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Can not update items if no id provided"));
            } else {
                itemIds.add(i.getId());
            }
        });
        List<Item> toBeRemoved = new ArrayList<>();
        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            restaurant.getMenu().forEach(i -> {
                if (itemIds.contains(i.getId())) {
                    toBeRemoved.add(i);
                }
            });
            restaurant.getMenu().removeAll(toBeRemoved);
            return restaurantRepo.save(restaurant).getMenu();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }
}

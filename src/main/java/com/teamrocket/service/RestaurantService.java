package com.teamrocket.service;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.MenuItem;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.repository.RestaurantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Set<MenuItem> addNewMenu(ItemsRequest request) {

        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            Set<MenuItem> items = new HashSet<>();
            items.addAll(request.getItems());
            restaurant.setMenu(items);
            return restaurantRepo.save(restaurant).getMenu();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Set<MenuItem> addNewItems(ItemsRequest request) {
        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            restaurant.getMenu().addAll(request.getItems());
            return restaurantRepo.save(restaurant).getMenu();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Collection<MenuItem> editItems(ItemsRequest request) {

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
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Collection<MenuItem> deleteItems(ItemsRequest request) {
        Set<Integer> itemIds = new HashSet<>();
        request.getItems().forEach(i -> {
            if (i.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ("Can not update items if no id provided"));
            } else {
                itemIds.add(i.getId());
            }
        });
        List<MenuItem> toBeRemoved = new ArrayList<>();
        try {
            Restaurant restaurant = restaurantRepo.findByIdWithMenu(request.getRestaurantId());
            restaurant.getMenu().forEach(i -> {
                if (itemIds.contains(i.getId())) {
                    toBeRemoved.add(i);
                }
            });
            restaurant.getMenu().removeAll(toBeRemoved);
            return restaurantRepo.save(restaurant).getMenu();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Collection<MenuItem> getMenu(int id) {
        try {
            return restaurantRepo.findByIdWithMenu(id).getMenu();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", id));
        }
    }

    @Override
    public ResponseEntity<String> openRestaurant(int id) {
        int rowsUpdated = restaurantRepo.setOpenCloseRestaurant(id, true);
        if (rowsUpdated > 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("More then Restaurants updated");
        }
        if (rowsUpdated < 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status not updated");

        }
        return ResponseEntity.status(HttpStatus.OK).body("Restaurant sat to OPEN");
    }

    @Override
    public ResponseEntity<String> closeRestaurant(int id) {
        int rowsUpdated = restaurantRepo.setOpenCloseRestaurant(id, false);
        if (rowsUpdated > 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("More then one Restaurants updated");
        }
        if (rowsUpdated < 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status not updated");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Restaurant sat to CLOSED");
    }

    @Override
    public ResponseEntity archiveRestaurant(int id) {
        int rowsUpdated = restaurantRepo.setOpenArchiveRestaurant(id, true);
        if (rowsUpdated > 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("More then one Restaurants updated");
        }
        if (rowsUpdated < 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status not updated");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Restaurant sat to ARCHIVED");
    }
}

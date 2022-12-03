package com.teamrocket.service;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Order;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.RegisterRestaurantRequest;
import com.teamrocket.model.items.ItemsRequest;
import com.teamrocket.repository.OrderRepo;
import com.teamrocket.repository.RestaurantRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RestaurantService implements IRestaurantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantService.class);

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private AuthClient authClient;

    @Override
    public Restaurant createNewRestaurant(RegisterRestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        restaurant = restaurantRepo.save(restaurant);
        restaurant.setUserId(authClient.registerRestaurantUser(restaurant));

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
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
        } catch (NullPointerException e) {
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
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", request.getRestaurantId()));
        }
    }

    @Override
    public Collection<Item> getMenu(int id) {
        try {
            return restaurantRepo.findByIdWithMenu(id).getMenu();
        } catch (NullPointerException e) {
            LOGGER.info("getMenu for restaurant id {} : restaurant does not exist");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Restaurant with id %s does not exist", id));
        }
    }

    @Override
    public String openRestaurant(int id) throws Exception {
        int rowsUpdated = restaurantRepo.setOpenCloseRestaurant(id, true);
        if (rowsUpdated > 1) {
            LOGGER.warn("openRestaurant with id {} : More then one Restaurants updated", id);
            throw new Exception("More then one Restaurants updated");
        }
        if (rowsUpdated < 1) {
            LOGGER.warn("openRestaurant with id {} : No Restaurants updated", id);
            throw new Exception("Status not updated");
        }
        return "Restaurant sat to OPEN";
    }

    @Override
    public String closeRestaurant(int id) throws Exception {
        int rowsUpdated = restaurantRepo.setOpenCloseRestaurant(id, false);
        if (rowsUpdated > 1) {
            LOGGER.warn("CloseRestaurant with id {} : More then one Restaurants updated", id);
            throw new Exception("Status not updated");
        }
        if (rowsUpdated < 1) {
            LOGGER.warn("closeRestaurant with id {} : No Restaurants updated", id);
            throw new Exception("Status not updated");
        }
        return "Restaurant sat to CLOSED";
    }

    @Override
    public String archiveRestaurant(int id) throws Exception {
        int rowsUpdated = restaurantRepo.setOpenArchiveRestaurant(id, true);
        if (rowsUpdated > 1) {
            LOGGER.warn("archiveRestaurant with id {} : More then one Restaurants updated", id);
            throw new Exception("Status not updated");
        }
        if (rowsUpdated < 1) {
            LOGGER.warn("archiveRestaurant with id {} : No Restaurants updated", id);
            throw new Exception("Status not updated");
        }
        return "Restaurant sat to ARCHIVED";
    }

    @Override
    public Set<Order> getOrdersForRestaurantByStatus(int restaurantId, List<String> statusList) {
        List<OrderStatus> status = new ArrayList<>();
        for (String s : statusList) {
            try {
                s = s.toUpperCase();
                status.add(OrderStatus.valueOf(s));
            } catch (IllegalArgumentException e) {
                LOGGER.info("Invalid status: {}", s);
            }
        }
        return orderRepo.findByRestaurantIdAndStatusIn(restaurantId, status);
    }
}

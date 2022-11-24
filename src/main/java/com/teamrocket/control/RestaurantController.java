package com.teamrocket.control;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.model.OrderItem;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);


    @Autowired
    RestaurantService restaurantService;

    @PostMapping("/register")
    public Restaurant createNew(@RequestBody String name) {
        return restaurantService.createNewRestaurant(name);
    }

    @PostMapping("/menu")
    public Collection<Item> addNewMenu(@RequestBody ItemsRequest request) {
        return restaurantService.addNewMenu(request);
    }

    @PostMapping("/items")
    public Collection<Item> addNewItems(@RequestBody ItemsRequest request) {
        return restaurantService.addNewItems(request);
    }

    @PatchMapping("/items")
    public Collection<Item> editItems(@RequestBody ItemsRequest request) {
        return restaurantService.editItems(request);
    }

    @DeleteMapping("/items")
    public Collection<Item> deleteItems(@RequestBody ItemsRequest request) {
        return restaurantService.deleteItems(request);
    }

    @GetMapping("/menu")
    public Collection<Item> getMenu(@RequestParam("id") int id) {
        return restaurantService.getMenu(id);
    }


    @GetMapping("/menuids")
    public Collection<Integer> getMenuIds(@RequestParam("id") int id) {


        Collection<Item> items = restaurantService.getMenu(id);
        List<Integer> ids = new ArrayList<>();
        for (Item item: items) {
            ids.add(item.getId());
        }
        return ids;
    }


    @PatchMapping("/open")
    public ResponseEntity<String> openRestaurant(@RequestParam("id") int id) {
        return restaurantService.openRestaurant(id);
    }

    @PatchMapping("/close")
    public ResponseEntity<String> closeRestaurant(@RequestParam("id") int id) {
        return restaurantService.closeRestaurant(id);
    }

    @GetMapping
    public RestaurantOrder o() {
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            items.add(new OrderItem(i, 5 - i));
        }
        RestaurantOrder order = new RestaurantOrder();
        order.setItems(items);
        order.setCreatedAt(new Date());
        return order;
    }
}

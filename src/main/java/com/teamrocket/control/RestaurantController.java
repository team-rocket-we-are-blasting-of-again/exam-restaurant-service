package com.teamrocket.control;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.service.OrderService;
import com.teamrocket.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping("")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);


    @Autowired
    RestaurantService restaurantService;

    @Autowired
    OrderService orderService;

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


    @PatchMapping("/open")
    public ResponseEntity<String> openRestaurant(@RequestParam("id") int id) {
        return restaurantService.openRestaurant(id);
    }

    @PatchMapping("/close")
    public ResponseEntity<String> closeRestaurant(@RequestParam("id") int id) {
        return restaurantService.closeRestaurant(id);
    }

    @PatchMapping("accept")
    public ResponseEntity acceptOrder(@RequestBody RestaurantOrder restaurantOrder) {
        String msg;
        try {
            msg = orderService.acceptOrder(restaurantOrder);
            return ResponseEntity.ok().body(msg);
        } catch (NullPointerException e) {
            msg = e.getMessage();
            return ResponseEntity.status(400).body(msg);
        } catch (RuntimeException e) {
            msg = e.getMessage();
            return ResponseEntity.status(500).body(msg);
        }
    }

    @PatchMapping("reject")
    public ResponseEntity rejectOrder(@RequestBody RestaurantOrder restaurantOrder) {
        try {
            String msg = orderService.cancelOrder(restaurantOrder, "Restaurants Cancellation");
            return ResponseEntity.ok().body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("System error");
        }
    }
}

//
//    @Autowired
//    private OrderService orderService;
//
//    @GetMapping("/populate")
//    public String getMenuIds() {
//
//        orderService.populate_order_items();
//        return "DONE";
//
//    }


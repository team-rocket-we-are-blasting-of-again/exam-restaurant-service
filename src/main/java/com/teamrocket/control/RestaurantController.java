package com.teamrocket.control;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.ItemsRequest;
import com.teamrocket.model.RestaurantAcceptDeclineRequest;
import com.teamrocket.service.OrderService;
import com.teamrocket.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("")
public class RestaurantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);


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
    public ResponseEntity acceptOrder(@RequestBody RestaurantAcceptDeclineRequest acceptrequest) {

        return orderService.acceptOrder(acceptrequest);

    }

    @PatchMapping("reject")
    public ResponseEntity rejectOrder(@RequestBody RestaurantAcceptDeclineRequest cancelRequest) {
        try {
            return orderService.cancelOrder(cancelRequest);

        } catch (Exception e) {
            LOGGER.error("In reject order: {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }

    @GetMapping("/orders")
    public ResponseEntity getRestaurantsOrdersByStatus(@RequestParam("restaurant") int id, @RequestParam("status") List<String> statusList) {
       return ResponseEntity.ok(restaurantService.getOrdersForRestaurantByStatus(id, statusList));


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


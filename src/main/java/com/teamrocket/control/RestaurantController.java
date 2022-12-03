package com.teamrocket.control;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.model.OrderActionRequest;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.model.items.ItemsRequest;
import com.teamrocket.service.OrderService;
import com.teamrocket.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("register")
    public Restaurant createNew(@RequestBody String name) {
        return restaurantService.createNewRestaurant(name);
    }

    @PostMapping("menu")
    public Collection<Item> addNewMenu(@RequestBody ItemsRequest request) {
        return restaurantService.addNewMenu(request);
    }

    @PostMapping("items")
    public Collection<Item> addNewItems(@RequestBody ItemsRequest request) {
        return restaurantService.addNewItems(request);
    }

    @PatchMapping("items")
    public Collection<Item> editItems(@RequestBody ItemsRequest request) {
        return restaurantService.editItems(request);
    }

    @DeleteMapping("items")
    public Collection<Item> deleteItems(@RequestBody ItemsRequest request) {
        return restaurantService.deleteItems(request);
    }

    @GetMapping("menu")
    public Collection<Item> getMenu(@RequestParam("id") int id) {
        return restaurantService.getMenu(id);
    }

    @PatchMapping("open")
    public ResponseEntity<String> openRestaurant(@RequestParam("id") int id) {
        return restaurantService.openRestaurant(id);
    }

    @PatchMapping("close")
    public ResponseEntity<String> closeRestaurant(@RequestParam("id") int id) {
        return restaurantService.closeRestaurant(id);
    }

    @PatchMapping("/archive")
    public ResponseEntity<String>  archiveRestaurant(@RequestParam("id") int id) {
        return restaurantService.archiveRestaurant(id);
    }

    @PatchMapping("accept")
    public ResponseEntity acceptOrder(@RequestBody OrderActionRequest acceptRequest) {
        try {
            return orderService.acceptOrder(acceptRequest);
        } catch (Exception e) {
            LOGGER.error("In acceptOrder: {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }

    @PatchMapping("reject")
    public ResponseEntity rejectOrder(@RequestBody OrderActionRequest cancelRequest) {
        try {
            return ResponseEntity.ok(orderService.cancelOrder(cancelRequest));
        } catch (Exception e) {
            LOGGER.error("In rejectOrder: {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }

    @PatchMapping("ready")
    public ResponseEntity orderReady(@RequestBody OrderActionRequest readyRequest) {
        try {
            return ResponseEntity.ok(orderService.orderReady(readyRequest));
        } catch (Exception e) {
            LOGGER.error("In orderReady: {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }

    @PatchMapping("complete")
    public ResponseEntity orderCompleted(@RequestBody OrderActionRequest completeRequest) {
        try {
            return ResponseEntity.ok(orderService.orderCollected(completeRequest));
        } catch (Exception e) {
            LOGGER.error("In orderCompleted order: {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }

    @GetMapping("totalprice")
    public ResponseEntity calculateTotalPrice(@RequestBody RestaurantOrder order) {
        try {
            return ResponseEntity.ok(orderService.getOrderWithTotalPrice(order));
        } catch (Exception e) {
            LOGGER.error("In calculateTotalPrice : {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }

    @GetMapping("orders")
    public ResponseEntity getRestaurantsOrdersByStatus(@RequestParam("restaurant") int id,
                                                       @RequestParam("status") List<String> statusList) {
        try {
            return ResponseEntity.ok(restaurantService.getOrdersForRestaurantByStatus(id, statusList));
        } catch (Exception e) {
            LOGGER.error("In getRestaurantsOrdersByStatus : {}", e.getMessage());
            return ResponseEntity.status(500).body("System error");
        }
    }
}

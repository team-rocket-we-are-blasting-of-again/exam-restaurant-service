package com.teamrocket.control;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.model.OrderActionRequest;
import com.teamrocket.model.RegisterRestaurantRequest;
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

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("")
public class RestaurantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);


    @Autowired
    RestaurantService restaurantService;

    @Autowired
    OrderService orderService;

    @PostMapping("register")
    public Restaurant createNew(@RequestBody RegisterRestaurantRequest request) {
        return restaurantService.createNewRestaurant(request);
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
    public ResponseEntity openRestaurant(@RequestParam("id") int id) throws Exception {
        return ResponseEntity.ok(restaurantService.openRestaurant(id));
    }

    @PatchMapping("close")
    public ResponseEntity closeRestaurant(@RequestParam("id") int id) throws Exception {
        return ResponseEntity.ok(restaurantService.closeRestaurant(id));
    }

    @PatchMapping("archive")
    public ResponseEntity archiveRestaurant(@RequestParam("id") int id) throws Exception {
        return ResponseEntity.ok(restaurantService.archiveRestaurant(id));
    }

    @PatchMapping("accept")
    public ResponseEntity acceptOrder(@RequestBody OrderActionRequest acceptRequest) {
        return ResponseEntity.ok(orderService.acceptOrder(acceptRequest));
    }

    @PatchMapping("reject")
    public ResponseEntity rejectOrder(@RequestBody OrderActionRequest cancelRequest) {
        return ResponseEntity.ok(orderService.restaurantCancelsOrder(cancelRequest));
    }

    @PatchMapping("ready")
    public ResponseEntity orderReady(@RequestBody OrderActionRequest readyRequest) {
        return ResponseEntity.ok(orderService.orderReady(readyRequest));
    }

    @PatchMapping("complete")
    public ResponseEntity orderCompleted(@RequestBody OrderActionRequest completeRequest) {
        return ResponseEntity.ok(orderService.orderCollected(completeRequest));
    }

    @GetMapping("orders")
    public ResponseEntity getRestaurantsOrdersByStatus(@RequestParam("restaurant") int id,
                                                       @RequestParam("status") List<String> statusList) {
        return ResponseEntity.ok(restaurantService.getOrdersForRestaurantByStatus(id, statusList));
    }
}

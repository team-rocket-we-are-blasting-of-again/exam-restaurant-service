package com.teamrocket.control;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.MenuItem;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping("")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    @PostMapping("/register")
    public Restaurant createNew(@RequestBody String name) {
        return restaurantService.createNewRestaurant(name);
    }

    @PostMapping("/menu")
    public Collection<MenuItem> addNewMenu(@RequestBody ItemsRequest request) {
        return restaurantService.addNewMenu(request);
    }

    @PostMapping("/items")
    public Collection<MenuItem> addNewItems(@RequestBody ItemsRequest request) {
        return restaurantService.addNewItems(request);
    }

    @PatchMapping("/items")
    public Collection<MenuItem> editItems(@RequestBody ItemsRequest request) {
        return restaurantService.editItems(request);
    }

    @DeleteMapping("/items")
    public Collection<MenuItem> deleteItems(@RequestBody ItemsRequest request) {
        return restaurantService.deleteItems(request);
    }

    @GetMapping("/menu")
    public Collection<MenuItem> getMenu(@RequestParam("id") int id) {
        return restaurantService.getMenu(id);
    }

    @PatchMapping("/open")
    public ResponseEntity<String> openRestaurant(@RequestParam("id") int id) {
        return restaurantService.openRestaurant(id);
    }

    @PatchMapping("/close")
    public ResponseEntity<String>  closeRestaurant(@RequestParam("id") int id) {
        return restaurantService.closeRestaurant(id);
    }
}

package com.teamrocket.control;

import com.teamrocket.dto.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    @PostMapping("/register")
    public Restaurant createNew(@RequestBody String name)  {
        return restaurantService.createNewRestaurant(name);
    }

    @PostMapping("/menu")
    public Collection<Item> addNewMenu(@RequestBody ItemsRequest request)  {
        return restaurantService.addNewMenu(request);
    }

    @PostMapping("/items")
    public Collection<Item> addNewItems(@RequestBody ItemsRequest request)  {
        return restaurantService.addNewItems(request);
    }

    @PatchMapping("/items")
    public Collection<Item> editItems(@RequestBody ItemsRequest request)  {
        return restaurantService.editItems(request);
    }
    @DeleteMapping("/items")
    public Collection<Item> deleteItems(@RequestBody ItemsRequest request)  {
        return restaurantService.deleteItems(request);
    }


}

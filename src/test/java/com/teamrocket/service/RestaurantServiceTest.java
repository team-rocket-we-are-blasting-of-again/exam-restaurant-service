package com.teamrocket.service;

import com.teamrocket.model.ItemsRequest;
import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.repository.RestaurantRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("unit")
class RestaurantServiceTest {

    @MockBean
    private RestaurantRepo restaurantRepo;
    @Autowired
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private String restaurantName;
    private int restaurantId;
    private Set<Item> menu;

    @BeforeEach
    void setUp() {
        restaurantName = "Di Amora";
        restaurantId = 666;
        menu = new HashSet<>();
        menu.add(new Item(null, "Cheese-Burger", "Burger", "juicy with onion rings", 110.0));
        menu.add(new Item(null, "Bacon-Burger", "Burger", "juicy with onion rings", 110.0));
        restaurant = new Restaurant(restaurantName);
        restaurant.setId(restaurantId);

        when(restaurantRepo.save(any())).thenReturn(restaurant);
        when(restaurantRepo.findByIdWithMenu(restaurantId)).thenReturn(restaurant);
        when(restaurantRepo.setOpenCLoseRestaurant(restaurantId, true)).thenReturn(1);
        when(restaurantRepo.setOpenCLoseRestaurant(restaurantId, false)).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        reset(restaurantRepo);

    }

    @Test
    void createNewRestaurantTest() {
        System.out.println(restaurantService.createNewRestaurant(restaurantName));
        assertTrue(restaurantService.createNewRestaurant(restaurantName).getName().equals(restaurantName));
    }

    @Test
    void addNewMenuTest() {
        ItemsRequest request = new ItemsRequest(restaurantId, menu);
        assertTrue(restaurantService.addNewMenu(request).containsAll(menu));
    }

    @Test
    void givenNonExistingRestaurantIDWhenAddNewMenuThenThrowsResponseStatusException() {
        ItemsRequest request = new ItemsRequest(7, menu);
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.addNewMenu(request);
        });
    }

    @Test
    void addNewItemsTest() {
        ItemsRequest request = new ItemsRequest(restaurantId, menu);
        assertTrue(restaurantService.addNewItems(request).containsAll(menu));
    }

    @Test
    void givenNonExistingRestaurantIDWhenAddNewItemThenThrowsResponseStatusException() {
        ItemsRequest request = new ItemsRequest(6, menu);
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.addNewItems(request);
        });
    }

    @Test
    @Disabled
    void editItemsTest() {
    }

    @Test
    void givenNonExistingRestaurantIDWhenEditItemThenThrowsResponseStatusException() {
        ItemsRequest request = new ItemsRequest(6, menu);
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.addNewItems(request);
        });
    }

    @Test
    void givenNullItemIdWhenEditItemThenThrowsResponseStatusException() {
        Set<Item> items = new HashSet<>();
        items.add(new Item());
        ItemsRequest request = new ItemsRequest(restaurantId, items);
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.editItems(request);
        });
    }

    @Test
    @Disabled
    void deleteItemsTest() {
    }

    @Test
    void givenNonExistingRestaurantIDWhenDeleteItemThenThrowsResponseStatusException() {
        ItemsRequest request = new ItemsRequest(6, menu);
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.deleteItems(request);
        });
    }

    @Test
    void givenNullItemIdWhenDeleteItemThenThrowsResponseStatusException() {
        Set<Item> items = new HashSet<>();
        items.add(new Item());
        ItemsRequest request = new ItemsRequest(restaurantId, items);
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.deleteItems(request);
        });
    }

    @Test
    void getMenuTest() {
        assertTrue(restaurantService.getMenu(restaurantId).containsAll(restaurant.getMenu()));
    }

    @Test
    void givenNonExistingRestaurantIDWhenGetMenuThenThrowsResponseStatusException() {
        assertThrows(ResponseStatusException.class, () -> {
            restaurantService.getMenu(3);
        });
    }

    @Test
    void openRestaurantTest() {
        assertTrue(restaurantService.openRestaurant(restaurantId).getStatusCode() == HttpStatus.OK);
    }

    @Test
    void givenNonExistingRestaurantIDWhenOpenRestaurantThenResponseCode500() {
        assertTrue(restaurantService.openRestaurant(5).getStatusCode().value() == 500);
    }

    @Test
    void givenNonExistingRestaurantIDWhenCloseRestaurantThenResponseCode500() {
        assertTrue(restaurantService.closeRestaurant(5).getStatusCode().value() == 500);
    }

    @Test
    void closeRestaurantTest() {
        assertTrue(restaurantService.closeRestaurant(restaurantId).getStatusCode() == HttpStatus.OK);

    }

}
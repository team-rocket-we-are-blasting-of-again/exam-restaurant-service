package com.teamrocket.integration.repository;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Restaurant;
import com.teamrocket.repository.RestaurantRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class  RestaurantRepoTest {
    @Autowired
    private RestaurantRepo restaurantRepo;

    @Test
    void saveRestaurantTest() {
        Restaurant r = restaurantRepo.save(new Restaurant("MAM"));
        assertTrue(r.getId() == 1);
    }

    @Test
    void findByIdWithMenuTest() {
        Set<Item> items = new HashSet<>();
        Restaurant r = new Restaurant("MAM");

        items.add(new Item(null, "Cheese-Burger", "Burger", "juicy with onion rings", 110.0));
        items.add(new Item(null, "Bacon-Burger", "Burger", "juicy with onion rings", 110.0));
        r.setMenu(items);
        r = restaurantRepo.save(r);
        assertTrue(restaurantRepo.findByIdWithMenu(r.getId()).getMenu().size() == items.size());
    }

    @Test
    void setOpenCloseRestaurantTest() {
        Restaurant r = restaurantRepo.save(new Restaurant("MAM"));
        restaurantRepo.setOpenCloseRestaurant(r.getId(), true);
        assertTrue(restaurantRepo.findById(r.getId()).get().isOpen());
    }

}

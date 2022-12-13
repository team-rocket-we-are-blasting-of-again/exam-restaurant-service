package com.teamrocket.repository;

import com.teamrocket.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface RestaurantRepo extends JpaRepository<Restaurant, Integer> {

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.menu m WHERE r.id = ?1")
    Restaurant findByIdWithMenu(int id);

    @Modifying
    @Query("UPDATE Restaurant r SET r.open= ?2 WHERE r.id = ?1")
    int setOpenCloseRestaurant(int restaurantId, boolean open);

    @Modifying
    @Query("UPDATE Restaurant r SET r.archived= ?2 WHERE r.id = ?1")
    int setOpenArchiveRestaurant(int restaurantId, boolean archive);
}

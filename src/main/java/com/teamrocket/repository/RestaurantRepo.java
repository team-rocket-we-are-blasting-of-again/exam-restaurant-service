package com.teamrocket.repository;

import com.teamrocket.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepo extends JpaRepository<Restaurant, Integer> {


    @Query("SELECT r FROM Restaurant r left Join fetch r.menu m WHERE r.id = ?1")
    Restaurant findByIdWithMenu(int id);


}

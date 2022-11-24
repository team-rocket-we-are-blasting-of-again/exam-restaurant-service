package com.teamrocket.repository;

import com.teamrocket.entity.Order;
import com.teamrocket.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findAllByRestaurantIdAndStatusAndCreatedAtBefore(int restaurantId, OrderStatus status, Date before);

    @Query("SELECT o.id FROM RestaurantOrder o WHERE o.restaurantId = ?1")
    List<Integer> getOrderIdsByRestaurant(int restaurantId);


}

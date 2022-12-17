package com.teamrocket.repository;

import com.teamrocket.entity.Order;
import com.teamrocket.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findAllByRestaurantIdAndStatusAndCreatedAtBefore(int restaurantId, OrderStatus status, Date before);

    List<Order> findAllByRestaurantId(int restaurantId);

    @Query("SELECT o.id FROM RestaurantOrder o WHERE o.restaurantId = ?1")
    List<Integer> getOrderIdsByRestaurant(int restaurantId);

    @Query("SELECT o FROM RestaurantOrder o WHERE o.systemOrderId=?1")
    Order findBySystemOrderId(int systemOrderId);

    Set<Order> findByRestaurantIdAndStatusIn(int restaurantId, Collection<OrderStatus> status);

}

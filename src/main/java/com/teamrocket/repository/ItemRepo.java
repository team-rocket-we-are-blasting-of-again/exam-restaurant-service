package com.teamrocket.repository;

import com.teamrocket.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Integer> {


    List<Item> findItemsByCategory(String category);

}

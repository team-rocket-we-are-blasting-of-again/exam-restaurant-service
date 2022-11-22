package com.teamrocket.repository;

import com.teamrocket.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepo extends JpaRepository<MenuItem, Integer> {
}

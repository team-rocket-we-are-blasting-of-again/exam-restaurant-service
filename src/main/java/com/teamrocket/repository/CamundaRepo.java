package com.teamrocket.repository;

import com.teamrocket.entity.CamundaOrderTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamundaRepo extends JpaRepository<CamundaOrderTask,Integer> {
}

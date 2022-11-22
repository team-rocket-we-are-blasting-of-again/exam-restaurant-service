package com.teamrocket.stream.persistent.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.teamrocket.stream.persistent.model.Message;

import java.util.List;


@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}

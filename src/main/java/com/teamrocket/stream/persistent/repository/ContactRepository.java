package com.teamrocket.stream.persistent.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.teamrocket.stream.persistent.model.Contact;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

	List<Contact> findAllByUserId(Long userId);
	
	Contact findByContactUserId(Long contactUserId);

	void deleteByContactUserId(Long contactUserId);
}

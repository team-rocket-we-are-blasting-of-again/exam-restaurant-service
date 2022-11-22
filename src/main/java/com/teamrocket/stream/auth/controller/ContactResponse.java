package com.teamrocket.stream.auth.controller;

import com.teamrocket.stream.persistent.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {
	
	private List<Contact> contacts;

}


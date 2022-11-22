package com.teamrocket.stream.auth.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivationResponse {
	
	private String email;
	
	private String activationCode;
	
}

package com.teamrocket.stream.auth.controller;


import com.teamrocket.stream.message.MessageServiceImpl;
import com.teamrocket.stream.message.dto.request.SendMessageRequest;
import com.teamrocket.stream.message.dto.response.SendMessageResponse;
import com.teamrocket.stream.persistent.model.AccessToken;
import com.teamrocket.stream.persistent.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.teamrocket.stream.auth.AuthService;
import com.teamrocket.stream.cache.respository.CacheRepository;
import com.teamrocket.stream.persistent.repository.UserRepository;
import com.teamrocket.stream.util.StringHelper;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Autowired
    CacheRepository cacheRepository;

    @RequestMapping(value = "/getcode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> getCode( @RequestBody ActivationRequest activationRequest) {
    	
    	int code = StringHelper.generateRandomNumber(6);
    	
    	// save the activation code to the cache repository (cached auth token)
    	cacheRepository.putActivationCode(activationRequest.getEmail(), String.valueOf(code));

    	ActivationResponse activationResponse = ActivationResponse.builder()
                .email(activationRequest.getEmail())
                .activationCode(String.valueOf(code))
                .build();

        return new ResponseEntity<>(
                activationResponse,
                HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        String email = cacheRepository.queryEmailActivationCode(loginRequest.getEmail(), loginRequest.getActivationCode());

        if(email == null) {
            return new ResponseEntity<>(
                    "Email not found!",
                    HttpStatus.NOT_FOUND);
        } else {
            Long userId = 0L;
            User user = userRepository.findByEmail(loginRequest.getEmail());
            if(user == null) {
                // save user in persistence
                userRepository.save(
                        User.builder()
                                .email(loginRequest.getEmail())
                                .fname(loginRequest.getEmail())
                                .lname(loginRequest.getEmail())
                                .createdAt(Calendar.getInstance().getTime())
                                .build()
                );
                user = userRepository.findByEmail(loginRequest.getEmail());
            }

            userId = user.getUserId();
            AccessToken accessToken = authService.getAccesToken(userId);

            String token = "";
            if (accessToken == null) {
                token = UUID.randomUUID().toString();
                authService.putAccessToken(token, userId);
            } else {
                token = accessToken.getToken();
            }

            return new ResponseEntity<>(
                    LoginResponse.builder()
                            .accessToken(token)
                            .build(),
                    HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/getcontacts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> getContacts( @RequestBody ContactRequest activationRequest) {

        int code = StringHelper.generateRandomNumber(6);

        // save the activation code to the cache repository (cached auth token)
        User user = userRepository.findByToken(activationRequest.getAccessToken());

        ContactResponse contactResponse = ContactResponse.builder()
                .contacts(user.getContacts())
                .build();

        return new ResponseEntity<>(
                contactResponse,
                HttpStatus.OK);
    }

}

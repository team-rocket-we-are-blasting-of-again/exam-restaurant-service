package com.teamrocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRestaurantRequest {
    private String name;
    private String phone;
    private String password;
    private String email;
}

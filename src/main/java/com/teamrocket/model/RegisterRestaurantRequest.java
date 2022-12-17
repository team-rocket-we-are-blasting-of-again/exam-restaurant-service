package com.teamrocket.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RegisterRestaurantRequest {
    private String name;
    private String phone;
    private String password;
    private String email;
}

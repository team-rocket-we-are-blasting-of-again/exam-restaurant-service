package com.teamrocket.model.items;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MenuItem {
    private Integer id;
    private String name;
    private String description;
    private double price;
}
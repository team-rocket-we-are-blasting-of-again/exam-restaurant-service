package com.teamrocket.model.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuItem {
    private Integer id;
    private String name;
    private String description;
    private double price;
}
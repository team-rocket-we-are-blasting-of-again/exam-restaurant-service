package com.teamrocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderItem {
    private Integer MenuItemId;
    private String name;
    private int price;
    private int quantity;
}

package com.teamrocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItem {
    private int menuItemId;
    private int quantity;

    @Override
    public String toString() {
        return "{" +
                "menuItemId=" + menuItemId +
                ", quantity=" + quantity +
                '}';
    }
}



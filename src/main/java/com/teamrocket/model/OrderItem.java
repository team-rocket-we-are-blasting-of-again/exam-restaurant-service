package com.teamrocket.model;

import com.teamrocket.model.camunda.CamundaOrderItem;
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
    private String name;

    public OrderItem(CamundaOrderItem camundaOrderItem) {
        this.menuItemId = camundaOrderItem.getMenuItemId();
        this.quantity = camundaOrderItem.getAmount();
        this.name = camundaOrderItem.getName();
    }

    @Override
    public String toString() {
        return "{" +
                "menuItemId=" + menuItemId +
                ", quantity=" + quantity +
                '}';
    }
}



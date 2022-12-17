package com.teamrocket.model;

import com.teamrocket.entity.Item;
import com.teamrocket.model.camunda.CamundaOrderItem;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderItem {
    private int menuItemId;
    private int quantity;
    private String name;

    public OrderItem(CamundaOrderItem camundaOrderItem) {
        this.menuItemId = camundaOrderItem.getMenuItemId();
        this.quantity = camundaOrderItem.getAmount();
        this.name = camundaOrderItem.getName();
    }

    public OrderItem(Item item, int amount) {
        this.menuItemId = item.getId();
        this.quantity = amount;
        this.name = item.getName();
    }

    @Override
    public String toString() {
        return "{" +
                "menuItemId=" + menuItemId +
                ", quantity=" + quantity +
                '}';
    }
}



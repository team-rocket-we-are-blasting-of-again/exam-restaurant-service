package com.teamrocket.model;

import com.teamrocket.enums.OrderStatus;
import lombok.*;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RestaurantOrder {
    private int id;
    private int restaurantId;
    private Date createdAt;
    private OrderStatus status;
    private boolean withDelivery;
    private double totalPrice;
    private List<OrderItem> items = new ArrayList();

    @Override
    public String toString() {
        StringBuilder itemsStr = new StringBuilder("[");
        for (int i = 0; i < items.size()-1; i++) {
            itemsStr.append(items.get(i).toString()).append(",");
        }
        itemsStr.append("]");
        return "RestaurantOrder{" +
                "id=" + id +
                ", restaurantId=" + restaurantId +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", withDelivery=" + withDelivery +
                ", totalPrice=" + totalPrice +
                ", items=" + itemsStr.toString() +
                '}';
    }
}
package com.teamrocket.model;

import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.camunda.CamundaOrder;
import com.teamrocket.model.camunda.CamundaOrderItem;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public RestaurantOrder(CamundaOrder camundaOrder) {
        this.id = camundaOrder.getId();
        this.restaurantId = camundaOrder.getRestaurantId();
        this.createdAt = camundaOrder.getCreatedAt();
        this.status = camundaOrder.getStatus();
        this.withDelivery = camundaOrder.isWithDelivery();
        mapCamundaItems(camundaOrder.getItems());
    }

    private void mapCamundaItems(List<CamundaOrderItem> camundaItems) {
        camundaItems.forEach(c -> {
            items.add(new OrderItem(c));
        });
    }


    @Override
    public String toString() {
        StringBuilder itemsStr = new StringBuilder("[");
        for (int i = 0; i < items.size() - 1; i++) {
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
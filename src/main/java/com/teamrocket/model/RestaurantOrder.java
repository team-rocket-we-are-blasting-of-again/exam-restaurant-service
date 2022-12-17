package com.teamrocket.model;

import com.teamrocket.entity.Item;
import com.teamrocket.entity.Order;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.camunda.CamundaOrder;
import com.teamrocket.model.camunda.CamundaOrderItem;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public RestaurantOrder(Order entity) {
        this.id = entity.getSystemOrderId();
        this.restaurantId = entity.getRestaurantId();
        this.createdAt = entity.getCreatedAt();
        this.status = entity.getStatus();
        this.withDelivery = entity.isWithDelivery();
        mapMenuItemsToOrderItems(entity.getOrderItems());
    }

    private void mapCamundaItems(List<CamundaOrderItem> camundaItems) {
        camundaItems.forEach(c -> {
            items.add(new OrderItem(c));
        });
    }

    private void mapMenuItemsToOrderItems(Map<Item, Integer> itemList) {
        itemList.keySet().forEach(m -> {
            items.add(new OrderItem(m, itemList.get(m)));
        });
    }
}
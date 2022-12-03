package com.teamrocket.entity;

import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.RestaurantOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "RestaurantOrder")
@Table(name = "restaurant_orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private int systemOrderId;

    private int restaurantId;

    @ElementCollection(fetch = FetchType.EAGER)

    @CollectionTable(name = "order_items",
            joinColumns = {@JoinColumn(name = "order_id",
                    referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<Item, Integer> orderItems = new HashMap<Item, Integer>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean withDelivery;

    private double totalPrice;

    public Order(RestaurantOrder restaurantOrder, Map<Item, Integer> items) {
        this.systemOrderId = restaurantOrder.getId();
        this.restaurantId = restaurantOrder.getRestaurantId();
        this.createdAt = restaurantOrder.getCreatedAt();
        this.status = restaurantOrder.getStatus();
        this.withDelivery = restaurantOrder.isWithDelivery();
        this.totalPrice = restaurantOrder.getTotalPrice();
        this.setOrderItems(items);

    }

    private double calculateTotalPrice() {
        double tPrice = 0;
        for (Item item : orderItems.keySet().stream().toList()) {
            tPrice += orderItems.get(item) * item.getPrice();
        }
        return tPrice;
    }

    public void addItem(Item item, int quantity) {
        this.orderItems.put(item, quantity);
        this.setTotalPrice(this.calculateTotalPrice());

    }

    public void setOrderItems(Map<Item, Integer> items) {
        this.orderItems = items;
        this.calculateTotalPrice();
    }

}
package com.teamrocket.entity;

import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.RestaurantOrder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "legacy_id", unique = true)
    private Integer legacyId;

    @Column(unique = true)
    private int systemOrderId;

    private Integer restaurantId;

    private Integer legacyRestaurantId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items",
        joinColumns = {
            @JoinColumn(name = "order_id", referencedColumnName = "id"),
        }
    )
    @MapKeyJoinColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<Item, Integer> orderItems = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "legacy_order_items",
        joinColumns = {
            @JoinColumn(name = "legacy_order_id", referencedColumnName = "legacy_id"),
        }
    )
    @MapKeyJoinColumn(name = "legacy_item_id")
    @Column(name = "legacy_quantity")
    private Map<Item, Integer> legacyOrderItems = new HashMap<>();

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
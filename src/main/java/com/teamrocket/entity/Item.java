package com.teamrocket.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "Item")
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Item {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "legacy_id", unique = true)
    private Integer legacyId;

    @Column(name = "legacy_restaurant_id")
    private Integer legacyRestaurantId;

    private String name;
    private String category;
    @Column(columnDefinition = "TEXT")
    private String description;
    private double price;

}

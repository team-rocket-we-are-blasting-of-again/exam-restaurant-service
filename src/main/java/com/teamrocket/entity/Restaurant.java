package com.teamrocket.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "Restaurant")
@Table(name = "restaurants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Restaurant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "legacy_id", unique = true)
    private Integer legacyId;

    @Column(name = "restaurant_name",
            columnDefinition = "TEXT",
            nullable = false)
    private String name;

    @OneToMany(targetEntity = Item.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Set<Item> menu = new HashSet<>();

    @Column(columnDefinition = "boolean default false")
    private boolean open;
    private String email;
    private String phone;
    private boolean archived;

    @Column(name = "address_id")
    private Integer addressId;

    private String areaId;

    @Column(name = "user_id", unique = true)
    private Integer userId;

    @Column(name = "legacy_user_id", unique = true)
    private Integer legacyUserId;

    @ManyToMany
    private List<Category> categories = new ArrayList<>();

    public Restaurant(String name) {
        this.name = name;
    }
}

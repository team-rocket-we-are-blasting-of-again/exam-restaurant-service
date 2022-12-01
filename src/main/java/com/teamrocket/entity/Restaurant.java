package com.teamrocket.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "Restaurant")
@Table(name = "restaurants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Restaurant {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    @ManyToMany
    private List<Category> categories;

    public Restaurant(String name) {
        this.name = name;
    }
}

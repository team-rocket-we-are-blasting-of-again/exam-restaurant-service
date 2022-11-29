package com.teamrocket.model;

import com.teamrocket.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemsRequest {
    private int restaurantId;
    private Collection<Item> items = new HashSet();
}
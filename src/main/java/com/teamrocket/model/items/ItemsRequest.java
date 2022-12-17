package com.teamrocket.model.items;

import com.teamrocket.entity.Item;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemsRequest {
    private int restaurantId;
    private Collection<Item> items = new HashSet();
}
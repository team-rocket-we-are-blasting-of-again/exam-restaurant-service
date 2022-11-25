package com.teamrocket.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class CamundaOrder {


        private Long id;
        private Long customerId;
        private Long restaurantId;
        private String status;
        private List<CamundaOrderItem> items;
}

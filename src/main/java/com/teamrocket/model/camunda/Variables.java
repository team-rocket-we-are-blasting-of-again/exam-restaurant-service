package com.teamrocket.model.camunda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Variables {
    OrderAccepted order_accepted;
    DeliveryTask delivery_task;
}
package com.teamrocket.model.camunda;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryTask {
    DeliveryTaskValue value;
}

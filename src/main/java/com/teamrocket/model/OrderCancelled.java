package com.teamrocket.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderCancelled {
    int systemOrderId;
    String reason;
}

package com.teamrocket.model.camunda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryTaskValue {
    int orderId;
    String restaurantName;
    int restaurantAddressId;
    String areaId;
    long pickupTime;


    public String toJsonString() {
        return "{" +
                "orderId=" + orderId +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantAddressId=" + restaurantAddressId +
                ", areaId='" + areaId + '\'' +
                ", pickupTime=" + pickupTime +
                '}';
    }
}

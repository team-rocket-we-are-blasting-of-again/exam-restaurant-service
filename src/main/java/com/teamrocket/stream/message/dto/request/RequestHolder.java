package com.teamrocket.stream.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHolder {
    private String topic;
    private SendMessageRequest message;
}

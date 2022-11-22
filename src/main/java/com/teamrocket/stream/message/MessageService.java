package com.teamrocket.stream.message;

import com.teamrocket.stream.persistent.model.Message;

import java.util.List;

public interface MessageService {

    public void sendMessage(String accessToken, Long sendTo, String msg, String topic);

    List<Message> getMessageHistory(Long fromUserId, Long toUserId);
}

package com.teamrocket.stream.message.broker;

import com.google.gson.Gson;
import com.teamrocket.stream.message.MessageService;
import com.teamrocket.stream.message.MessageServiceImpl;
import com.teamrocket.stream.websocket.MessageHandler;
import com.teamrocket.stream.websocket.MessageHandlerImpl;
import com.teamrocket.stream.websocket.WebSocketPool;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    @Autowired
    MessageServiceImpl messageService;

    @Autowired
    MessageHandlerImpl messageHandler;

    @KafkaListener(topics = "SEND_MESSAGE", groupId = "foo")
    public void _messagesSendToUser(@Payload String message, @Headers MessageHeaders headers) {
        System.out.println(message);
                JSONObject jsonObject = new JSONObject(message);

        LOG.info("Websocket message will be sent if corresponding destination websocket session is found");
        if (jsonObject.get("sendTo") != null
                && WebSocketPool.websockets.get(jsonObject.getLong("sendTo")) != null
                && WebSocketPool.websockets.get(jsonObject.getLong("sendTo")).size() > 0) {

            String accessToken = jsonObject.getString("accessToken");
            Long sendTo = jsonObject.getLong("sendTo");
            String msg = jsonObject.getString("msg");

            LOG.info("Websocket message is sent to " + sendTo);

            String topic = "SEND_MESSAGE";

            messageService.sendMessage(accessToken, sendTo, msg, topic);

        } else {
            LOG.info("Websocket session not found for given sendTo");
        }
    }

    @KafkaListener(topics = "SEND_MESSAGE", groupId = "foo")
    public void messagesSendToUser(@Payload String message, @Headers MessageHeaders headers) {
        System.out.println(message);

        Gson gson = new Gson();

        JSONObject jsonObject = new JSONObject(message);

        LOG.info("Websocket message will be sent if corresponding destination websocket session is found");
        if (jsonObject.get("sendTo") != null
                && WebSocketPool.websockets.get(jsonObject.getLong("sendTo")) != null
                && WebSocketPool.websockets.get(jsonObject.getLong("sendTo")).size() > 0) {

            String accessToken = jsonObject.getString("accessToken");
            Long sendTo = jsonObject.getLong("sendTo");
            String msg = jsonObject.getString("msg");

            LOG.info("Websocket message is sent to " + sendTo);

            String topic = "SEND_MESSAGE";

            messageService.sendMessage(accessToken, sendTo, msg, topic);

        } else {
            LOG.info("Websocket session not found for given sendTo");
        }
    }

}
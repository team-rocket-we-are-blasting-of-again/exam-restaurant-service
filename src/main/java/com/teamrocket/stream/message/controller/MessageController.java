package com.teamrocket.stream.message.controller;

import com.teamrocket.stream.message.MessageServiceImpl;
import com.teamrocket.stream.message.broker.MessageSender;
import com.teamrocket.stream.message.dto.request.SendMessageRequest;
import com.teamrocket.stream.message.dto.response.SendMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    MessageServiceImpl messageService;

    @Autowired
    MessageSender messageSender;

    @RequestMapping(value = "/send-message", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
        //messageService.sendMessage(sendMessageRequest.getAccessToken(), sendMessageRequest.getSendTo(), sendMessageRequest.getMessage());

        ObjectMapper mapper = new ObjectMapper();

        try {
//            messageSender.send("SEND_MESSAGE", mapper.writeValueAsString(sendMessageRequest));
            messageSender.send("SEND_MESSAGE", new JSONObject(sendMessageRequest));
            SendMessageResponse sendMessageResponse = SendMessageResponse.builder()
                    .message("Message sent successfully")
                    .build();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(
                "Message sent to Kafka topic SEND_MESSAGE",
                HttpStatus.OK);
    }

}

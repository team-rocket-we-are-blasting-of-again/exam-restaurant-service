package com.teamrocket.control;

import com.google.gson.Gson;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class ChatController {

    private int id = 0;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final Gson GSON = new Gson();


    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message) {
        System.out.println("publicccc");
        System.out.println(message.toString());

        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        System.out.println("private");

        System.out.println(message.toString());
        return message;
    }

//
//    @KafkaListener(id = "order-manager", topics = "NEW_ORDER_PLACED")
//    public void listenOrders(int in) {
//        System.out.println("listenOrders");
//        System.out.println(in);
//
//        id++;
//        RestaurantOrder order = new RestaurantOrder();
//        order.setId(112 + id);
//        order.setRestaurantId(in);
//        List<OrderItem> itemslist = new ArrayList();
//        String[] alf = {"a", "b", "c", "d"};
//        for (int i = 0; i < 4; i++) {
//            MenuItem item = new MenuItem();
//            item.setId(i);
//            item.setName(alf[i] + "__blabla");
//            item.setPrice(15 * i);
//            item.setDescription("OAOAOSOAODOA");
//            itemslist.add(new OrderItem(item.getId(), 4 - i));
//        }
//        order.setStatus(OrderStatus.PENDING);
//        order.setWithDelivery(true);
//        order.setTotalPrice(87.60);
//        order.setItems(itemslist);
//        order.setCreatedAt(new Date());
//
//        String OrderString = GSON.toJson(order);
//        System.out.println(OrderString);
//
//        simpMessagingTemplate.convertAndSend("/order/" + order.getRestaurantId() + "/orders", order)   ;
//
//
//    }
//
//    @KafkaListener(id = "user", topics = "NEW_ORDER_PLACED")
//    public void listen(int in) {
//        System.out.printf("MSG");
//        Message m = new Message();
//        m.setMessage("Msg no. " + in + "  blah");
//        m.setReceiverName("Magda");
//        m.setSenderName("User "+ id);
//        m.setStatus(Status.MESSAGE);
//
//        simpMessagingTemplate.convertAndSend("/user/" + "Magda" + "/private", m);
//
//
//    }
}

package com.teamrocket.handlers;

import com.teamrocket.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    private static Map<String, String> sessions = new HashMap();

    @Autowired
    OrderService orderService;

    @EventListener
    private void handleSessionConnect(SessionConnectEvent event) {
      //TODO handle authorization
    }

    @EventListener
    private void handleSessionConnected(SessionConnectedEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        GenericMessage msg = (GenericMessage) event.getMessage().getHeaders().get("simpConnectMessage");
        Map<String, ArrayList> map = (Map<String, ArrayList>) msg.getHeaders().get("nativeHeaders");
        String restaurantId = (String) map.get("role_id").get(0);
        sessions.put(sessionId, restaurantId);
    }


    @EventListener
    private void handleSessionSubscribe(SessionSubscribeEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        GenericMessage msg = (GenericMessage) event.getMessage();
        Map<String, ArrayList> map = (Map<String, ArrayList>) msg.getHeaders().get("nativeHeaders");
        String restaurantId = (String) map.get("role_id").get(0);

        if (restaurantId.equals(sessions.get(sessionId))) {
            orderService.sendPendingOrdersToRestaurant(Integer.parseInt(restaurantId));
        } else {
            LOGGER.info("No session with given id");
        }
    }

    @EventListener
    private void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        //TODO
//        System.out.println("handleSessionUnsubscribe");
//        System.out.println(event.getMessage());
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        //TODO
//        System.out.println("handleSessionDisconnect");
//        System.out.println(event.getMessage());

    }
}

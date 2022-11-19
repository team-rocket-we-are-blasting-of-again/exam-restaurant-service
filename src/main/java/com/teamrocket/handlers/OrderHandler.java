package com.teamrocket.handlers;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription(topicName = "pendingOrder")
public class OrderHandler implements ExternalTaskHandler {

    /*@Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println("createOrder was run!");
        Gson gson = new GsonBuilder().create();
        OrderDTO orderToCreate = gson.fromJson(externalTask.getVariableTyped("order").getValue().toString(), OrderDTO.class);
        System.out.println("orderToCreate: " + orderToCreate);
        OrderServiceImpl orderService = new OrderServiceImpl(orderRepository);
        OrderDTO orderCreated = orderService.saveOrder(orderToCreate);
        Map<String, Object> allVariables = externalTask.getAllVariables();
        allVariables.put("order", gson.toJson(orderCreated));
        externalTaskService.complete(externalTask, externalTask.getAllVariables());
    }*/

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println("pendingOrder fired!");
        externalTaskService.complete(externalTask, externalTask.getAllVariables());
    }
}
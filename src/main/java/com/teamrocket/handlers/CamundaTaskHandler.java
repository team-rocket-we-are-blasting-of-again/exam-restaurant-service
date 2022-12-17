package com.teamrocket.handlers;

import com.google.gson.Gson;
import com.teamrocket.entity.CamundaOrderTask;
import com.teamrocket.enums.OrderStatus;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.model.camunda.CamundaOrder;
import com.teamrocket.repository.CamundaRepo;
import com.teamrocket.service.OrderService;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@ExternalTaskSubscription(topicName = "pendingOrder")
public class CamundaTaskHandler implements ExternalTaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaTaskHandler.class);

    private Gson GSON = new Gson();

    @Autowired
    OrderService orderService;

    @Autowired
    private CamundaRepo camundaRepo;


    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        CamundaOrder orderRequest =
                GSON.fromJson(externalTask.getVariableTyped("order").getValue().toString(), CamundaOrder.class);

        RestaurantOrder restaurantOrder = new RestaurantOrder(orderRequest);

        String processId = externalTask.getProcessInstanceId();
        String taskDefinitionKey = externalTask.getActivityId();
        String taskId = externalTask.getId();
        String workerId = externalTask.getWorkerId();
        CamundaOrderTask task =
                new CamundaOrderTask(restaurantOrder.getId(), processId, taskDefinitionKey, taskId, workerId);
        try {
            CamundaOrderTask existingTask = camundaRepo.findById(restaurantOrder.getId()).get();
            if (existingTask.getProcessId().equals(task.getProcessId())) {

            }

        } catch (NoSuchElementException e) {
            LOGGER.info("New TASK {}", externalTask.getId());
            camundaRepo.save(task);
            LOGGER.info("Process begins with RestaurantOrder {}", restaurantOrder);

            orderService.handleNewOrderCamunda(restaurantOrder);
        }

    }


}

package com.teamrocket.handlers;

import com.google.gson.Gson;
import com.teamrocket.entity.CamundaOrderTask;
import com.teamrocket.model.RestaurantOrder;
import com.teamrocket.model.camunda.CamundaOrder;
import com.teamrocket.model.camunda.OrderAccepted;
import com.teamrocket.model.camunda.TaskVariables;
import com.teamrocket.model.camunda.Variables;
import com.teamrocket.repository.CamundaRepo;
import com.teamrocket.service.OrderService;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@ExternalTaskSubscription(topicName = "pendingOrder")
public class OrderHandler implements ExternalTaskHandler {
    private Gson GSON = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderHandler.class);

    @Autowired
    OrderService orderService;

    @Autowired
    private CamundaRepo camundaRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${camunda.bpm.client.base-url}")
    private String restEngine;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        LOGGER.info("New TASK {}", externalTask);

        CamundaOrder orderRequest = GSON.fromJson(externalTask.getVariableTyped("order").getValue().toString(), CamundaOrder.class);

        RestaurantOrder restaurantOrder = new RestaurantOrder(orderRequest);

        String processId = externalTask.getProcessInstanceId();
        String taskDefinitionKey = externalTask.getActivityId();
        String taskId = externalTask.getId();
        String workerId = externalTask.getWorkerId();
        try {
            camundaRepo.save(new CamundaOrderTask(restaurantOrder.getId(), processId, taskDefinitionKey, taskId, workerId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        orderService.handleNewOrderCamunda(restaurantOrder);
    }

    public void completeCamundaTask(RestaurantOrder order, boolean accepted) {
        try {
            CamundaOrderTask task = camundaRepo.findById(order.getId()).
                    orElseThrow(() -> new NoSuchElementException("No task defined for orderId: " + order.getId()));

            String url = new StringBuilder(restEngine)
                    .append("/external-task/")
                    .append(task.getTaskId())
                    .append("/complete")
                    .toString();

            String requestBody = buildTaskVariables(task.getWorkerId(), accepted);

            LOGGER.info("FIRE TASK URL: {}", url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> mediaTypeList = new ArrayList();
            mediaTypeList.add(MediaType.APPLICATION_JSON);
            headers.setAccept(mediaTypeList);
            HttpEntity<String> request =
                    new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(url, request, String.class);
            LOGGER.info("completeCamundaTask with variables: {}", requestBody);

        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private String buildTaskVariables(String workerId, boolean accepted) {
        OrderAccepted orderAccepted = new OrderAccepted(accepted);
        Variables variables = new Variables(orderAccepted);
        TaskVariables taskVariables = new TaskVariables(workerId, variables);
        return GSON.toJson(taskVariables, TaskVariables.class);
    }
}

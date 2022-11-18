package com.teamrocket.unit.service;

import com.teamrocket.service.TemplateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TemplateServiceTest {

    @Autowired
    TemplateService templateService;
    private String who = "Me";

    @Test
    void helloTest() {
        Assertions.assertEquals(templateService.hello("Me").getMsg(), "Hello, " + who + "!");
        Assertions.assertEquals(templateService.hello("Me").getId(), 99);
    }
}
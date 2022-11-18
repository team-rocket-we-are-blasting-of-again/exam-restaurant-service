package com.teamrocket.service;

import com.teamrocket.dto.TemplateDTO;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {
    public TemplateDTO hello(String who) {
        return new TemplateDTO(99, "Hello, " + who + "!");
    }
}

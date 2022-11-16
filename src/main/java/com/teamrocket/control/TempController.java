package com.teamrocket.control;

import com.teamrocket.service.TemplateService;
import com.teamrocket.dto.TemplateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/tmpl", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class TempController {

    private final TemplateService templateService;

    @GetMapping
    public TemplateDTO getHello() {
        return templateService.hello("You");
    }
}

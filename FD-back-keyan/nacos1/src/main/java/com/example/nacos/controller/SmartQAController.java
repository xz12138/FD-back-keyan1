package com.example.nacos.controller;

import com.example.nacos.pojo.SmartQaRequest;
import com.example.nacos.service.SmartQAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Slf4j
public class SmartQAController {
    @Autowired
    private SmartQAService smartQAService;

    @PostMapping("/smart_qa")
    public String smartQA(@RequestBody SmartQaRequest smartQaRequest) {

        return smartQAService.getBigModelResponseResult(smartQaRequest);
    }
}

package com.example.fdbacknew.controller;

import com.example.fdbacknew.pojo.FDRequest;
import com.example.fdbacknew.pojo.FDResponseData;
import com.example.fdbacknew.pojo.Result;
import com.example.fdbacknew.service.FDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class FDController {

    @Autowired
    private FDService fdService;

    @PostMapping("/fd_chat")
    public Result<FDResponseData> fdChat(@RequestBody FDRequest fdRequest) {
        FDResponseData fdResponseData = fdService.fdChat(fdRequest);
        return Result.success(fdResponseData);
    }
}

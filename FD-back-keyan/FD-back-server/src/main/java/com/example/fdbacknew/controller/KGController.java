package com.example.fdbacknew.controller;

import com.example.fdbacknew.entity.*;
import com.example.fdbacknew.pojo.Result;
import com.example.fdbacknew.service.KGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kg")
public class KGController {

    @Autowired
    private KGService kgService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        return Result.success(kgService.kgProcess(file));
    }

    @PostMapping("/getAppearByAlarmInfo")
    public Result<List<Appear>> getAppearByAlarmInfo(@RequestBody AlarmInf alarmInf) {
        return Result.success(kgService.getAppearByAlarmInfo(alarmInf.getName()));
    }

    @PostMapping("/getReasonByAlarmInfo")
    public Result<List<Reason>> getReasonByAlarmInfo(@RequestBody AlarmInf alarmInf) {
        return Result.success(kgService.getReasonByAlarmInfo(alarmInf.getName()));
    }

    @PostMapping("/getReasonByAppear")
    public Result<List<Reason>> getReasonByAppear(@RequestBody Appear appear) {
        return Result.success(kgService.getReasonByAppear(appear.getName()));
    }

    @PostMapping("/getSolutionByReason")
    public Result<List<Solution>> getSolutionByReason(@RequestBody Reason reason) {
        return Result.success(kgService.getSolutionByReason(reason.getName()));
    }

    @PostMapping("/createAlarmInf")
    public Result<String> createAlarmInf(@RequestBody AlarmInf alarmInf) {
        kgService.createAlarmInf(alarmInf.getName());
        return Result.success("");
    }

    @DeleteMapping("/deleteAlarmInfByName")
    public Result<String> deleteAlarmInfByName(@RequestBody AlarmInf alarmInf) {
        kgService.deleteAlarmInfByName(alarmInf.getName());
        return Result.success("");
    }
}

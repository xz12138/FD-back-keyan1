package com.example.fdbacknew.service;

import com.example.fdbacknew.dao.*;
import com.example.fdbacknew.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class KGService {
    @Autowired
    private AlarmIdRespository alarmIdRespository;
    @Autowired
    private AlarmInfRespository alarmInfRespository;
    @Autowired
    private AppearRespository appearRespository;
    @Autowired
    private ReasonRespository reasonRespository;
    @Autowired
    private SolutionRespository solutionRespository;
    @Autowired
    private FileProcessService fileProcessService;

    //根据alarmInf查询知识图谱appear
    public List<Appear> getAppearByAlarmInfo(String alarmInfo) {
        AlarmInf alarmInf = alarmInfRespository.findByName(alarmInfo);
        if (alarmInf != null) {
            return alarmInf.getAppears();
        }
        return null;
    }

    //根据alarmInf查询知识图谱Reason
    public List<Reason> getReasonByAlarmInfo(String alarmInfo) {
        AlarmInf alarmInf = alarmInfRespository.findByName(alarmInfo);
        if (alarmInf != null) {
            return alarmInf.getReasons();
        }
        return null;
    }

    //根据appear查询知识图谱reason
    public List<Reason> getReasonByAppear(String appear) {
        Appear appear1 = appearRespository.findByName(appear);
        if (appear1 != null) {
            return appear1.getReasons();
        }
        return null;
    }

    //根据reason查询知识图谱solution
    public List<Solution> getSolutionByReason(String reason) {
        Reason reason1 = reasonRespository.findByName(reason);
        if (reason1 != null) {
            return reason1.getSolutions();
        }
        return null;
    }

    //根据解决方案获取参数
    public List<Para> getParaBySolution(String solution) {
        Solution solution1 = solutionRespository.findByName(solution);
        if (solution1 != null) {
            return solution1.getParas();
        }
        return null;
    }

    // 增加AlarmInf节点
    public void createAlarmInf(String alarmInfStr) {
        AlarmInf alarmInf = new AlarmInf();
        alarmInf.setName(alarmInfStr);
        alarmInfRespository.save(alarmInf);
    }

    // 根据名称删除AlarmInf节点(未完成，仅删除AlarmInf节点和联系，关联节点未删除)
    public void deleteAlarmInfByName(String alarmInfStr) {
        alarmInfRespository.delete(alarmInfRespository.findByName(alarmInfStr));
    }

    // 相对路径，打开工程FD-back-keyan是"FD-back-server/src/main/resources/uploads/"
    // 打开工程FD-back-server是"src/main/resources/uploads/"
    private static final String UPLOAD_DIR = "FD-back-server/src/main/resources/uploads/";

    // 建立知识图谱
    public String kgProcess(MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return "redirect:/uploadFailure";
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().indexOf('.'));
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/uploadFailure";
        }

        List<Map<String, Object>> infoMaps = fileProcessService.readExcel(new File(UPLOAD_DIR + filename));
        for (Object alarmId : infoMaps.get(0).values()) {
            alarmIdRespository.save((AlarmId) alarmId);
        }
        for (Object appear : infoMaps.get(2).values()) {
            appearRespository.save((Appear) appear);
        }

        return "redirect:/uploadSuccess";
    }
}

package com.example.fdbacknew.service;

import com.example.fdbacknew.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Slf4j
@Service
public class FileProcessService {


    public List<Map<String, Object>> readExcel(File file) throws Exception {
        Workbook workbook = null;
        String fileName = file.getName();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            if(fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
            } else if(fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
            } else {
                log.warn("文件类型错误！");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

        //使用HashMap收集信息
        List<Map<String, Object>> infoMaps = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            infoMaps.add(new HashMap<>());
        }

        // 表
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1;; i++) {
            // 行
            Row row = sheet.getRow(i);

            // 循环终止条件
            if (row == null) break;

            // 单元格
            String alarmIdStr = row.getCell(0).getStringCellValue();
            String alarmInfoStr = row.getCell(1).getStringCellValue();
            String appearStr = row.getCell(2).getStringCellValue();
            String reasonStr = row.getCell(3).getStringCellValue();
            String solutionStr = row.getCell(4).getStringCellValue();

            // 报警号和报警信息可能不存在
            if (!alarmInfoStr.isEmpty()) {
                // 创建报警号
                if (!infoMaps.get(0).containsKey(alarmIdStr)) {
                    AlarmId alarmId = new AlarmId();
                    alarmId.setName(alarmIdStr);
                    alarmId.setAlarmId2AlarmInfs(new ArrayList<>());
                    infoMaps.get(0).put(alarmIdStr, alarmId);
                }
                // 创建报警信息
                if (!infoMaps.get(1).containsKey(alarmInfoStr)) {
                    AlarmInf alarmInf = new AlarmInf();
                    alarmInf.setName(alarmInfoStr);
                    alarmInf.setAppears(new ArrayList<>());
                    alarmInf.setReasons(new ArrayList<>());
                    infoMaps.get(1).put(alarmInfoStr, alarmInf);
                }
                // 创建报警号和报警信息的联系
                if (!infoMaps.get(5).containsKey(alarmIdStr + "|" + alarmInfoStr)) {
                    AlarmId2AlarmInf alarmId2AlarmInf = new AlarmId2AlarmInf();
                    alarmId2AlarmInf.setName("alarmId2AlarmInf");
                    alarmId2AlarmInf.setAlarmInf((AlarmInf) infoMaps.get(1).get(alarmInfoStr));
                    infoMaps.get(5).put(alarmIdStr + "|" + alarmInfoStr, alarmId2AlarmInf);
                    AlarmId alarmId = (AlarmId) infoMaps.get(0).get(alarmIdStr);
                    alarmId.getAlarmId2AlarmInfs().add(alarmId2AlarmInf);
                }
            }
            // 因现象的创建需要原因，故先创建原因
            if (!infoMaps.get(3).containsKey(alarmInfoStr + "|" + appearStr + "|" + reasonStr)) {
                Reason reason = new Reason();
                reason.setName(alarmInfoStr.isEmpty() ? reasonStr : alarmInfoStr + "|" + reasonStr);
                reason.setSolutions(new ArrayList<>());
                infoMaps.get(3).put(alarmInfoStr + "|" + appearStr + "|" + reasonStr, reason);
            }
            // 因现象的有无分不同情况
            if (!appearStr.isEmpty()) {
                // 创建现象（有现象）
                if (!infoMaps.get(2).containsKey(alarmInfoStr + "|" + appearStr)) {
                    Appear appear = new Appear();
                    appear.setName(alarmInfoStr.isEmpty() ? appearStr : alarmInfoStr + "|" + appearStr);
                    appear.setReasons(new ArrayList<>());
                    infoMaps.get(2).put(alarmInfoStr + "|" + appearStr, appear);
                }
                // 创建报警信息和现象的联系(报警信息可能不存在)
                if (!alarmInfoStr.isEmpty() && !infoMaps.get(6).containsKey(alarmInfoStr + "|" + appearStr)) {
                    AlarmInf2Appear alarmInf2Appear = new AlarmInf2Appear();
                    alarmInf2Appear.setName("alarmInf2Appear");
                    Appear appear = (Appear) infoMaps.get(2).get(alarmInfoStr + "|" + appearStr);
                    alarmInf2Appear.setAppear(appear);
                    infoMaps.get(6).put(alarmInfoStr + "|" + appearStr, alarmInf2Appear);
                    AlarmInf alarmInf = (AlarmInf) infoMaps.get(1).get(alarmInfoStr);
                    alarmInf.getAppears().add(appear);
                }
                // 创建现象和原因的联系
                if (!infoMaps.get(7).containsKey(alarmInfoStr + "|" + appearStr + "|" + reasonStr)) {
                    Appear2Reason appear2Reason = new Appear2Reason();
                    appear2Reason.setName("appear2Reason");
                    Reason reason = (Reason) infoMaps.get(3).get(alarmInfoStr + "|" + appearStr + "|" + reasonStr);
                    appear2Reason.setReason(reason);
                    infoMaps.get(7).put(alarmInfoStr + "|" + appearStr + "|" + reasonStr, appear2Reason);
                    Appear appear = (Appear) infoMaps.get(2).get(alarmInfoStr + "|" + appearStr);
                    appear.getReasons().add(reason);
                }
            } else {
                // 无现象，创建报警信息和原因的联系
                if (!infoMaps.get(6).containsKey(alarmInfoStr + "||" + reasonStr)) {
                    AlarmInf2Reason alarmInf2Reason = new AlarmInf2Reason();
                    alarmInf2Reason.setName("alarmInf2Reason");
                    Reason reason = (Reason) infoMaps.get(3).get(alarmInfoStr + "||" + reasonStr);
                    alarmInf2Reason.setReason(reason);
                    infoMaps.get(6).put(alarmInfoStr + "||" + reasonStr, alarmInf2Reason);
                    AlarmInf alarmInf = (AlarmInf) infoMaps.get(1).get(alarmInfoStr);
                    alarmInf.getReasons().add(reason);
                }
            }
            // 创建解决方案
            if (!infoMaps.get(4).containsKey(solutionStr)) {
                Solution solution = new Solution();
                solution.setName(solutionStr);
                solution.setParas(new ArrayList<>());
                infoMaps.get(4).put(solutionStr, solution);
            }
            //创建原因和解决方案的联系
            if (!infoMaps.get(8).containsKey(reasonStr + "|" + solutionStr)) {
                Reason2Solution reason2Solution = new Reason2Solution();
                reason2Solution.setName("reason2Solution");
                Solution solution = (Solution) infoMaps.get(4).get(solutionStr);
                reason2Solution.setSolution(solution);
                infoMaps.get(8).put(reasonStr + "|" + solutionStr, reason2Solution);
                Reason reason = (Reason) infoMaps.get(3).get(alarmInfoStr + "|" + appearStr + "|" + reasonStr);
                reason.getSolutions().add(solution);
            }

        }

        return infoMaps;
    }

}

package com.example.fdbacknew;

import com.example.fdbacknew.dao.AlarmIdRespository;
import com.example.fdbacknew.dao.AlarmInfRespository;
import com.example.fdbacknew.dao.AppearRespository;
import com.example.fdbacknew.entity.AlarmId;
import com.example.fdbacknew.entity.AlarmInf;
import com.example.fdbacknew.entity.Appear;
import com.example.fdbacknew.entity.Reason;
import com.example.fdbacknew.pojo.*;

import com.example.fdbacknew.service.FDService;
import com.example.fdbacknew.service.FileProcessService;
import com.example.fdbacknew.service.KGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class FdBackNewApplicationTests {
    @Autowired
    private KGService KGService;
    @Autowired
    private FDService FDService;

    @Test
    void contextLoads() {

        List<Appear> appearByAlarmInfo = KGService.getAppearByAlarmInfo("alarmInfo");
        if (appearByAlarmInfo != null) {
            for (Appear appear : appearByAlarmInfo) {
                System.out.println(new String(appear.getName()));
            }
        }
    }
        @Test
        void test1() {
            List<History> history = new ArrayList<>();
            History historychat = new History();
            historychat.setRole("user");
            historychat.setContent("你好");
            String result = FDService.getBigModelResponseResult("主轴振动过大", history);
            System.out.println(result);
        }

        @Test
        void testIntent() {
            DetectionReturn aReturn = FDService.IntentDetection("主轴振动过大");
            System.out.println(aReturn.getInputs());
            System.out.println(aReturn.getSearch_type());
        }

        @Test
        void testGetTop(){
            GetTopResponse topResponseResult = FDService.getTopResponseResult("主轴振动过大", "hnc_data");
            System.out.println(topResponseResult.getTopName().toString());
        }

        @Test
        void testGetTop1(){
            System.out.println(System.getProperty("file.encoding"));
        }

        @Test
        void testFDService(){
            FDRequest fdRequest = new FDRequest();
            fdRequest.setConversationId("123");
            fdRequest.setType("alarm_number");
            fdRequest.setPrompt("1");
            fdRequest.setTimestamp(1714267005);
            FDResponseData fdResponseData = FDService.fdChat(fdRequest);
            System.out.println(fdResponseData.getMsg());
            fdResponseData.getPara().forEach(System.out::println);
        }

        @Test
        void testFDService1(){
            List<Reason> reasonByAppear = KGService.getReasonByAppear("Z脉冲丢失");
            for (Reason reason : reasonByAppear) {
                System.out.println(new String(reason.getName()));
            }
        }

        @Test
        void testFDService2(){
            String appearByInput = FDService.getAppearByInput("2", "1. 电机堵转 | 驱动器给电机脉冲指令后，编码器反馈位置异常\n" +
                    "2. 电机堵转 | 机床出现飞车或一动轴就报警\n" +
                    "3. 电机堵转 | 机床加工一段时间报警");
            System.out.println(appearByInput);
        }

        @Autowired
        private AlarmIdRespository alarmIdRespository;

        @Autowired
        private AlarmInfRespository alarmInfRespository;

        @Autowired
        private AppearRespository appearRespository;

        @Autowired
        private FileProcessService fileProcessService;

        @Test
        void testKGAlarmInf2Reason() throws Exception {

//            File file = new File("C:\\Users\\86153\\Documents\\WeChat Files\\wxid_2gaikdo1bd0a22\\FileStorage\\File\\2024-05\\故障诊断知识图谱数据模板-全_0402_output.xlsx");
//            List<Map<String, Object>> infoMaps = fileProcessService.readExcel(file);
//            for (Object alarmId : infoMaps.get(0).values()) {
//                alarmIdRespository.save((AlarmId) alarmId);
//            }
//            for (Object appear : infoMaps.get(2).values()) {
//                appearRespository.save((Appear) appear);
//            }


//            Appear appear = appearRespository.findByName("驱动器给电机脉冲指令后，编码器反馈位置异常");
//            System.out.println(appear);

//            AlarmInf alarmInf = alarmInfRespository.findByName("电机堵转");
//            System.out.println(alarmInf);




        }
}

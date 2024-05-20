package com.example.fdbacknew.service;


import com.example.fdbacknew.entity.Appear;
import com.example.fdbacknew.entity.Para;
import com.example.fdbacknew.entity.Reason;
import com.example.fdbacknew.entity.Solution;
import com.example.fdbacknew.mapper.FDMapper;
import com.example.fdbacknew.pojo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.example.fdbacknew.constant.FilePathConstant.History_File_Path;
import static com.example.fdbacknew.constant.URLConstant.*;

@Service
@Slf4j
public class FDService {
    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private FDMapper fdMapper;
    @Autowired
    private KGService kgService;

    public FDResponseData fdChat(FDRequest fdRequest) {
        //判断是否为新用户
        int count = fdMapper.getConversationCount(fdRequest.getConversationId());
        List<History> historyList = new ArrayList<>();
        if (count == 0) {
            //新用户，创建
            addConversation(fdRequest.getConversationId());
        }else {
            //老用户
            //获取用户历史对话路径
            String conversationId = fdRequest.getConversationId();
            //判断离创建时间是否超过十五分钟
            int create_time = fdMapper.getCreateTime(conversationId);
            int current_time = (int)Instant.now().getEpochSecond();
            int diff = current_time - create_time;
            if (diff > 900) {
                //超过十五分钟，更新历史对话路径

            }else {
                //未超过十五分钟，读取历史对话
                String historyPath = fdMapper.getHistoryPath(conversationId);
                String path = History_File_Path + historyPath;
                //根据路径读取历史对话
                try {
                    historyList = getHistoryFromJson(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //获取用户输入
        String inputs = fdRequest.getPrompt();
        //意图检测
        DetectionReturn detectionReturn = IntentDetection(inputs);
        //获取意图类型
        String searchType = detectionReturn.getSearch_type();
        //获取embedding之后的输入
        String newInputs = detectionReturn.getInputs();
        String output = null;
        String prompt = null;
        List<String> para = new ArrayList<>();
        //根据意图识别结果进行相应的处理
        switch (searchType) {
            case "alarm_info":
                //报警信息
                List<Appear> appearList = kgService.getAppearByAlarmInfo(newInputs);
                if (appearList.size() == 0) {
                    //根据报警信息未找到报警现象,查找故障原因
                    List<Reason> reasonList = kgService.getReasonByAlarmInfo(newInputs);
                    KGPromptAndPara kgPromptAndPara = makePromptAlarmInf2Reason(newInputs, reasonList);
                    prompt = kgPromptAndPara.getPrompt();
                    para = kgPromptAndPara.getPara();
                }else {
                    //找到报警现象，返回报警现象
                    prompt = makePromptAlarmInfo2Appear(newInputs, appearList);
                }
                break;
            case "appear":
                //报警现象
                List<Reason> reasonList = kgService.getReasonByAppear(newInputs);
                KGPromptAndPara kgPromptAndPara = makePromptAppear2Reason(newInputs, reasonList);
                prompt = kgPromptAndPara.getPrompt();
                para = kgPromptAndPara.getPara();
                break;
            case "chat":
                //聊天,判断是否是选取原因列表中的一项，直接调用getTop服务
                boolean selectLastResult = isSelectLastResult(historyList);
                if (selectLastResult) {
                    //选取原因列表中的一项
                    String text = historyList.get(historyList.size() - 1).getContent();
                    String getAppearByInput = getAppearByInput(inputs, text);
                    if (getAppearByInput != null) {
                        List<Reason> reasonByAppear = kgService.getReasonByAppear(getAppearByInput);
                        KGPromptAndPara kgPromptAndPara1 = makePromptAppear2Reason(newInputs, reasonByAppear);
                        prompt = kgPromptAndPara1.getPrompt();
                        para = kgPromptAndPara1.getPara();
                    }
                }else{
                    GetTopResponse getTopResponse = getTopResponseResult(newInputs, "hnc_data");
                    output = String.valueOf(getTopResponse.getTopName());
                }
                break;
            case "chat_appear":
                //现象描述不清
                output = "现象描述不清,请重新描述~";
                break;
            case "engineer_feedback":
                //工程师反馈
                output = "感谢您的反馈，我们会尽快处理~";
                break;
            default:
                //其他
                break;
        }
        //将用户输入和机器回复添加到历史对话中并写入文件
        History question = new History();
        question.setRole("user");
        question.setContent(inputs);
        historyList.add(question);
        String bigModelResponseResult = null;
        //将prompt和history传入大模型
        if (prompt != null) {
            bigModelResponseResult = getBigModelResponseResult(prompt, historyList);
        }
        if (output == null) {
            output = bigModelResponseResult;
        }
        History assistant = new History();
        assistant.setRole("assistant");
        assistant.setContent(output);
        historyList.add(assistant);
        String path = History_File_Path + fdMapper.getHistoryPath(fdRequest.getConversationId());
        writeHistoryToFile(historyList, path);
        FDResponseData fdResponseData = new FDResponseData();
        fdResponseData.setMsg(output);
        fdResponseData.setHistory(historyList);
        fdResponseData.setPrompt(prompt);
        fdResponseData.setPara(para);
        return fdResponseData;
    }

    //添加conversation相关数据
    public void addConversation(String conversationId) {
        Conversation conversation = new Conversation();

        String historyPath = conversationId + ".json";
        conversation.setConversationId(conversationId);
        conversation.setHistoryPath(historyPath);
        //获得当前时间的时间戳
        int createTime = (int)Instant.now().getEpochSecond();
        conversation.setCreateTime(createTime);
        fdMapper.addConversation(conversationId, historyPath, createTime);
        //创建给定路径下的json文件
        String path = History_File_Path + historyPath;
        try {
            File file = new File(path);
            file.createNewFile();
            objectMapper.writeValue(file,new ArrayList<History>());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //意图识别
    public DetectionReturn IntentDetection(String inputs) {
        // 创建请求对象
        IntentRequest request = new IntentRequest(inputs);
        // 创建请求头并设置Content-Type为application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 封装请求体和头信息
        HttpEntity<IntentRequest> requestEntity = new HttpEntity<>(request, headers);
        // 发送POST请求
        return restTemplate.postForObject(Intent_Detection_URL, requestEntity, DetectionReturn.class);
    }

    //根据路径获取历史对话
    public List<History> getHistoryFromJson(String FilePath) throws IOException {
        Path path = Paths.get(FilePath);
        String jsonContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonContent, new TypeReference<List<History>>() {
        });
    }

    //向指定路径中写入历史对话
    public void writeHistoryToFile(List<History> history, String filePath)  {
        try {
            // 将 HistoryList 对象转换为 JSON字符串
            String jsonString = objectMapper.writeValueAsString(history);
            // 指定文件路径
            Path path = Paths.get(filePath);
            // 写入文件
            Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("保存历史对话失败");
        }
    }

    //调用getTop服务获取返回值
    public GetTopResponse getTopResponseResult(String input, String nodeType) {
        GetTopRequset getTopRequset = new GetTopRequset();
        getTopRequset.setInputs(input);
        getTopRequset.setNodeType(nodeType);
        getTopRequset.setTopK(1);
        getTopRequset.setTopThreshold(0.6);
        HttpEntity<GetTopRequset> requestEntity = new HttpEntity<>(getTopRequset);
        ResponseEntity<Result<GetTopResponse>> responseEntity = restTemplate.exchange(
                GetTop_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Result<GetTopResponse>>() {}
        );
        Result<GetTopResponse> result = responseEntity.getBody();
        if (result != null && result.getCode() == 200) {
            log.info("调用getTop服务成功");
            return  result.getData();
        }
        //调用失败
        log.error("调用getTop服务失败");
        return null;
    }

    //调用大模型服务获取返回值
    public String getBigModelResponseResult(String inputs, List<History> history) {
        SmartQaRequest smartQaRequest = new SmartQaRequest();
        smartQaRequest.setInputs(inputs);
        smartQaRequest.setHistory(history);
        ResponseEntity<Result<String>> response = restTemplate.exchange(
                Smart_QA_URL,
                HttpMethod.POST,
                new HttpEntity<>(smartQaRequest),
                new ParameterizedTypeReference<Result<String>>() {}
        );
        Result<String> result = response.getBody();
        if (result == null || result.getCode() != 200) {
            log.error("调用大模型服务失败");
            return null;
        }
        return  result.getData();
    }

    //根据报警信息和报警现象构造prompt
    public  String makePromptAlarmInfo2Appear(String alarmInfo, List<Appear> appearList) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("通过检索知识库，报警内容为").append(alarmInfo).append("；")
                .append("在知识库中找到以下").append(appearList.size()).append("种可能的现象：\n");

        for (Appear appear : appearList) {
            prompt.append(appear.getName()).append("\n");
        }
        prompt.append("这").append(appearList.size()).append("种现象可以整理为(仅很简单的整理现象，不需要对原因等信息进行整理)：");
        return prompt.toString();
    }

    //根据报警现象和原因构造prompt
    private KGPromptAndPara makePromptAppear2Reason(String appear, List<Reason> reasonList) {
        KGPromptAndPara kgPromptAndPara = new KGPromptAndPara();
        StringBuilder prompt = new StringBuilder();
        List<String> paraList = new ArrayList<>();
        prompt.append("对于现象").append(appear).append(",在知识库中找到以下").append(reasonList.size()).append("种可能的原因以及对应的解决方案：\n");
        return getKgPromptAndPara(reasonList, kgPromptAndPara, prompt, paraList);
    }

    //根据报警信息和原因构造prompt
    private KGPromptAndPara makePromptAlarmInf2Reason(String alarmInf, List<Reason> reasonList) {
        KGPromptAndPara kgPromptAndPara = new KGPromptAndPara();
        StringBuilder prompt = new StringBuilder();
        List<String> paraList = new ArrayList<>();
        prompt.append("对于报警信息").append(alarmInf).append(",在知识库中找到以下").append(reasonList.size()).append("种可能的原因以及对应的解决方案：\n");
        return getKgPromptAndPara(reasonList, kgPromptAndPara, prompt, paraList);
    }

    //由原因获取解决方案和参数并构造prompt
    private KGPromptAndPara getKgPromptAndPara(List<Reason> reasonList, KGPromptAndPara kgPromptAndPara, StringBuilder prompt, List<String> paraList) {
        for (Reason reason : reasonList) {
            List<Solution> solutionList = kgService.getSolutionByReason(reason.getName());
            prompt.append("【原因】").append(reason.getName()).append("：【解决方案】");
            for (Solution solution : solutionList) {
                prompt.append(solution.getName()).append("；\n");
                List<Para> paraBySolution = kgService.getParaBySolution(solution.getName());
                for (Para para : paraBySolution) {
                    if (!paraList.contains(para.getName())) {
                        paraList.add(para.getName());
                    }
                }
            }
        }
        prompt.append("这").append(reasonList.size()).append("种原因以及解决方案整理为（请整理为简洁的").append(reasonList.size()).append("个点）：");
        kgPromptAndPara.setPrompt(prompt.toString());
        kgPromptAndPara.setPara(paraList);
        return kgPromptAndPara;
    }

    //判断是否是对上一轮对话给出的结果进行选择
    private boolean isSelectLastResult(List<History> historyList) {
        if (historyList.size() > 0) {
            History history = historyList.get(historyList.size() - 1);
            String content = history.getContent();
            return content.contains("2.") && content.contains("|");
        }
        return false;
    }

    //根据输入序号来获取具体appear
    public String getAppearByInput(String input, String text) {
        // 使用正则表达式匹配编号，注意对点和数字进行了转义处理
        String regex = "^" + Pattern.quote(input) + "\\.\\s*(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            // 返回第二个捕获组，即编号后面的语句部分
            return matcher.group(1).trim();
        } else {
            // 如果没有找到对应编号的语句，则返回空字符串
            return "";
        }
    }
}

package com.example.nacos.service;

import com.example.nacos.pojo.History;
import com.example.nacos.pojo.Result;
import com.example.nacos.pojo.SmartQaRequest;
import com.example.nacos.constant.URLConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class SmartQAService {
    @Autowired
    private RestTemplate restTemplate;
    //调用大模型服务获取返回值
    public String getBigModelResponseResult(SmartQaRequest smartQaRequest) {

        ResponseEntity<Result<String>> response = restTemplate.exchange(
                URLConstant.Smart_QA_URL,
                HttpMethod.POST,
                new HttpEntity<>(smartQaRequest),
                new ParameterizedTypeReference<Result<String>>() {}
        );
        Result<String> result = response.getBody();
        if (result == null || result.getCode() != 200) {
            log.error("调用大模型服务失败");
            return null;
        }
        log.info("调用大模型服务成功");
        return  result.getData();
    }
}

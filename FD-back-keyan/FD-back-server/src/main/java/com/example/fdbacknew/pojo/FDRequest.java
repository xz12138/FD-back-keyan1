package com.example.fdbacknew.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FDRequest {
    @JsonProperty("conversation_id")
    private String conversationId;
    private String type;
    private int model;
    private String prompt;
    @JsonProperty("device_archives")
    private String deviceArchives;
    private Integer timestamp;
}

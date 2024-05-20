package com.example.fdbacknew.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmCase implements Serializable {
    private Integer id;
    private String caseName;
    private String faultName;
    private String solution;
    private String source;
}

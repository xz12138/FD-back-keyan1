package com.example.fdbacknew.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FDResponseData {
    private String msg;
    private List<History> history;
    private String prompt;
    private List<String> para;
}

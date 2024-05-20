package com.example.fdbacknew.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionReturn {
    private Integer code;
    private String msg;
    private String inputs;
    private String search_type;
}

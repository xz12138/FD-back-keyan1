package com.example.fdbacknew.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTopResponse {
    @JsonProperty("top_index")
    private List<Integer> topIndex;
    @JsonProperty("top_sim_score")
    private List<Double> topSimScore;
    @JsonProperty("top_name")
    private List<String> topName;
}

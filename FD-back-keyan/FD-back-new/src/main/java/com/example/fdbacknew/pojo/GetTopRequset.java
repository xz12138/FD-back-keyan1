package com.example.fdbacknew.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTopRequset {
    private String inputs;
    @JsonProperty("node_type")
    private String nodeType;
    @JsonProperty("choice_set")
    private Optional<Object> choiceSet;
    @JsonProperty("top_k")
    private Integer topK;
    @JsonProperty("top_threshold")
    private Double topThreshold;
}

package tech.qiantong.qknow.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMResponse {
    private boolean success;
    private String cypherQuery;
    private String answer;
    private String explanation;
    private List<Map<String, Object>> executionResult;
    private Double executionTime;
    private String errorMessage;
    private String errorCode;
} 
package tech.qiantong.qknow.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMRequest {
    private String llmName;
    private String databaseName;
    private String workflowType;
    private String inputText;
    private Map<String, Object> context;
    private Integer timeout;
} 
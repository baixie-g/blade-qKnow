package tech.qiantong.qknow.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMModel {
    private String name;
    private String status;
    private String provider;
    private String modelType;
    private Integer maxTokens;
    private Double temperature;
} 
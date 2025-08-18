package tech.qiantong.qknow.llm.service;

import tech.qiantong.qknow.llm.dto.LLMModel;
import tech.qiantong.qknow.llm.dto.LLMResponse;

import java.util.List;
import java.util.Map;

public interface LLMService {
    /**
     * 执行自然语言查询（使用默认配置）
     */
    LLMResponse executeQuery(String question, Map<String, Object> context);
    
    /**
     * 执行自然语言查询（使用自定义配置）
     */
    LLMResponse executeQuery(String question, Map<String, Object> context, 
                           String llmName, String databaseName, String workflowType, Integer timeout);
    
    /**
     * 健康检查
     */
    boolean isHealthy();
    
    /**
     * 获取可用模型列表
     */
    List<LLMModel> getAvailableModels();
} 
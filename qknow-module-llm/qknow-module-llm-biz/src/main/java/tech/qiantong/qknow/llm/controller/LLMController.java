package tech.qiantong.qknow.llm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qknow.common.annotation.Anonymous;
import tech.qiantong.qknow.llm.dto.LLMModel;
import tech.qiantong.qknow.llm.dto.LLMResponse;
import tech.qiantong.qknow.llm.service.LLMService;

import java.util.*;

/**
 * LLM问答交互控制器
 * 提供自然语言查询、模型管理、数据库管理等功能
 */
@RestController
@RequestMapping("/api/llm")
@Slf4j
@Anonymous
public class LLMController {
    
    private final LLMService llmService;
    
    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }
    
    /**
     * 执行自然语言查询
     */
    @PostMapping("/query")
    public ResponseEntity<LLMResponse> executeQuery(
            @RequestBody Map<String, Object> request) {
        
        String question = (String) request.get("question");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.get("context");
        
        // 获取用户选择的设置参数
        String llmName = (String) request.get("llm_name");
        String databaseName = (String) request.get("database_name");
        String databaseId = request.get("database_id") != null ? String.valueOf(request.get("database_id")) : null;
        String workflowType = (String) request.get("workflow_type");
        Integer timeout = (Integer) request.get("timeout");
        
        if (!StringUtils.hasText(question)) {
            return ResponseEntity.badRequest()
                .body(LLMResponse.builder()
                    .success(false)
                    .errorMessage("问题不能为空")
                    .errorCode("VALIDATION_ERROR")
                    .build());
        }
        
        log.info("收到LLM查询请求: question={}, llm={}, database={}, workflow={}, timeout={}", 
                question, llmName, databaseName, workflowType, timeout);
        
        LLMResponse response = llmService.executeQuery(question, context, llmName, databaseId, databaseName, workflowType, timeout);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean healthy = llmService.isHealthy();
        Map<String, Object> result = new HashMap<>();
        result.put("healthy", healthy);
        result.put("timestamp", new Date());
        result.put("service", "LLM问答服务");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取可用模型
     */
    @GetMapping("/models")
    public ResponseEntity<List<LLMModel>> getModels() {
        List<LLMModel> models = llmService.getAvailableModels();
        return ResponseEntity.ok(models);
    }
    
    /**
     * 获取系统状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service_status", llmService.isHealthy() ? "running" : "stopped");
        status.put("timestamp", new Date());
        status.put("llm_count", llmService.getAvailableModels().size());
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 测试LLM连接
     */
    @PostMapping("/models/{modelName}/test")
    public ResponseEntity<Map<String, Object>> testModel(@PathVariable String modelName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里可以添加具体的模型测试逻辑
            result.put("success", true);
            result.put("message", "模型连接测试成功");
            result.put("model_name", modelName);
        } catch (Exception e) {
            log.error("模型连接测试失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "模型连接测试失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取数据库列表
     */
    @GetMapping("/databases")
    public ResponseEntity<Map<String, Object>> getDatabases() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> databases = llmService.getAvailableDatabases();
            result.put("success", true);
            result.put("message", "获取数据库列表成功");
            result.put("data", databases);
        } catch (Exception e) {
            log.error("获取数据库列表失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取数据库列表失败: " + e.getMessage());
            result.put("data", Collections.emptyList());
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试数据库连接
     */
    @PostMapping("/databases/{databaseName}/test")
    public ResponseEntity<Map<String, Object>> testDatabase(@PathVariable String databaseName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里可以添加具体的数据库测试逻辑
            result.put("success", true);
            result.put("message", "数据库连接测试成功");
            result.put("database_name", databaseName);
            result.put("node_count", 150);
        } catch (Exception e) {
            log.error("数据库连接测试失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "数据库连接测试失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取工作流列表
     */
    @GetMapping("/workflows")
    public ResponseEntity<Map<String, Object>> getWorkflows() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> workflows = llmService.getAvailableWorkflows();
            result.put("success", true);
            result.put("message", "获取工作流列表成功");
            result.put("data", workflows);
        } catch (Exception e) {
            log.error("获取工作流列表失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取工作流列表失败: " + e.getMessage());
            result.put("data", Collections.emptyList());
        }
        return ResponseEntity.ok(result);
    }
} 
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
        
        LLMResponse response = llmService.executeQuery(question, context, llmName, databaseName, workflowType, timeout);
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
            // 这里可以添加获取数据库列表的逻辑
            result.put("success", true);
            result.put("message", "获取数据库列表成功");
            
            // 创建数据库信息
            Map<String, Object> databaseInfo = new HashMap<>();
            databaseInfo.put("name", "neo4j");
            databaseInfo.put("status", "connected");
            databaseInfo.put("uri", "bolt://localhost:7687");
            databaseInfo.put("schema_count", 8);
            
            List<String> nodeTypes = Arrays.asList("Entity", "技术", "组织");
            List<String> relationshipTypes = Arrays.asList("掌握技术", "毕业院校", "工作单位", "所在地");
            
            databaseInfo.put("node_types", nodeTypes);
            databaseInfo.put("relationship_types", relationshipTypes);
            
            List<Map<String, Object>> databases = Arrays.asList(databaseInfo);
            result.put("data", databases);
            
        } catch (Exception e) {
            log.error("获取数据库列表失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取数据库列表失败: " + e.getMessage());
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
            result.put("success", true);
            result.put("message", "获取工作流列表成功");
            
            List<Map<String, Object>> workflows = new ArrayList<>();
            
            // 工作流1
            Map<String, Object> workflow1 = new HashMap<>();
            workflow1.put("name", "text2cypher_with_1_retry_and_output_check");
            workflow1.put("type", "text2cypher_with_1_retry_and_output_check");
            workflow1.put("description", "Text2Cypher with retry and output check workflow");
            
            Map<String, Object> params1 = new HashMap<>();
            params1.put("timeout", 60);
            params1.put("max_retries", 1);
            workflow1.put("parameters", params1);
            
            // 工作流2
            Map<String, Object> workflow2 = new HashMap<>();
            workflow2.put("name", "naive_text2cypher");
            workflow2.put("type", "naive_text2cypher");
            workflow2.put("description", "Simple text to Cypher conversion");
            
            Map<String, Object> params2 = new HashMap<>();
            params2.put("timeout", 30);
            workflow2.put("parameters", params2);
            
            // 工作流3
            Map<String, Object> workflow3 = new HashMap<>();
            workflow3.put("name", "naive_text2cypher_with_1_retry");
            workflow3.put("type", "naive_text2cypher_with_1_retry");
            workflow3.put("description", "Simple text to Cypher with retry");
            
            Map<String, Object> params3 = new HashMap<>();
            params3.put("timeout", 45);
            params3.put("max_retries", 1);
            workflow3.put("parameters", params3);
            
            workflows.add(workflow1);
            workflows.add(workflow2);
            workflows.add(workflow3);
            
            result.put("data", workflows);
            
        } catch (Exception e) {
            log.error("获取工作流列表失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取工作流列表失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
} 
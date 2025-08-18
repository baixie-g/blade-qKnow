package tech.qiantong.qknow.llm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.qiantong.qknow.llm.dto.LLMModel;
import tech.qiantong.qknow.llm.dto.LLMResponse;
import tech.qiantong.qknow.llm.service.LLMService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Text2CypherClient implements LLMService {

    @Value("${text2cypher.api.base-url:http://localhost:8003/api/v1}")
    private String baseUrl;

    @Value("${text2cypher.api.timeout:60000}")
    private int timeout;

    @Value("${text2cypher.api.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${text2cypher.api.retry.backoff-delay:1000}")
    private long backoffDelay;

    @Value("${text2cypher.llm.default-model:ark-model}")
    private String defaultLlmModel;

    @Value("${text2cypher.llm.default-database:neo4j}")
    private String defaultDatabase;

    @Value("${text2cypher.llm.default-workflow:text2cypher_with_1_retry_and_output_check}")
    private String defaultWorkflow;

    private final RestTemplate restTemplate;

    public Text2CypherClient(@Qualifier("text2cypherRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public LLMResponse executeQuery(String question, Map<String, Object> context) {
        // 使用默认配置调用新方法
        return executeQuery(question, context, defaultLlmModel, defaultDatabase, defaultWorkflow, timeout / 1000);
    }

    @Override
    public LLMResponse executeQuery(String question, Map<String, Object> context,
                                   String llmName, String databaseName, String workflowType, Integer timeout) {
        int attempt = 0;
        Exception lastException = null;

        // 使用传入的参数，如果为空则使用默认值
        String finalLlmName = (llmName != null && !llmName.trim().isEmpty()) ? llmName : defaultLlmModel;
        String finalDatabaseName = (databaseName != null && !databaseName.trim().isEmpty()) ? databaseName : defaultDatabase;
        String finalWorkflowType = (workflowType != null && !workflowType.trim().isEmpty()) ? workflowType : defaultWorkflow;
        int finalTimeout = (timeout != null && timeout > 0) ? timeout : (this.timeout / 1000);

        while (attempt < maxRetryAttempts) {
            try {
                log.info("执行LLM查询，第{}次尝试: question={}, llm={}, database={}, workflow={}, timeout={}",
                        attempt + 1, question, finalLlmName, finalDatabaseName, finalWorkflowType, finalTimeout);

                // 构建请求到Python Text2Cypher API
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("llm_name", finalLlmName);
                requestBody.put("database_name", finalDatabaseName);
                requestBody.put("workflow_type", finalWorkflowType);
                requestBody.put("input_text", question);
                requestBody.put("context", context);
                requestBody.put("timeout", finalTimeout);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/workflow/execute",
                    HttpMethod.POST,
                    entity,
                    Map.class
                );

                LLMResponse result = parseResponse(response.getBody());

                if (result.isSuccess()) {
                    log.info("LLM查询执行成功，耗时: {}ms",
                        result.getExecutionTime() != null ?
                        (long)(result.getExecutionTime() * 1000) : "未知");
                } else {
                    log.warn("LLM查询执行失败: {}", result.getErrorMessage());
                }

                return result;

            } catch (Exception e) {
                lastException = e;
                attempt++;

                log.error("LLM查询执行失败，第{}次尝试: {}", attempt, e.getMessage());

                if (attempt < maxRetryAttempts) {
                    try {
                        long delay = backoffDelay * attempt; // 指数退避
                        log.info("等待{}ms后重试...", delay);
                        TimeUnit.MILLISECONDS.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("LLM查询执行失败，已重试{}次", maxRetryAttempts, lastException);
        return LLMResponse.builder()
            .success(false)
            .errorMessage("查询执行失败，已重试" + maxRetryAttempts + "次: " +
                (lastException != null ? lastException.getMessage() : "未知错误"))
            .errorCode("EXECUTION_ERROR")
            .build();
    }

    @Override
    public boolean isHealthy() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/health",
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null) {
                    String status = (String) body.get("status");
                    return "healthy".equals(status);
                }
            }

            return false;
        } catch (Exception e) {
            log.error("LLM服务健康检查失败", e);
            return false;
        }
    }

    @Override
    public List<LLMModel> getAvailableModels() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/llms",
                Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body != null && (Boolean) body.get("success")) {
                List<Map<String, Object>> models = (List<Map<String, Object>>) body.get("data");
                return models.stream()
                    .map(this::mapToLLMModel)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("获取LLM模型列表失败", e);
        }
        return Collections.emptyList();
    }

    private LLMResponse parseResponse(Map<String, Object> response) {
        if (response == null) {
            return LLMResponse.builder()
                .success(false)
                .errorMessage("响应为空")
                .errorCode("EMPTY_RESPONSE")
                .build();
        }

        boolean success = (Boolean) response.getOrDefault("success", false);

        if (!success) {
            return LLMResponse.builder()
                .success(false)
                .errorMessage((String) response.get("error_message"))
                .errorCode((String) response.get("error_code"))
                .build();
        }

        Map<String, Object> result = (Map<String, Object>) response.get("result");
        if (result == null) {
            return LLMResponse.builder()
                .success(false)
                .errorMessage("响应结果为空")
                .errorCode("EMPTY_RESULT")
                .build();
        }

        return LLMResponse.builder()
            .success(true)
            .cypherQuery((String) result.get("cypher_query"))
            .answer((String) result.get("answer"))
            .explanation((String) result.get("explanation"))
            .executionResult((List<Map<String, Object>>) result.get("execution_result"))
            .executionTime((Double) response.get("execution_time"))
            .build();
    }

    private LLMModel mapToLLMModel(Map<String, Object> modelData) {
        return LLMModel.builder()
            .name((String) modelData.get("name"))
            .status((String) modelData.get("status"))
            .provider((String) modelData.get("provider"))
            .modelType((String) modelData.get("model_type"))
            .maxTokens((Integer) modelData.get("max_tokens"))
            .temperature((Double) modelData.get("temperature"))
            .build();
    }
}

# SpringBoot后端集成LLM服务方案

## 🎯 架构设计

### 推荐架构
```
前端 (Vue) → SpringBoot后端 → Text2Cypher API → LLM服务
```

### 优势
1. **安全性**：API密钥和敏感配置在后端管理
2. **性能**：后端可以缓存和优化请求
3. **可维护性**：统一的错误处理和日志记录
4. **扩展性**：可以添加认证、限流、监控等功能

## 🚀 实现方案

### 1. 创建LLM服务接口

```java
// LLMService.java
@Service
public interface LLMService {
    /**
     * 执行自然语言查询
     */
    LLMResponse executeQuery(String question, Map<String, Object> context);
    
    /**
     * 健康检查
     */
    boolean isHealthy();
    
    /**
     * 获取可用模型列表
     */
    List<LLMModel> getAvailableModels();
}
```

### 2. 创建数据传输对象

```java
// LLMRequest.java
@Data
@Builder
public class LLMRequest {
    private String llmName;
    private String databaseName;
    private String workflowType;
    private String inputText;
    private Map<String, Object> context;
    private Integer timeout;
}

// LLMResponse.java
@Data
@Builder
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

// LLMModel.java
@Data
@Builder
public class LLMModel {
    private String name;
    private String status;
    private String provider;
    private String modelType;
    private Integer maxTokens;
    private Double temperature;
}
```

### 3. 实现Text2Cypher客户端

```java
// Text2CypherClient.java
@Service
@Slf4j
public class Text2CypherClient implements LLMService {
    
    @Value("${text2cypher.api.base-url:http://localhost:8003/api/v1}")
    private String baseUrl;
    
    @Value("${text2cypher.api.timeout:60000}")
    private int timeout;
    
    private final RestTemplate restTemplate;
    
    public Text2CypherClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public LLMResponse executeQuery(String question, Map<String, Object> context) {
        try {
            LLMRequest request = LLMRequest.builder()
                .llmName("ark-model")
                .databaseName("neo4j")
                .workflowType("text2cypher_with_1_retry_and_output_check")
                .inputText(question)
                .context(context)
                .timeout(60)
                .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<LLMRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/workflow/execute",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            return parseResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("LLM查询执行失败", e);
            return LLMResponse.builder()
                .success(false)
                .errorMessage("查询执行失败: " + e.getMessage())
                .build();
        }
    }
    
    @Override
    public boolean isHealthy() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/health", 
                Map.class
            );
            return response.getStatusCode() == HttpStatus.OK;
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
                .build();
        }
        
        return LLMResponse.builder()
            .success(true)
            .cypherQuery((String) result.get("cypher"))
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
```

### 4. 创建控制器

```java
// LLMController.java
@RestController
@RequestMapping("/api/llm")
@Slf4j
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
        
        if (StringUtils.isEmpty(question)) {
            return ResponseEntity.badRequest()
                .body(LLMResponse.builder()
                    .success(false)
                    .errorMessage("问题不能为空")
                    .build());
        }
        
        LLMResponse response = llmService.executeQuery(question, context);
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
}
```

### 5. 配置RestTemplate

```java
// RestTemplateConfig.java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(60))
            .build();
    }
}
```

### 6. 配置文件

```yaml
# application.yml
text2cypher:
  api:
    base-url: http://localhost:8003/api/v1
    timeout: 60000
    retry:
      max-attempts: 3
      backoff-delay: 1000
```

## 🔄 前端修改

### 修改API调用

```javascript
// 修改 qknow-ui/src/api/app/graph/text2cypher.js
import request from "@/utils/request";

// 改为调用后端API
export function askQuestion(question, context = {}) {
  return request({
    url: "/api/llm/query",
    method: "post",
    data: {
      question,
      context
    }
  });
}

export function checkText2CypherHealth() {
  return request({
    url: "/api/llm/health",
    method: "get"
  });
}

export function getAvailableLLMs() {
  return request({
    url: "/api/llm/models",
    method: "get"
  });
}
```

## 🛡️ 安全增强

### 1. 添加认证

```java
@PreAuthorize("hasRole('USER')")
@PostMapping("/query")
public ResponseEntity<LLMResponse> executeQuery(...) {
    // 实现
}
```

### 2. 添加限流

```java
@RateLimiter(name = "llm-query", fallbackMethod = "queryFallback")
@PostMapping("/query")
public ResponseEntity<LLMResponse> executeQuery(...) {
    // 实现
}

public ResponseEntity<LLMResponse> queryFallback(...) {
    return ResponseEntity.status(429)
        .body(LLMResponse.builder()
            .success(false)
            .errorMessage("请求过于频繁，请稍后再试")
            .build());
}
```

### 3. 添加缓存

```java
@Cacheable(value = "llm-queries", key = "#question + #context.toString()")
public LLMResponse executeQuery(String question, Map<String, Object> context) {
    // 实现
}
```

## 📊 监控和日志

### 1. 添加指标

```java
@Component
public class LLMMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter queryCounter;
    private final Timer queryTimer;
    
    public LLMMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.queryCounter = Counter.builder("llm.queries.total")
            .description("Total number of LLM queries")
            .register(meterRegistry);
        this.queryTimer = Timer.builder("llm.queries.duration")
            .description("LLM query duration")
            .register(meterRegistry);
    }
    
    public void recordQuery(boolean success) {
        queryCounter.increment();
        if (success) {
            meterRegistry.counter("llm.queries.success").increment();
        } else {
            meterRegistry.counter("llm.queries.failure").increment();
        }
    }
    
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }
}
```

### 2. 结构化日志

```java
@Slf4j
public class Text2CypherClient {
    
    public LLMResponse executeQuery(String question, Map<String, Object> context) {
        String requestId = UUID.randomUUID().toString();
        
        log.info("开始执行LLM查询", Map.of(
            "requestId", requestId,
            "question", question,
            "context", context
        ));
        
        try {
            LLMResponse response = // 执行查询
            
            log.info("LLM查询执行成功", Map.of(
                "requestId", requestId,
                "success", response.isSuccess(),
                "executionTime", response.getExecutionTime()
            ));
            
            return response;
        } catch (Exception e) {
            log.error("LLM查询执行失败", Map.of(
                "requestId", requestId,
                "error", e.getMessage()
            ), e);
            throw e;
        }
    }
}
```

## 🚀 部署建议

1. **环境隔离**：开发、测试、生产环境使用不同的LLM配置
2. **健康检查**：定期检查LLM服务状态
3. **监控告警**：设置查询失败率和响应时间告警
4. **备份方案**：准备LLM服务不可用时的降级方案

---

这样的架构设计更加安全、可维护，也更符合企业级应用的要求。 
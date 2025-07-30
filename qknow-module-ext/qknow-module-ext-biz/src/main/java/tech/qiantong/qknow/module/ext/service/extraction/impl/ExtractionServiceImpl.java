package tech.qiantong.qknow.module.ext.service.extraction.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.utils.StringUtils;
import tech.qiantong.qknow.module.ext.service.extraction.ExtractionService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽取服务实现类
 *
 * @author qknow
 * @date 2025-01-20
 */
@Slf4j
@Service
public class ExtractionServiceImpl implements ExtractionService {

    @Resource
    private RestTemplate restTemplate;

    @Value("${extraction.api.url:http://127.0.0.1:8000/extract}")
    private String extractionApiUrl;

    @Override
    public AjaxResult extractWithSchema(String text, String schema) throws Exception {
        if (StringUtils.isBlank(text)) {
            return AjaxResult.error("文本内容不能为空");
        }

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);
            
            // 构建schema对象
            Map<String, Object> schemaObj = new HashMap<>();
            schemaObj.put("schema", "知识抽取"); // 默认schema名称
            schemaObj.put("triplet", JSON.parseArray(schema)); // 解析schema字符串为数组
            
            requestBody.put("schema", schemaObj);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("调用抽取API，请求URL: {}", extractionApiUrl);
            log.info("请求参数: {}", JSON.toJSONString(requestBody));

            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                extractionApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                log.info("抽取API响应: {}", responseBody);
                
                // 解析响应结果
                JSONObject result = JSON.parseObject(responseBody);
                return AjaxResult.success("抽取成功", result);
            } else {
                log.error("抽取API调用失败，状态码: {}", response.getStatusCode());
                return AjaxResult.error("抽取API调用失败");
            }

        } catch (Exception e) {
            log.error("调用抽取API异常", e);
            return AjaxResult.error("抽取API调用异常: " + e.getMessage());
        }
    }

    /**
     * 根据任务配置的schema进行抽取
     *
     * @param text 文本内容
     * @param schemaList 任务配置的schema列表
     * @return 抽取结果
     */
    @Override
    public AjaxResult extractWithTaskSchema(String text, List<String> schemaList) throws Exception {
        if (StringUtils.isBlank(text)) {
            return AjaxResult.error("文本内容不能为空");
        }

        if (schemaList == null || schemaList.isEmpty()) {
            return AjaxResult.error("任务未配置抽取schema");
        }

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);
            
            // 构建schema对象
            Map<String, Object> schemaObj = new HashMap<>();
            schemaObj.put("schema", "任务抽取"); // schema名称
            schemaObj.put("triplet", schemaList); // 直接使用schema列表
            
            requestBody.put("schema", schemaObj);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("============ 开始调用8000端口抽取API ============");
            log.info("请求URL: {}", extractionApiUrl);
            log.info("请求方法: POST");
            log.info("请求头: {}", headers);
            log.info("请求体: {}", JSON.toJSONString(requestBody, true));
            log.info("文本内容: {}", text);
            log.info("Schema列表: {}", schemaList);

            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                extractionApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            log.info("============ 8000端口抽取API响应 ============");
            log.info("响应状态码: {}", response.getStatusCode());
            log.info("响应头: {}", response.getHeaders());
            log.info("响应体: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                
                // 解析响应结果
                JSONObject result = JSON.parseObject(responseBody);
                log.info("解析后的响应结果: {}", JSON.toJSONString(result, true));
                
                // 检查响应中是否包含nodes和relationships
                if (result.containsKey("nodes") && result.containsKey("relationships")) {
                    JSONArray nodes = result.getJSONArray("nodes");
                    JSONArray relationships = result.getJSONArray("relationships");
                    log.info("抽取到的实体数量: {}", nodes.size());
                    log.info("抽取到的关系数量: {}", relationships.size());
                    
                    if (nodes.size() > 0) {
                        log.info("实体详情: {}", JSON.toJSONString(nodes, true));
                    }
                    if (relationships.size() > 0) {
                        log.info("关系详情: {}", JSON.toJSONString(relationships, true));
                    }
                } else {
                    log.warn("响应结果中未找到nodes或relationships字段");
                }
                
                return AjaxResult.success("抽取成功", result);
            } else {
                log.error("抽取API调用失败，状态码: {}", response.getStatusCode());
                log.error("错误响应体: {}", response.getBody());
                return AjaxResult.error("抽取API调用失败，状态码: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("============ 调用8000端口抽取API异常 ============");
            log.error("异常类型: {}", e.getClass().getSimpleName());
            log.error("异常消息: {}", e.getMessage());
            log.error("异常堆栈: ", e);
            return AjaxResult.error("抽取API调用异常: " + e.getMessage());
        }
    }
} 
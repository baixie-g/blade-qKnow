package tech.qiantong.qknow.llm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.qiantong.qknow.llm.dto.*;
import tech.qiantong.qknow.llm.service.PromptTemplateService;

import java.util.*;

/**
 * 提示词模板服务实现类
 */
@Service
@Slf4j
public class PromptTemplateServiceImpl implements PromptTemplateService {
    
    @Value("${llm.prompts.base-url:http://localhost:8003}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public PromptTemplateServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public List<PromptType> getPromptTypes() {
        try {
            String url = baseUrl + "/api/v1/prompts/types";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> data = (List<Map<String, Object>>) body.get("data");
                    return convertToPromptTypes(data);
                }
            }
        } catch (Exception e) {
            log.error("获取提示词类型失败", e);
        }
        return Collections.emptyList();
    }
    
    @Override
    public PageResult<PromptTemplate> getTemplates(String promptType, Boolean isActive, Boolean isDefault, 
                                                 Integer page, Integer pageSize, String search) {
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl + "/api/v1/prompts/templates?");
            if (promptType != null) urlBuilder.append("prompt_type=").append(promptType).append("&");
            if (isActive != null) urlBuilder.append("is_active=").append(isActive).append("&");
            if (isDefault != null) urlBuilder.append("is_default=").append(isDefault).append("&");
            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (pageSize != null) urlBuilder.append("page_size=").append(pageSize).append("&");
            if (search != null) urlBuilder.append("search=").append(search).append("&");
            
            String url = urlBuilder.toString();
            if (url.endsWith("&")) {
                url = url.substring(0, url.length() - 1);
            }
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return convertToPageResult(body);
            }
        } catch (Exception e) {
            log.error("获取模板列表失败", e);
        }
        return PageResult.<PromptTemplate>builder()
                .templates(Collections.emptyList())
                .total(0L)
                .page(1)
                .pageSize(20)
                .totalPages(0)
                .build();
    }
    
    @Override
    public PromptTemplate getTemplateDetail(String templateId) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/detail/" + templateId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return convertToPromptTemplate(data);
                }
            }
        } catch (Exception e) {
            log.error("获取模板详情失败", e);
        }
        return null;
    }
    
    @Override
    public PromptTemplate createTemplate(CreateTemplateRequest request) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CreateTemplateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return convertToPromptTemplate(data);
                }
            }
        } catch (Exception e) {
            log.error("创建模板失败", e);
        }
        return null;
    }
    
    @Override
    public PromptTemplate updateTemplate(String templateId, UpdateTemplateRequest request) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/" + templateId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<UpdateTemplateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return convertToPromptTemplate(data);
                }
            }
        } catch (Exception e) {
            log.error("更新模板失败", e);
        }
        return null;
    }
    
    @Override
    public boolean deleteTemplate(String templateId) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/" + templateId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return Boolean.TRUE.equals(body.get("success"));
            }
        } catch (Exception e) {
            log.error("删除模板失败", e);
        }
        return false;
    }
    
    @Override
    public PromptTemplate copyTemplate(String templateId, CopyTemplateRequest request) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/" + templateId + "/copy";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CopyTemplateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return convertToPromptTemplate(data);
                }
            }
        } catch (Exception e) {
            log.error("复制模板失败", e);
        }
        return null;
    }
    
    @Override
    public PromptTemplate getDefaultTemplate(String promptType) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/default/" + promptType;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return convertToPromptTemplate(data);
                }
            }
        } catch (Exception e) {
            log.error("获取默认模板失败", e);
        }
        return null;
    }
    
    @Override
    public PageResult<PromptTemplate> getTemplatesByType(String promptType, Integer page, Integer pageSize) {
        try {
            StringBuilder urlBuilder = new StringBuilder(baseUrl + "/api/v1/prompts/templates/by-type/" + promptType + "?");
            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (pageSize != null) urlBuilder.append("page_size=").append(pageSize).append("&");
            
            String url = urlBuilder.toString();
            if (url.endsWith("&")) {
                url = url.substring(0, url.length() - 1);
            }
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return convertToPageResult(data);
                }
            }
        } catch (Exception e) {
            log.error("按类型获取模板失败", e);
        }
        return PageResult.<PromptTemplate>builder()
                .templates(Collections.emptyList())
                .total(0L)
                .page(1)
                .pageSize(20)
                .totalPages(0)
                .build();
    }
    
    @Override
    public int importTemplates(List<CreateTemplateRequest> requests) {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/import";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<List<CreateTemplateRequest>> entity = new HttpEntity<>(requests, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    return ((Number) data.get("imported_count")).intValue();
                }
            }
        } catch (Exception e) {
            log.error("批量导入模板失败", e);
        }
        return 0;
    }
    
    @Override
    public List<PromptTemplate> exportTemplates() {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/export";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> data = (List<Map<String, Object>>) body.get("data");
                    return convertToPromptTemplates(data);
                }
            }
        } catch (Exception e) {
            log.error("导出模板失败", e);
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<PromptTemplateFile> getTemplateFiles() {
        try {
            String url = baseUrl + "/api/v1/prompts/templates/files";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (Boolean.TRUE.equals(body.get("success"))) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> data = (List<Map<String, Object>>) body.get("data");
                    return convertToPromptTemplateFiles(data);
                }
            }
        } catch (Exception e) {
            log.error("获取模板文件信息失败", e);
        }
        return Collections.emptyList();
    }
    
    // 转换方法
    private List<PromptType> convertToPromptTypes(List<Map<String, Object>> data) {
        List<PromptType> types = new ArrayList<>();
        for (Map<String, Object> item : data) {
            types.add(PromptType.builder()
                    .type((String) item.get("type"))
                    .name((String) item.get("name"))
                    .description((String) item.get("description"))
                    .workflow((String) item.get("workflow"))
                    .step((String) item.get("step"))
                    .build());
        }
        return types;
    }
    
    private PromptTemplate convertToPromptTemplate(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        return PromptTemplate.builder()
                .id((String) data.get("id"))
                .name((String) data.get("name"))
                .promptType((String) data.get("prompt_type"))
                .content((String) data.get("content"))
                .description((String) data.get("description"))
                .version((String) data.get("version"))
                .isDefault((Boolean) data.get("is_default"))
                .isActive((Boolean) data.get("is_active"))
                .createdAt(parseDateTime((String) data.get("created_at")))
                .updatedAt(parseDateTime((String) data.get("updated_at")))
                .metadata((Map<String, Object>) data.get("metadata"))
                .build();
    }
    
    private List<PromptTemplate> convertToPromptTemplates(List<Map<String, Object>> data) {
        List<PromptTemplate> templates = new ArrayList<>();
        if (data != null) {
            for (Map<String, Object> item : data) {
                if (item != null) {
                    PromptTemplate template = convertToPromptTemplate(item);
                    if (template != null) {
                        templates.add(template);
                    }
                }
            }
        }
        return templates;
    }
    
    private PageResult<PromptTemplate> convertToPageResult(Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> templatesData = (List<Map<String, Object>>) data.get("templates");
        
        // 安全获取数值，提供默认值
        Long total = data.get("total") != null ? ((Number) data.get("total")).longValue() : 0L;
        Integer pageSize = data.get("page_size") != null ? ((Number) data.get("page_size")).intValue() : 20;
        Integer page = data.get("page") != null ? ((Number) data.get("page")).intValue() : 1;
        
        // 计算总页数
        Integer totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        
        return PageResult.<PromptTemplate>builder()
                .templates(convertToPromptTemplates(templatesData != null ? templatesData : new ArrayList<>()))
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
    
    private List<PromptTemplateFile> convertToPromptTemplateFiles(List<Map<String, Object>> data) {
        List<PromptTemplateFile> files = new ArrayList<>();
        for (Map<String, Object> item : data) {
            files.add(PromptTemplateFile.builder()
                    .promptType((String) item.get("prompt_type"))
                    .filePath((String) item.get("file_path"))
                    .fileExists((Boolean) item.get("file_exists"))
                    .templateCount(((Number) item.get("template_count")).intValue())
                    .name((String) item.get("name"))
                    .description((String) item.get("description"))
                    .workflow((String) item.get("workflow"))
                    .step((String) item.get("step"))
                    .build());
        }
        return files;
    }
    
    private java.time.LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        try {
            // 简单的日期时间解析，实际项目中可能需要更复杂的处理
            return java.time.LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
        } catch (Exception e) {
            log.warn("解析日期时间失败: {}", dateTimeStr);
            return null;
        }
    }
}

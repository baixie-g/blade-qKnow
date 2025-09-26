package tech.qiantong.qknow.llm.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 创建模板请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateRequest {
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 提示词类型
     */
    private String promptType;
    
    /**
     * 提示词内容
     */
    private String content;
    
    /**
     * 模板描述
     */
    private String description;
    
    /**
     * 是否默认模板
     */
    private Boolean isDefault;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
}

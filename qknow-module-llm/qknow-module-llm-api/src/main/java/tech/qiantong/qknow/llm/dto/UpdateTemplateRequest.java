package tech.qiantong.qknow.llm.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 更新模板请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateRequest {
    
    /**
     * 模板名称
     */
    private String name;
    
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
     * 是否激活
     */
    private Boolean isActive;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
}

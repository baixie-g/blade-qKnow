package tech.qiantong.qknow.llm.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 提示词模板DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {
    
    /**
     * 模板ID
     */
    private String id;
    
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
     * 版本号
     */
    private String version;
    
    /**
     * 是否默认模板
     */
    private Boolean isDefault;
    
    /**
     * 是否激活
     */
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
}

package tech.qiantong.qknow.llm.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 复制模板请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyTemplateRequest {
    
    /**
     * 新模板名称
     */
    private String newName;
    
    /**
     * 新模板描述
     */
    private String description;
}

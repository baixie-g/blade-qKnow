package tech.qiantong.qknow.llm.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 提示词类型DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptType {
    
    /**
     * 提示词类型标识
     */
    private String type;
    
    /**
     * 提示词类型名称
     */
    private String name;
    
    /**
     * 提示词类型描述
     */
    private String description;
    
    /**
     * 所属工作流
     */
    private String workflow;
    
    /**
     * 所属步骤
     */
    private String step;
}

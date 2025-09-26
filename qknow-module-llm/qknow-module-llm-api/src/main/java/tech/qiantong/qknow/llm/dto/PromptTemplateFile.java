package tech.qiantong.qknow.llm.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 提示词模板文件信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplateFile {
    
    /**
     * 提示词类型
     */
    private String promptType;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件是否存在
     */
    private Boolean fileExists;
    
    /**
     * 模板数量
     */
    private Integer templateCount;
    
    /**
     * 类型名称
     */
    private String name;
    
    /**
     * 类型描述
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

package tech.qiantong.qknow.llm.service;

import tech.qiantong.qknow.llm.dto.*;

import java.util.List;

/**
 * 提示词模板服务接口
 */
public interface PromptTemplateService {
    
    /**
     * 获取所有提示词类型
     */
    List<PromptType> getPromptTypes();
    
    /**
     * 分页获取模板列表
     */
    PageResult<PromptTemplate> getTemplates(String promptType, Boolean isActive, Boolean isDefault, 
                                          Integer page, Integer pageSize, String search);
    
    /**
     * 获取模板详情
     */
    PromptTemplate getTemplateDetail(String templateId);
    
    /**
     * 创建模板
     */
    PromptTemplate createTemplate(CreateTemplateRequest request);
    
    /**
     * 更新模板
     */
    PromptTemplate updateTemplate(String templateId, UpdateTemplateRequest request);
    
    /**
     * 删除模板
     */
    boolean deleteTemplate(String templateId);
    
    /**
     * 复制模板
     */
    PromptTemplate copyTemplate(String templateId, CopyTemplateRequest request);
    
    /**
     * 获取指定类型的默认模板
     */
    PromptTemplate getDefaultTemplate(String promptType);
    
    /**
     * 按类型分页获取模板
     */
    PageResult<PromptTemplate> getTemplatesByType(String promptType, Integer page, Integer pageSize);
    
    /**
     * 批量导入模板
     */
    int importTemplates(List<CreateTemplateRequest> requests);
    
    /**
     * 导出所有模板
     */
    List<PromptTemplate> exportTemplates();
    
    /**
     * 获取模板文件信息
     */
    List<PromptTemplateFile> getTemplateFiles();
}

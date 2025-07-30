package tech.qiantong.qknow.module.ext.service.extraction;

import tech.qiantong.qknow.common.core.domain.AjaxResult;

import java.util.List;

/**
 * 抽取服务接口
 *
 * @author qknow
 * @date 2025-01-20
 */
public interface ExtractionService {
    
    /**
     * 调用新的抽取API
     *
     * @param text 文本内容
     * @param schema 抽取schema
     * @return 抽取结果
     */
    AjaxResult extractWithSchema(String text, String schema) throws Exception;

    /**
     * 根据任务配置的schema进行抽取
     *
     * @param text 文本内容
     * @param schemaList 任务配置的schema列表
     * @return 抽取结果
     */
    AjaxResult extractWithTaskSchema(String text, List<String> schemaList) throws Exception;
} 
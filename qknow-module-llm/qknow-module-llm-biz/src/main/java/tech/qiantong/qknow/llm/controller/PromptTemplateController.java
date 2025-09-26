package tech.qiantong.qknow.llm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qknow.common.annotation.Anonymous;
import tech.qiantong.qknow.llm.dto.*;
import tech.qiantong.qknow.llm.service.PromptTemplateService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提示词模板管理控制器
 */
@RestController
@RequestMapping("/api/llm/prompts")
@Slf4j
@Anonymous
public class PromptTemplateController {
    
    private final PromptTemplateService promptTemplateService;
    
    public PromptTemplateController(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }
    
    /**
     * 获取提示词类型
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getPromptTypes() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PromptType> types = promptTemplateService.getPromptTypes();
            result.put("success", true);
            result.put("message", "获取提示词类型成功");
            result.put("data", types);
        } catch (Exception e) {
            log.error("获取提示词类型失败", e);
            result.put("success", false);
            result.put("message", "获取提示词类型失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 分页获取模板列表
     */
    @GetMapping("/templates")
    public ResponseEntity<PageResult<PromptTemplate>> getTemplates(
            @RequestParam(required = false) String promptType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String search) {
        
        try {
            PageResult<PromptTemplate> result = promptTemplateService.getTemplates(
                    promptType, isActive, isDefault, page, pageSize, search);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取模板列表失败", e);
            return ResponseEntity.ok(PageResult.<PromptTemplate>builder()
                    .templates(List.of())
                    .total(0L)
                    .page(page)
                    .pageSize(pageSize)
                    .totalPages(0)
                    .build());
        }
    }
    
    /**
     * 获取模板详情
     */
    @GetMapping("/templates/detail/{templateId}")
    public ResponseEntity<Map<String, Object>> getTemplateDetail(@PathVariable String templateId) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromptTemplate template = promptTemplateService.getTemplateDetail(templateId);
            if (template != null) {
                result.put("success", true);
                result.put("message", "获取模板详情成功");
                result.put("data", template);
            } else {
                result.put("success", false);
                result.put("message", "模板不存在");
                result.put("data", null);
            }
        } catch (Exception e) {
            log.error("获取模板详情失败", e);
            result.put("success", false);
            result.put("message", "获取模板详情失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 创建模板
     */
    @PostMapping("/templates")
    public ResponseEntity<Map<String, Object>> createTemplate(@RequestBody CreateTemplateRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromptTemplate template = promptTemplateService.createTemplate(request);
            if (template != null) {
                result.put("success", true);
                result.put("message", "模板创建成功");
                result.put("data", template);
            } else {
                result.put("success", false);
                result.put("message", "模板创建失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            log.error("创建模板失败", e);
            result.put("success", false);
            result.put("message", "创建模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新模板
     */
    @PutMapping("/templates/{templateId}")
    public ResponseEntity<Map<String, Object>> updateTemplate(
            @PathVariable String templateId,
            @RequestBody UpdateTemplateRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromptTemplate template = promptTemplateService.updateTemplate(templateId, request);
            if (template != null) {
                result.put("success", true);
                result.put("message", "模板更新成功");
                result.put("data", template);
            } else {
                result.put("success", false);
                result.put("message", "模板更新失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            log.error("更新模板失败", e);
            result.put("success", false);
            result.put("message", "更新模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除模板
     */
    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<Map<String, Object>> deleteTemplate(@PathVariable String templateId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = promptTemplateService.deleteTemplate(templateId);
            result.put("success", success);
            result.put("message", success ? "模板删除成功" : "模板删除失败");
        } catch (Exception e) {
            log.error("删除模板失败", e);
            result.put("success", false);
            result.put("message", "删除模板失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 复制模板
     */
    @PostMapping("/templates/{templateId}/copy")
    public ResponseEntity<Map<String, Object>> copyTemplate(
            @PathVariable String templateId,
            @RequestBody CopyTemplateRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromptTemplate template = promptTemplateService.copyTemplate(templateId, request);
            if (template != null) {
                result.put("success", true);
                result.put("message", "模板复制成功");
                result.put("data", template);
            } else {
                result.put("success", false);
                result.put("message", "模板复制失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            log.error("复制模板失败", e);
            result.put("success", false);
            result.put("message", "复制模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取指定类型的默认模板
     */
    @GetMapping("/templates/default/{promptType}")
    public ResponseEntity<Map<String, Object>> getDefaultTemplate(@PathVariable String promptType) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromptTemplate template = promptTemplateService.getDefaultTemplate(promptType);
            if (template != null) {
                result.put("success", true);
                result.put("message", "获取默认模板成功");
                result.put("data", template);
            } else {
                result.put("success", false);
                result.put("message", "默认模板不存在");
                result.put("data", null);
            }
        } catch (Exception e) {
            log.error("获取默认模板失败", e);
            result.put("success", false);
            result.put("message", "获取默认模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 按类型分页获取模板
     */
    @GetMapping("/templates/by-type/{promptType}")
    public ResponseEntity<Map<String, Object>> getTemplatesByType(
            @PathVariable String promptType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            PageResult<PromptTemplate> pageResult = promptTemplateService.getTemplatesByType(promptType, page, pageSize);
            result.put("success", true);
            result.put("message", "获取类型 '" + promptType + "' 的模板成功");
            result.put("data", pageResult);
        } catch (Exception e) {
            log.error("按类型获取模板失败", e);
            result.put("success", false);
            result.put("message", "按类型获取模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 批量导入模板
     */
    @PostMapping("/templates/import")
    public ResponseEntity<Map<String, Object>> importTemplates(@RequestBody List<CreateTemplateRequest> requests) {
        Map<String, Object> result = new HashMap<>();
        try {
            int importedCount = promptTemplateService.importTemplates(requests);
            result.put("success", true);
            result.put("message", "成功导入 " + importedCount + " 个模板");
            result.put("data", Map.of("imported_count", importedCount));
        } catch (Exception e) {
            log.error("批量导入模板失败", e);
            result.put("success", false);
            result.put("message", "批量导入模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 导出全部模板
     */
    @GetMapping("/templates/export")
    public ResponseEntity<Map<String, Object>> exportTemplates() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PromptTemplate> templates = promptTemplateService.exportTemplates();
            result.put("success", true);
            result.put("message", "导出 " + templates.size() + " 个模板成功");
            result.put("data", templates);
        } catch (Exception e) {
            log.error("导出模板失败", e);
            result.put("success", false);
            result.put("message", "导出模板失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取模板文件信息
     */
    @GetMapping("/templates/files")
    public ResponseEntity<Map<String, Object>> getTemplateFiles() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PromptTemplateFile> files = promptTemplateService.getTemplateFiles();
            result.put("success", true);
            result.put("message", "获取到 " + files.size() + " 个模板文件信息");
            result.put("data", files);
        } catch (Exception e) {
            log.error("获取模板文件信息失败", e);
            result.put("success", false);
            result.put("message", "获取模板文件信息失败: " + e.getMessage());
            result.put("data", null);
        }
        return ResponseEntity.ok(result);
    }
}

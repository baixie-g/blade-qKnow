package tech.qiantong.qknow.module.ext.service.extEntityPool.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolSaveReqVO;
import tech.qiantong.qknow.module.ext.convert.extEntityPool.ExtEntityPoolConvert;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extRelationshipPool.ExtRelationshipPoolDO;
import tech.qiantong.qknow.module.ext.dal.mapper.extEntityPool.ExtEntityPoolMapper;
import tech.qiantong.qknow.module.ext.service.extEntityPool.IExtEntityPoolService;
import tech.qiantong.qknow.module.ext.service.extRelationshipPool.IExtRelationshipPoolService;
import tech.qiantong.qknow.module.ext.service.neo4j.service.ExtNeo4jService;
import tech.qiantong.qknow.neo4j.domain.DynamicEntity;
import tech.qiantong.qknow.neo4j.enums.Neo4jLabelEnum;
import tech.qiantong.qknow.neo4j.repository.DynamicRepository;
import tech.qiantong.qknow.neo4j.wrapper.Neo4jBuildWrapper;
import tech.qiantong.qknow.neo4j.wrapper.Neo4jQueryWrapper;
import tech.qiantong.qknow.common.utils.spring.SpringUtils;
import org.springframework.data.neo4j.core.Neo4jTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Lazy;
import java.util.stream.Collectors;

/**
 * 实体池 Service 实现类
 *
 * @author qknow
 * @date 2025-01-20
 */
@Service
@Validated
@Slf4j
public class ExtEntityPoolServiceImpl implements IExtEntityPoolService {

    @Resource
    private ExtEntityPoolMapper extEntityPoolMapper;

    @Resource
    @Lazy
    private IExtRelationshipPoolService extRelationshipPoolService;
    
    @Resource
    private ExtNeo4jService extNeo4jService;
    
    @Resource
    private DynamicRepository dynamicRepository;
    
    @Resource
    private RestTemplate restTemplate;

    @Value("${disambiguation.api.url:http://localhost:8002/match-candidates}")
    private String disambiguationApiUrl;

    @Override
    public Long createExtEntityPool(@Valid ExtEntityPoolSaveReqVO createReqVO) {
        // 插入
        ExtEntityPoolDO extEntityPool = ExtEntityPoolConvert.INSTANCE.convert(createReqVO);
        extEntityPoolMapper.insert(extEntityPool);
        // 返回
        return extEntityPool.getId();
    }

    @Override
    public void updateExtEntityPool(@Valid ExtEntityPoolSaveReqVO updateReqVO) {
        // 校验存在
        validateExtEntityPoolExists(updateReqVO.getId());
        // 更新
        ExtEntityPoolDO updateObj = ExtEntityPoolConvert.INSTANCE.convert(updateReqVO);
        extEntityPoolMapper.updateById(updateObj);
    }

    @Override
    public void deleteExtEntityPool(Long id) {
        // 校验存在
        validateExtEntityPoolExists(id);
        // 删除
        extEntityPoolMapper.deleteById(id);
    }

    @Override
    public int removeExtEntityPool(Collection<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return 0;
        }
        // 批量删除
        return extEntityPoolMapper.deleteBatchIds(idList);
    }

    @Override
    public String importExtEntityPool(List<ExtEntityPoolRespVO> importExcelList, boolean updateSupport, String operName) {
        if (importExcelList == null || importExcelList.isEmpty()) {
            throw new RuntimeException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (ExtEntityPoolRespVO respVO : importExcelList) {
            try {
                ExtEntityPoolDO extEntityPoolDO = ExtEntityPoolConvert.INSTANCE.convert(respVO);
                Long extEntityPoolId = respVO.getId();
                if (updateSupport) {
                    if (extEntityPoolId != null) {
                        ExtEntityPoolDO existingExtEntityPool = extEntityPoolMapper.selectById(extEntityPoolId);
                        if (existingExtEntityPool != null) {
                            extEntityPoolMapper.updateById(extEntityPoolDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + extEntityPoolId + " 的实体池记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + extEntityPoolId + " 的实体池记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    extEntityPoolMapper.insert(extEntityPoolDO);
                    successNum++;
                    successMessages.add("数据插入成功，ID为 " + extEntityPoolDO.getId() + " 的实体池记录。");
                }
            } catch (Exception e) {
                failureNum++;
                String errorMsg = "数据导入失败，错误信息：" + e.getMessage();
                failureMessages.add(errorMsg);
                log.error(errorMsg, e);
            }
        }
        StringBuilder resultMsg = new StringBuilder();
        if (failureNum > 0) {
            resultMsg.append("很抱歉，导入失败！共 ").append(failureNum).append(" 条数据格式不正确，错误如下：");
            resultMsg.append("<br/>").append(String.join("<br/>", failureMessages));
            throw new RuntimeException(resultMsg.toString());
        } else {
            resultMsg.append("恭喜您，数据已全部导入成功！共 ").append(successNum).append(" 条。");
        }
        
        return resultMsg.toString();
    }

    private void validateExtEntityPoolExists(Long id) {
        if (extEntityPoolMapper.selectById(id) == null) {
            throw new RuntimeException("实体池不存在");
        }
    }

    @Override
    public ExtEntityPoolRespVO getExtEntityPool(Long id) {
        ExtEntityPoolDO extEntityPool = extEntityPoolMapper.selectById(id);
        return ExtEntityPoolConvert.INSTANCE.convert(extEntityPool);
    }

    @Override
    public PageResult<ExtEntityPoolDO> getExtEntityPoolPage(ExtEntityPoolPageReqVO pageReqVO) {
        return extEntityPoolMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ExtEntityPoolDO> getExtEntityPoolList(ExtEntityPoolPageReqVO exportReqVO) {
        return extEntityPoolMapper.selectList(exportReqVO);
    }

    @Override
    public List<ExtEntityPoolDO> getExtEntityPoolList() {
        return extEntityPoolMapper.selectList();
    }

    @Override
    public void batchSaveEntities(List<ExtEntityPoolDO> entityPoolList) {
        if (entityPoolList != null && !entityPoolList.isEmpty()) {
            extEntityPoolMapper.insertBatch(entityPoolList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult processEntity(Long id, Integer status, String remark) {
        // 校验存在
        ExtEntityPoolDO entityPool = extEntityPoolMapper.selectById(id);
        if (entityPool == null) {
            throw new RuntimeException("实体池不存在");
        }
        
        log.info("============ 开始处理实体 ============");
        log.info("实体ID: {}, 状态: {}, 备注: {}", id, status, remark);
        log.info("原始实体信息: {}", JSON.toJSONString(entityPool));
        
        // 检查实体当前状态
        if (entityPool.getStatus() != null) {
            if (entityPool.getStatus() == 1) {
                log.warn("实体ID {} 已经确认，无需重复处理", id);
                return AjaxResult.error("实体已经确认，无需重复处理");
            } else if (entityPool.getStatus() == 2) {
                log.warn("实体ID {} 已经拒绝，无法重新处理", id);
                return AjaxResult.error("实体已经拒绝，无法重新处理");
            }
        }
        
        // 更新处理状态
        ExtEntityPoolDO updateObj = new ExtEntityPoolDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        updateObj.setProcessRemark(remark);
        updateObj.setProcessTime(new Date());
        
        extEntityPoolMapper.updateById(updateObj);
        
        // 如果确认实体，则存入Neo4j并处理相关关系
        if (status == 1) { // 已确认
            try {
                // 重新查询最新的实体信息
                entityPool = extEntityPoolMapper.selectById(id);
                if (entityPool == null) {
                    throw new RuntimeException("实体池不存在");
                }
                
                // 确保实体ID不为空
                if (entityPool.getEntityId() == null || entityPool.getEntityId().trim().isEmpty()) {
                    // 如果没有实体ID，使用一个默认的ID
                    String defaultEntityId = "entity_" + entityPool.getId() + "_" + System.currentTimeMillis();
                    entityPool.setEntityId(defaultEntityId);
                    
                    // 更新数据库中的实体ID
                    ExtEntityPoolDO updateEntityIdObj = new ExtEntityPoolDO();
                    updateEntityIdObj.setId(id);
                    updateEntityIdObj.setEntityId(defaultEntityId);
                    extEntityPoolMapper.updateById(updateEntityIdObj);
                    
                    log.info("为实体生成了默认ID: {}", defaultEntityId);
                }
                
                log.info("更新后的实体信息: {}", JSON.toJSONString(entityPool));
                
                // 存入Neo4j
                saveEntityToNeo4j(entityPool);
                
                // 移除自动关系处理逻辑 - 关系应该只在关系池中手动确认时才创建
                // processRelatedRelationships(entityPool);
                
                log.info("============ 实体确认成功 ============");
                log.info("实体ID: {}, 已存入Neo4j", id);
            } catch (Exception e) {
                log.error("============ 实体确认后存入Neo4j失败 ============");
                log.error("实体ID: {}", id, e);
                throw new RuntimeException("实体确认后存入Neo4j失败: " + e.getMessage());
            }
        }
        
        return AjaxResult.success("处理成功");
    }
    
    @Override
    public AjaxResult disambiguateEntity(Long entityPoolId, Integer topK) {
        try {
            // 获取实体信息
            ExtEntityPoolDO entityPool = extEntityPoolMapper.selectById(entityPoolId);
            if (entityPool == null) {
                return AjaxResult.error("实体不存在");
            }
            
            // 构建消歧请求 - 按照正确的API格式
            Map<String, Object> requestBody = new HashMap<>();
            
            // 构建entity对象
            Map<String, Object> entity = new HashMap<>();
            entity.put("name", entityPool.getEntityName());
            entity.put("type", entityPool.getEntityType());
            
            // 添加别名（如果有）- 确保是真正的JSON数组
            if (entityPool.getAliases() != null && !entityPool.getAliases().isEmpty()) {
                // 如果aliases是字符串，需要解析为数组
                if (entityPool.getAliases() instanceof String) {
                    String aliasesStr = entityPool.getAliases();
                    try {
                        // 首先尝试解析为JSON数组
                        if (aliasesStr.trim().startsWith("[") && aliasesStr.trim().endsWith("]")) {
                            JSONArray aliasesArray = JSON.parseArray(aliasesStr);
                            entity.put("aliases", aliasesArray);
                        } else {
                            // 如果不是JSON格式，按逗号分隔处理
                            String[] aliasesArray = aliasesStr.split(",");
                            List<String> aliasesList = new ArrayList<>();
                            for (String alias : aliasesArray) {
                                String trimmedAlias = alias.trim();
                                if (!trimmedAlias.isEmpty()) {
                                    aliasesList.add(trimmedAlias);
                                }
                            }
                            entity.put("aliases", aliasesList);
                        }
                    } catch (Exception e) {
                        log.warn("解析aliases失败，使用空数组: {}", entityPool.getAliases());
                        entity.put("aliases", new ArrayList<>());
                    }
                } else {
                    entity.put("aliases", entityPool.getAliases());
                }
            }
            
            // 添加定义（如果有）
            if (entityPool.getDefinition() != null && !entityPool.getDefinition().isEmpty()) {
                entity.put("definition", entityPool.getDefinition());
            }
            
            // 添加属性（如果有）- 确保是真正的JSON对象
            if (entityPool.getAttributes() != null && !entityPool.getAttributes().isEmpty()) {
                // 如果attributes是字符串，需要解析为对象
                if (entityPool.getAttributes() instanceof String) {
                    String attributesStr = entityPool.getAttributes();
                    try {
                        // 首先尝试解析为JSON对象
                        if (attributesStr.trim().startsWith("{") && attributesStr.trim().endsWith("}")) {
                            JSONObject attributesObj = JSON.parseObject(attributesStr);
                            entity.put("attributes", attributesObj);
                        } else {
                            // 如果不是JSON格式，尝试解析为键值对格式
                            Map<String, Object> attributesMap = new HashMap<>();
                            String[] pairs = attributesStr.split(",");
                            for (String pair : pairs) {
                                String trimmedPair = pair.trim();
                                if (!trimmedPair.isEmpty() && trimmedPair.contains(":")) {
                                    String[] keyValue = trimmedPair.split(":", 2);
                                    if (keyValue.length == 2) {
                                        String key = keyValue[0].trim();
                                        String value = keyValue[1].trim();
                                        if (!key.isEmpty() && !value.isEmpty()) {
                                            attributesMap.put(key, value);
                                        }
                                    }
                                }
                            }
                            entity.put("attributes", attributesMap);
                        }
                    } catch (Exception e) {
                        log.warn("解析attributes失败，使用空对象: {}", entityPool.getAttributes());
                        entity.put("attributes", new HashMap<>());
                    }
                } else {
                    entity.put("attributes", entityPool.getAttributes());
                }
            }
            
            // 添加来源信息
            entity.put("source", "extraction-task-" + entityPool.getTaskId());
            
            // 设置entity到请求体
            requestBody.put("entity", entity);
            requestBody.put("top_k", topK != null ? topK : 5);
            requestBody.put("include_scores", true);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("============ 调用实体消歧API开始 ============");
            log.info("请求URL: {}", disambiguationApiUrl);
            log.info("请求头: {}", headers);
            log.info("请求参数: {}", JSON.toJSONString(requestBody));
            
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                disambiguationApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            log.info("============ 实体消歧API响应 ============");
            log.info("响应状态码: {}", response.getStatusCode());
            log.info("响应头: {}", response.getHeaders());
            log.info("响应体: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                
                // 解析响应结果
                JSONObject result = JSON.parseObject(responseBody);
                log.info("============ 实体消歧API调用成功 ============");
                return AjaxResult.success("消歧成功", result);
            } else {
                log.error("实体消歧API调用失败，状态码: {}", response.getStatusCode());
                return AjaxResult.error("实体消歧API调用失败");
            }
            
        } catch (Exception e) {
            log.error("============ 调用实体消歧API异常 ============");
            log.error("异常详情: ", e);
            return AjaxResult.error("实体消歧API调用异常: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirmDisambiguation(Long entityPoolId, String candidateId, String remark) {
        try {
            // 获取实体信息
            ExtEntityPoolDO entityPool = extEntityPoolMapper.selectById(entityPoolId);
            if (entityPool == null) {
                return AjaxResult.error("实体不存在");
            }
            
            // 更新实体信息（使用候选实体的信息）
            ExtEntityPoolDO updateObj = new ExtEntityPoolDO();
            updateObj.setId(entityPoolId);
            updateObj.setEntityId(candidateId);
            updateObj.setProcessRemark(remark);
            updateObj.setProcessTime(new Date());
            
            extEntityPoolMapper.updateById(updateObj);
            
            // 更新实体池对象
            entityPool.setEntityId(candidateId);
            entityPool.setProcessRemark(remark);
            entityPool.setProcessTime(new Date());
            
            // 存入Neo4j
            saveEntityToNeo4j(entityPool);
            
            // 处理相关的关系
            processRelatedRelationships(entityPool);
            
            log.info("实体消歧确认成功，实体ID: {}, 候选实体ID: {}", entityPoolId, candidateId);
            return AjaxResult.success("消歧确认成功");
            
        } catch (Exception e) {
            log.error("实体消歧确认失败，实体ID: {}", entityPoolId, e);
            throw new RuntimeException("实体消歧确认失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量处理实体（确认或拒绝）
     *
     * @param idList 实体ID列表
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    @Override
    public AjaxResult batchProcessEntities(List<Long> idList, Integer status, String remark) {
        if (idList == null || idList.isEmpty()) {
            return AjaxResult.error("请选择要处理的实体");
        }
        
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();
        List<Long> successIds = new ArrayList<>();
        List<Long> failIds = new ArrayList<>();
        
        log.info("============ 开始批量处理实体 ============");
        log.info("待处理实体数量: {}, 状态: {}, 备注: {}", idList.size(), status, remark);
        
        for (Long id : idList) {
            try {
                log.info("正在处理实体ID: {}", id);
                AjaxResult result = processEntity(id, status, remark);
                if (result.isSuccess()) {
                    successCount++;
                    successIds.add(id);
                    log.info("实体ID {} 处理成功", id);
                } else {
                    failCount++;
                    failIds.add(id);
                    String errorMsg = "实体ID " + id + ": " + result.get("msg");
                    errorMessages.add(errorMsg);
                    log.warn("实体ID {} 处理失败: {}", id, result.get("msg"));
                }
            } catch (Exception e) {
                failCount++;
                failIds.add(id);
                String errorMsg = "实体ID " + id + ": " + e.getMessage();
                errorMessages.add(errorMsg);
                log.error("批量处理实体异常，实体ID: {}", id, e);
            }
        }
        
        log.info("============ 批量处理实体完成 ============");
        log.info("成功: {} 个, 失败: {} 个", successCount, failCount);
        
        // 构建返回结果
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("totalCount", idList.size());
        resultData.put("successCount", successCount);
        resultData.put("failCount", failCount);
        resultData.put("successIds", successIds);
        resultData.put("failIds", failIds);
        resultData.put("errorMessages", errorMessages);
        
        if (failCount > 0) {
            String message = String.format("批量处理失败：%d 个实体处理失败", failCount);
            if (successCount > 0) {
                message = String.format("批量处理部分成功：%d 个成功，%d 个失败", successCount, failCount);
            }
            resultData.put("message", message);
            return AjaxResult.error(message, resultData);
        } else {
            resultData.put("message", String.format("批量处理成功：全部 %d 个实体处理成功", successCount));
            return AjaxResult.success("批量处理成功", resultData);
        }
    }
    
    /**
     * 根据实体ID查询实体池记录
     *
     * @param entityId 实体ID
     * @param taskId 任务ID
     * @return 实体池记录
     */
    @Override
    public ExtEntityPoolDO getEntityByEntityId(String entityId, Long taskId) {
        return extEntityPoolMapper.selectByEntityId(entityId, taskId);
    }
    
    /**
     * 将实体存入Neo4j
     *
     * @param entityPool 实体池对象
     */
    private void saveEntityToNeo4j(ExtEntityPoolDO entityPool) {
        try {
            log.info("============ 开始保存实体到Neo4j ============");
            log.info("实体信息: {}", JSON.toJSONString(entityPool));
            
            // 检查必要字段
            if (entityPool == null) {
                throw new RuntimeException("实体池对象为空");
            }
            
            if (entityPool.getEntityId() == null || entityPool.getEntityId().trim().isEmpty()) {
                throw new RuntimeException("实体ID为空");
            }
            
            if (entityPool.getTaskId() == null) {
                throw new RuntimeException("任务ID为空");
            }
            
            if (entityPool.getEntityName() == null || entityPool.getEntityName().trim().isEmpty()) {
                throw new RuntimeException("实体名称为空");
            }
            
            if (entityPool.getEntityType() == null || entityPool.getEntityType().trim().isEmpty()) {
                throw new RuntimeException("实体类型为空");
            }
            
            // 检查实体是否已存在
            try {
                Neo4jQueryWrapper<DynamicEntity> queryWrapper = new Neo4jQueryWrapper<>(DynamicEntity.class);
                queryWrapper.eq("id", entityPool.getEntityId());
                List<DynamicEntity> existingEntities = dynamicRepository.find(queryWrapper);
                if (!existingEntities.isEmpty()) {
                    log.warn("实体已存在，跳过创建: {}", entityPool.getEntityId());
                    return;
                }
            } catch (Exception e) {
                log.debug("检查实体存在性时出错，继续创建: {}", e.getMessage());
            }
            
            // 创建Neo4j节点
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            
            // 构建合并条件 - 只使用id字段作为唯一标识
            Map<String, Object> mergeMap = new HashMap<>();
            mergeMap.put("id", entityPool.getEntityId());
            
            log.info("合并条件: {}", JSON.toJSONString(mergeMap));
            
            // 构建属性映射 - 使用id字段而不是entity_id
            Map<String, Object> propertiesMap = new ConcurrentHashMap<>();
            propertiesMap.put("name", entityPool.getEntityName());
            propertiesMap.put("id", entityPool.getEntityId());
            propertiesMap.put("type", entityPool.getEntityType());
            propertiesMap.put("task_id", entityPool.getTaskId());
            
            // 添加可选字段（检查null值）
            if (entityPool.getDocId() != null) {
                propertiesMap.put("doc_id", entityPool.getDocId());
            }
            if (entityPool.getParagraphIndex() != null) {
                propertiesMap.put("paragraph_index", entityPool.getParagraphIndex());
            }
            if (entityPool.getWorkspaceId() != null) {
                propertiesMap.put("workspace_id", entityPool.getWorkspaceId());
            }
            
            propertiesMap.put("release_status", 1); // 已发布
            
            if (entityPool.getProcessTime() != null) {
                propertiesMap.put("process_time", entityPool.getProcessTime());
            }
            if (entityPool.getProcessBy() != null) {
                propertiesMap.put("process_by", entityPool.getProcessBy());
            }
            
            // 添加别名和属性（检查null值）
            if (entityPool.getAliases() != null) {
                propertiesMap.put("aliases", entityPool.getAliases());
            }
            if (entityPool.getDefinition() != null) {
                propertiesMap.put("definition", entityPool.getDefinition());
            }
            if (entityPool.getAttributes() != null) {
                propertiesMap.put("attributes", entityPool.getAttributes());
            }
            
            log.info("节点属性: {}", JSON.toJSONString(propertiesMap));
            
            // 创建节点 - 使用Entity标签而不是DynamicEntity
            String baseLabel = "Entity";
            String fullLabel = baseLabel + ":" + entityPool.getEntityType();
            log.info("使用标签: {}", fullLabel);
            
            // 调用Neo4j API
            dynamicRepository.mergeCreateNode(fullLabel, wrapper, mergeMap, propertiesMap);
            
            log.info("============ 实体已成功存入Neo4j ============");
            log.info("实体名称: {}", entityPool.getEntityName());
            
        } catch (Exception e) {
            log.error("============ 实体存入Neo4j失败 ============");
            log.error("实体信息: {}", JSON.toJSONString(entityPool));
            log.error("错误详情: ", e);
            throw new RuntimeException("实体存入Neo4j失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理相关的关系
     *
     * @param entityPool 实体池对象
     */
    private void processRelatedRelationships(ExtEntityPoolDO entityPool) {
        try {
            // 获取与该实体相关的所有关系
            List<ExtRelationshipPoolDO> relationships = extRelationshipPoolService.getRelationshipsByEntityId(
                entityPool.getEntityId(), entityPool.getTaskId());
            
            if (relationships.isEmpty()) {
                log.info("实体 {} 没有相关的关系需要处理", entityPool.getEntityName());
                return;
            }
            
            // 处理每个关系
            for (ExtRelationshipPoolDO relationship : relationships) {
                // 检查关系的两个实体是否都已确认
                ExtEntityPoolDO sourceEntity = getEntityByEntityId(relationship.getSourceEntityId(), relationship.getTaskId());
                ExtEntityPoolDO targetEntity = getEntityByEntityId(relationship.getTargetEntityId(), relationship.getTaskId());
                
                if (sourceEntity != null && targetEntity != null && 
                    sourceEntity.getStatus() == 1 && targetEntity.getStatus() == 1) {
                    // 两个实体都已确认，可以创建关系
                    saveRelationshipToNeo4j(relationship, sourceEntity, targetEntity);
                }
            }
            
        } catch (Exception e) {
            log.error("处理实体相关关系失败: {}", entityPool.getEntityName(), e);
            throw new RuntimeException("处理实体相关关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 将关系存入Neo4j
     *
     * @param relationship 关系池对象
     * @param sourceEntity 源实体
     * @param targetEntity 目标实体
     */
    private void saveRelationshipToNeo4j(ExtRelationshipPoolDO relationship, 
                                       ExtEntityPoolDO sourceEntity, 
                                       ExtEntityPoolDO targetEntity) {
        try {
            // 检查关系是否已存在
            try {
                // 使用简单的Cypher查询检查关系是否存在
                String checkQuery = String.format(
                    "MATCH (a:Entity {id: '%s'})-[r:%s]->(b:Entity {id: '%s'}) RETURN count(r) as count",
                    sourceEntity.getEntityId(), 
                    relationship.getRelationshipType(), 
                    targetEntity.getEntityId()
                );
                
                // 使用DynamicRepository的find方法，但使用简单的查询
                Neo4jQueryWrapper<DynamicEntity> queryWrapper = new Neo4jQueryWrapper<>(DynamicEntity.class);
                // 这里我们只是检查关系是否存在，不需要复杂的查询
                // 如果关系已存在，会在创建时失败，所以我们跳过这个检查
                log.debug("跳过关系存在性检查，直接尝试创建关系");
                
            } catch (Exception e) {
                log.debug("检查关系存在性时出错，继续创建: {}", e.getMessage());
            }
            
            // 构建关系属性
            Map<String, Object> relationshipProperties = new HashMap<>();
            relationshipProperties.put("relationship_type", relationship.getRelationshipType());
            relationshipProperties.put("task_id", relationship.getTaskId());
            relationshipProperties.put("doc_id", relationship.getDocId());
            relationshipProperties.put("paragraph_index", relationship.getParagraphIndex());
            relationshipProperties.put("workspace_id", relationship.getWorkspaceId());
            relationshipProperties.put("release_status", 1); // 已发布
            relationshipProperties.put("process_time", relationship.getProcessTime());
            relationshipProperties.put("process_by", relationship.getProcessBy());
            
            // 构建源节点和目标节点的属性映射 - 只使用id字段作为合并条件
            Map<String, Object> sourceNodeMap = new HashMap<>();
            sourceNodeMap.put("id", sourceEntity.getEntityId());
            
            Map<String, Object> targetNodeMap = new HashMap<>();
            targetNodeMap.put("id", targetEntity.getEntityId());
            
            // 创建关系 - 使用Entity标签而不是DynamicEntity
            String label = "Entity";
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            dynamicRepository.mergeRelationship(label, wrapper, sourceNodeMap, targetNodeMap, 
                relationship.getRelationshipType(), relationshipProperties);
            
            log.info("关系已成功存入Neo4j: {} -[{}]-> {}", 
                sourceEntity.getEntityName(), relationship.getRelationshipType(), targetEntity.getEntityName());
            
        } catch (Exception e) {
            log.error("关系存入Neo4j失败: {} -[{}]-> {}", 
                sourceEntity.getEntityName(), relationship.getRelationshipType(), targetEntity.getEntityName(), e);
            throw new RuntimeException("关系存入Neo4j失败: " + e.getMessage());
        }
    }

    /**
     * 获取候选实体的详细信息
     *
     * @param candidateId 候选实体ID
     * @return 候选实体详细信息
     */
    @Override
    public AjaxResult getCandidateEntityDetails(String candidateId) {
        try {
            log.info("============ 获取候选实体详细信息 ============");
            log.info("候选实体ID: {}", candidateId);
            
            // 使用Neo4j REST API查询候选实体
            String neo4jUrl = "http://localhost:7474/db/neo4j/tx/commit";
            String username = "neo4j";
            String password = "12345678";
            
            // 构建查询请求
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> statement = new HashMap<>();
            statement.put("statement", "MATCH (n:Entity) WHERE n.id = $candidateId RETURN n");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("candidateId", candidateId);
            statement.put("parameters", parameters);
            
            List<Map<String, Object>> statements = new ArrayList<>();
            statements.add(statement);
            requestBody.put("statements", statements);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(username, password);
            
            // 发送请求
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(neo4jUrl, HttpMethod.POST, requestEntity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                JSONObject result = JSON.parseObject(responseBody);
                
                // 解析结果
                JSONArray results = result.getJSONArray("results");
                if (results != null && results.size() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    JSONArray data = firstResult.getJSONArray("data");
                    
                    if (data != null && data.size() > 0) {
                        JSONObject firstData = data.getJSONObject(0);
                        JSONArray row = firstData.getJSONArray("row");
                        
                        if (row != null && row.size() > 0) {
                            JSONObject properties = row.getJSONObject(0);
                            
                            log.info("Neo4j返回的原始属性: {}", JSON.toJSONString(properties));
                            
                            // 构建候选实体信息
                            Map<String, Object> candidateInfo = new HashMap<>();
                            candidateInfo.put("id", properties.getString("id"));
                            candidateInfo.put("name", properties.getString("name"));
                            candidateInfo.put("type", properties.getString("type"));
                            
                            // 构建动态属性
                            Map<String, Object> dynamicProperties = new HashMap<>();
                            for (String key : properties.keySet()) {
                                if (!key.equals("id") && !key.equals("name") && !key.equals("type")) {
                                    Object value = properties.get(key);
                                    dynamicProperties.put(key, value);
                                    log.info("动态属性 - {}: {} (类型: {})", key, value, value != null ? value.getClass().getSimpleName() : "null");
                                }
                            }
                            candidateInfo.put("dynamicProperties", dynamicProperties);
                            
                            log.info("构建的动态属性: {}", JSON.toJSONString(dynamicProperties));
                            
                            // 获取候选实体的关系信息
                            Map<String, Object> relationships = getEntityRelationshipsFromNeo4j(candidateId);
                            candidateInfo.put("relationships", relationships);
                            
                            log.info("候选实体信息: {}", JSON.toJSONString(candidateInfo));
                            return AjaxResult.success("获取成功", candidateInfo);
                        }
                    }
                }
            }
            
            log.warn("候选实体不存在: {}", candidateId);
            return AjaxResult.error("候选实体不存在");
            
        } catch (Exception e) {
            log.error("获取候选实体详细信息失败: {}", candidateId, e);
            return AjaxResult.error("获取候选实体详细信息失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult mergeEntityInfo(Long entityPoolId, String candidateId, List<Map<String, Object>> mergeFields, String remark) {
        try {
            log.info("============ 开始合并实体信息 ============");
            log.info("实体池ID: {}, 候选实体ID: {}, 合并字段: {}", entityPoolId, candidateId, mergeFields);
            
            // 获取实体池信息
            ExtEntityPoolDO entityPool = extEntityPoolMapper.selectById(entityPoolId);
            if (entityPool == null) {
                log.error("实体池记录不存在: {}", entityPoolId);
                return AjaxResult.error("实体池记录不存在");
            }
            log.info("找到实体池记录: {}", entityPool.getEntityName());
            
            // 获取候选实体信息
            DynamicEntity candidateEntity = getCandidateEntityFromNeo4j(candidateId);
            if (candidateEntity == null) {
                log.error("候选实体不存在，ID: {}", candidateId);
                return AjaxResult.error("候选实体不存在");
            }
            log.info("找到候选实体: {}", candidateEntity.getName());
            
            // 执行智能合并
            Map<String, Object> mergedProperties = performIntelligentMerge(entityPool, candidateEntity, mergeFields);
            
            // 更新Neo4j中的候选实体
            updateNeo4jEntityWithMergeHistory(candidateId, mergedProperties, entityPool, mergeFields);
            
            // 更新实体池记录状态
            updateEntityPoolStatus(entityPoolId, candidateId, remark);
            
            log.info("============ 实体信息合并成功 ============");
            return AjaxResult.success("实体信息合并成功");
            
        } catch (Exception e) {
            log.error("实体信息合并失败: {}", entityPoolId, e);
            throw new RuntimeException("实体信息合并失败: " + e.getMessage());
        }
    }
    
    /**
     * 从Neo4j获取候选实体
     */
    private DynamicEntity getCandidateEntityFromNeo4j(String candidateId) {
        try {
            String neo4jUrl = "http://localhost:7474/db/neo4j/tx/commit";
            String username = "neo4j";
            String password = "12345678";
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> statement = new HashMap<>();
            statement.put("statement", "MATCH (n:Entity) WHERE n.id = $candidateId RETURN n");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("candidateId", candidateId);
            statement.put("parameters", parameters);
            
            List<Map<String, Object>> statements = new ArrayList<>();
            statements.add(statement);
            requestBody.put("statements", statements);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(username, password);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(neo4jUrl, HttpMethod.POST, requestEntity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JSONObject result = JSON.parseObject(response.getBody());
                JSONArray results = result.getJSONArray("results");
                if (results != null && results.size() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    JSONArray data = firstResult.getJSONArray("data");
                    
                    if (data != null && data.size() > 0) {
                        JSONObject firstData = data.getJSONObject(0);
                        JSONArray row = firstData.getJSONArray("row");
                        
                        if (row != null && row.size() > 0) {
                            JSONObject properties = row.getJSONObject(0);
                            
                            DynamicEntity entity = new DynamicEntity();
                            entity.setId(properties.getString("id"));
                            entity.setName(properties.getString("name"));
                            entity.setType(properties.getString("type"));
                            
                            Map<String, Object> dynamicProperties = new HashMap<>();
                            for (String key : properties.keySet()) {
                                if (!key.equals("id") && !key.equals("name") && !key.equals("type")) {
                                    dynamicProperties.put(key, properties.get(key));
                                }
                            }
                            entity.setDynamicProperties(dynamicProperties);
                            
                            return entity;
                        }
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取候选实体失败: {}", candidateId, e);
            return null;
        }
    }
    
    /**
     * 执行智能合并
     */
    private Map<String, Object> performIntelligentMerge(ExtEntityPoolDO entityPool, DynamicEntity candidateEntity, List<Map<String, Object>> mergeFields) {
        Map<String, Object> mergedProperties = new HashMap<>();
        
        // 复制候选实体的现有属性
        mergedProperties.putAll(candidateEntity.getDynamicProperties());
        
        // 获取当前时间戳
        long mergeTimestamp = System.currentTimeMillis();
        
        // 根据合并策略更新属性
        for (Map<String, Object> fieldConfig : mergeFields) {
            String fieldName = (String) fieldConfig.get("fieldName");
            String mergeType = (String) fieldConfig.get("mergeType");
            
            if (fieldName == null || mergeType == null) {
                continue;
            }
            
            Object fieldValue = null;
            if ("manual".equals(mergeType)) {
                fieldValue = fieldConfig.get("value");
            } else {
                // 从实体池获取值
                switch (fieldName) {
                    case "name":
                        fieldValue = entityPool.getEntityName();
                        break;
                    case "aliases":
                        fieldValue = entityPool.getAliases();
                        break;
                    case "definition":
                        fieldValue = entityPool.getDefinition();
                        break;
                    case "attributes":
                        fieldValue = entityPool.getAttributes();
                        break;
                    default:
                        log.warn("未知的合并字段: {}", fieldName);
                        continue;
                }
            }
            
            // 应用智能合并策略
            applyIntelligentMerge(mergedProperties, fieldName, fieldValue, mergeType, entityPool, mergeTimestamp);
        }
        
        return mergedProperties;
    }
    
    /**
     * 应用智能合并策略
     */
    private void applyIntelligentMerge(Map<String, Object> mergedProperties, String fieldName, Object newValue, 
                                     String mergeType, ExtEntityPoolDO entityPool, long mergeTimestamp) {
        if (newValue == null || newValue.toString().trim().isEmpty()) {
            return;
        }
        
        Object existingValue = mergedProperties.get(fieldName);
        
        switch (mergeType) {
            case "replace":
                // 直接替换
                mergedProperties.put(fieldName, newValue);
                break;
                
            case "append":
                // 追加到现有值
                if (existingValue != null && !existingValue.toString().trim().isEmpty()) {
                    if (fieldName.equals("aliases")) {
                        mergedProperties.put(fieldName, appendToArray(existingValue, newValue));
                    } else if (fieldName.equals("attributes")) {
                        mergedProperties.put(fieldName, appendToObject(existingValue, newValue));
                    } else {
                        mergedProperties.put(fieldName, existingValue + " | " + newValue);
                    }
                } else {
                    mergedProperties.put(fieldName, newValue);
                }
                break;
                
            case "merge":
                // 智能合并
                if (existingValue != null && !existingValue.toString().trim().isEmpty()) {
                    if (fieldName.equals("aliases")) {
                        mergedProperties.put(fieldName, mergeArrays(existingValue, newValue));
                    } else if (fieldName.equals("attributes")) {
                        mergedProperties.put(fieldName, mergeObjects(existingValue, newValue));
                    } else if (fieldName.equals("definition")) {
                        mergedProperties.put(fieldName, mergeDefinitions(existingValue, newValue));
                    } else {
                        mergedProperties.put(fieldName, mergeStrings(existingValue, newValue));
                    }
                } else {
                    mergedProperties.put(fieldName, newValue);
                }
                break;
                
            case "manual":
                // 手动合并：使用前端传递的值
                mergedProperties.put(fieldName, newValue);
                break;
                
            default:
                log.warn("未知的合并类型: {}", mergeType);
                break;
        }
        
        // 记录合并历史
        recordMergeHistory(mergedProperties, fieldName, newValue, entityPool, mergeTimestamp);
    }
    
    /**
     * 记录合并历史
     */
    private void recordMergeHistory(Map<String, Object> mergedProperties, String fieldName, Object newValue, 
                                  ExtEntityPoolDO entityPool, long mergeTimestamp) {
        // 获取或创建合并历史
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> mergeHistory = (List<Map<String, Object>>) mergedProperties.get("_merge_history");
        if (mergeHistory == null) {
            mergeHistory = new ArrayList<>();
            mergedProperties.put("_merge_history", mergeHistory);
        }
        
        // 添加合并记录
        Map<String, Object> mergeRecord = new HashMap<>();
        mergeRecord.put("field", fieldName);
        mergeRecord.put("value", newValue);
        mergeRecord.put("source_entity_id", entityPool.getEntityId());
        mergeRecord.put("source_entity_name", entityPool.getEntityName());
        mergeRecord.put("source_task_id", entityPool.getTaskId());
        mergeRecord.put("merge_timestamp", mergeTimestamp);
        mergeRecord.put("merge_time", new Date(mergeTimestamp));
        
        mergeHistory.add(mergeRecord);
        
        // 限制历史记录数量，避免数据过大
        if (mergeHistory.size() > 100) {
            mergeHistory.remove(0);
        }
    }
    
    /**
     * 更新Neo4j实体（包含合并历史）
     */
    private void updateNeo4jEntityWithMergeHistory(String entityId, Map<String, Object> properties, 
                                                 ExtEntityPoolDO entityPool, List<Map<String, Object>> mergeFields) {
        try {
            log.info("更新Neo4j实体: {}, 属性: {}", entityId, JSON.toJSONString(properties));
            
            // 构建更新条件
            Map<String, Object> mergeMap = new HashMap<>();
            mergeMap.put("id", entityId);
            
            // 添加合并元数据
            properties.put("_last_merge_time", new Date());
            properties.put("_merge_count", getMergeCount(properties) + 1);
            properties.put("_last_merge_source", entityPool.getEntityId());
            properties.put("_last_merge_task", entityPool.getTaskId());
            
            // 创建Neo4j节点更新
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            String label = "Entity";
            
            dynamicRepository.mergeCreateNode(label, wrapper, mergeMap, properties);
            
            log.info("Neo4j实体更新成功: {}", entityId);
            
        } catch (Exception e) {
            log.error("更新Neo4j实体失败: {}", entityId, e);
            throw new RuntimeException("更新Neo4j实体失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取合并次数
     */
    private int getMergeCount(Map<String, Object> properties) {
        Object mergeCount = properties.get("_merge_count");
        return mergeCount != null ? Integer.parseInt(mergeCount.toString()) : 0;
    }
    
    /**
     * 更新实体池状态
     */
    private void updateEntityPoolStatus(Long entityPoolId, String candidateId, String remark) {
        ExtEntityPoolDO updateObj = new ExtEntityPoolDO();
        updateObj.setId(entityPoolId);
        updateObj.setEntityId(candidateId);
        updateObj.setStatus(1); // 已确认
        updateObj.setProcessRemark(remark);
        updateObj.setProcessTime(new Date());
        
        extEntityPoolMapper.updateById(updateObj);
    }
    
    /**
     * 获取实体的关系信息
     *
     * @param entity 实体对象
     * @return 关系信息
     */
    private Map<String, Object> getEntityRelationships(DynamicEntity entity) {
        Map<String, Object> relationships = new HashMap<>();
        
        try {
            // 获取出边关系
            Neo4jQueryWrapper<DynamicEntity> queryWrapper = new Neo4jQueryWrapper<>(DynamicEntity.class);
            queryWrapper.eq("id", entity.getId());
            
            // 这里需要根据实际的Neo4j查询方式获取关系
            // 由于关系查询比较复杂，这里先返回基本信息
            relationships.put("outgoing", new ArrayList<>());
            relationships.put("incoming", new ArrayList<>());
            
        } catch (Exception e) {
            log.warn("获取实体关系信息失败: {}", entity.getId(), e);
        }
        
        return relationships;
    }
    
    /**
     * 合并实体属性
     *
     * @param entityPool 实体池对象
     * @param candidateEntity 候选实体
     * @param mergeFields 要合并的字段
     * @return 合并后的属性
     */
    private Map<String, Object> mergeEntityProperties(ExtEntityPoolDO entityPool, DynamicEntity candidateEntity, List<String> mergeFields) {
        Map<String, Object> mergedProperties = new HashMap<>();
        
        // 复制候选实体的现有属性
        mergedProperties.putAll(candidateEntity.getDynamicProperties());
        
        // 根据合并字段更新属性
        for (String field : mergeFields) {
            switch (field) {
                case "name":
                    if (entityPool.getEntityName() != null && !entityPool.getEntityName().isEmpty()) {
                        mergedProperties.put("name", entityPool.getEntityName());
                    }
                    break;
                case "aliases":
                    if (entityPool.getAliases() != null && !entityPool.getAliases().isEmpty()) {
                        mergedProperties.put("aliases", entityPool.getAliases());
                    }
                    break;
                case "definition":
                    if (entityPool.getDefinition() != null && !entityPool.getDefinition().isEmpty()) {
                        mergedProperties.put("definition", entityPool.getDefinition());
                    }
                    break;
                case "attributes":
                    if (entityPool.getAttributes() != null && !entityPool.getAttributes().isEmpty()) {
                        mergedProperties.put("attributes", entityPool.getAttributes());
                    }
                    break;
                default:
                    log.warn("未知的合并字段: {}", field);
                    break;
            }
        }
        
        // 添加来源信息
        mergedProperties.put("source", "extraction-task-" + entityPool.getTaskId());
        mergedProperties.put("merged_time", new Date());
        mergedProperties.put("merged_from_entity_id", entityPool.getEntityId());
        
        return mergedProperties;
    }
    
    /**
     * 更新Neo4j中的实体
     *
     * @param entityId 实体ID
     * @param properties 要更新的属性
     */
    private void updateNeo4jEntity(String entityId, Map<String, Object> properties) {
        try {
            log.info("更新Neo4j实体: {}, 属性: {}", entityId, JSON.toJSONString(properties));
            
            // 构建更新条件
            Map<String, Object> mergeMap = new HashMap<>();
            mergeMap.put("id", entityId);
            
            // 创建Neo4j节点更新 - 使用Entity标签而不是DynamicEntity
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            String label = "Entity";
            
            dynamicRepository.mergeCreateNode(label, wrapper, mergeMap, properties);
            
            log.info("Neo4j实体更新成功: {}", entityId);
            
        } catch (Exception e) {
            log.error("更新Neo4j实体失败: {}", entityId, e);
            throw new RuntimeException("更新Neo4j实体失败: " + e.getMessage());
        }
    }

    /**
     * 从Neo4j获取实体的关系信息
     *
     * @param entityId 实体ID
     * @return 关系信息
     */
    private Map<String, Object> getEntityRelationshipsFromNeo4j(String entityId) {
        Map<String, Object> relationships = new HashMap<>();
        
        try {
            String neo4jUrl = "http://localhost:7474/db/neo4j/tx/commit";
            String username = "neo4j";
            String password = "12345678";
            
            // 首先检查实体是否存在
            Map<String, Object> checkRequestBody = new HashMap<>();
            Map<String, Object> checkStatement = new HashMap<>();
            checkStatement.put("statement", "MATCH (n) WHERE n.id = $entityId RETURN n.id, labels(n) as labels");
            Map<String, Object> checkParams = new HashMap<>();
            checkParams.put("entityId", entityId);
            checkStatement.put("parameters", checkParams);
            
            List<Map<String, Object>> checkStatements = new ArrayList<>();
            checkStatements.add(checkStatement);
            checkRequestBody.put("statements", checkStatements);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(username, password);
            
            try {
                HttpEntity<Map<String, Object>> checkRequestEntity = new HttpEntity<>(checkRequestBody, headers);
                ResponseEntity<String> checkResponse = restTemplate.exchange(neo4jUrl, HttpMethod.POST, checkRequestEntity, String.class);
                
                log.info("实体存在性检查响应: {}", checkResponse.getBody());
                
                if (checkResponse.getStatusCode() == HttpStatus.OK && checkResponse.getBody() != null) {
                    JSONObject checkResult = JSON.parseObject(checkResponse.getBody());
                    JSONArray checkResultsArray = checkResult.getJSONArray("results");
                    if (checkResultsArray != null && checkResultsArray.size() > 0) {
                        JSONObject firstResult = checkResultsArray.getJSONObject(0);
                        JSONArray data = firstResult.getJSONArray("data");
                        if (data != null && data.size() > 0) {
                            JSONObject dataRow = data.getJSONObject(0);
                            JSONArray row = dataRow.getJSONArray("row");
                            if (row != null && row.size() > 0) {
                                String foundId = row.getString(0);
                                JSONArray labels = row.getJSONArray(1);
                                log.info("找到实体: ID={}, 标签={}", foundId, labels);
                            }
                        } else {
                            log.warn("实体 {} 不存在", entityId);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("检查实体存在性失败: {}", e.getMessage());
            }
            
            // 查询出边关系 - 只使用Entity标签
            Map<String, Object> outgoingRequestBody = new HashMap<>();
            Map<String, Object> outgoingStatement = new HashMap<>();
            outgoingStatement.put("statement", "MATCH (n:Entity)-[r]->(target:Entity) WHERE n.id = $entityId RETURN type(r) as type, target.name as targetName, target.id as targetId");
            Map<String, Object> outgoingParams = new HashMap<>();
            outgoingParams.put("entityId", entityId);
            outgoingStatement.put("parameters", outgoingParams);
            
            List<Map<String, Object>> outgoingStatements = new ArrayList<>();
            outgoingStatements.add(outgoingStatement);
            outgoingRequestBody.put("statements", outgoingStatements);
            
            // 查询入边关系 - 只使用Entity标签
            Map<String, Object> incomingRequestBody = new HashMap<>();
            Map<String, Object> incomingStatement = new HashMap<>();
            incomingStatement.put("statement", "MATCH (source:Entity)-[r]->(n:Entity) WHERE n.id = $entityId RETURN type(r) as type, source.name as sourceName, source.id as sourceId");
            Map<String, Object> incomingParams = new HashMap<>();
            incomingParams.put("entityId", entityId);
            incomingStatement.put("parameters", incomingParams);
            
            List<Map<String, Object>> incomingStatements = new ArrayList<>();
            incomingStatements.add(incomingStatement);
            incomingRequestBody.put("statements", incomingStatements);
            
            log.info("开始查询实体 {} 的关系信息", entityId);
            
            // 发送出边关系查询
            List<Map<String, Object>> outgoingResults = new ArrayList<>();
            try {
                HttpEntity<Map<String, Object>> outgoingRequestEntity = new HttpEntity<>(outgoingRequestBody, headers);
                ResponseEntity<String> outgoingResponse = restTemplate.exchange(neo4jUrl, HttpMethod.POST, outgoingRequestEntity, String.class);
                
                log.info("出边关系查询响应状态: {}", outgoingResponse.getStatusCode());
                log.info("出边关系查询响应: {}", outgoingResponse.getBody());
                
                if (outgoingResponse.getStatusCode() == HttpStatus.OK && outgoingResponse.getBody() != null) {
                    JSONObject outgoingResult = JSON.parseObject(outgoingResponse.getBody());
                    JSONArray outgoingResultsArray = outgoingResult.getJSONArray("results");
                    if (outgoingResultsArray != null && outgoingResultsArray.size() > 0) {
                        JSONObject firstResult = outgoingResultsArray.getJSONObject(0);
                        JSONArray data = firstResult.getJSONArray("data");
                        if (data != null) {
                            log.info("出边关系数据条数: {}", data.size());
                            for (int i = 0; i < data.size(); i++) {
                                try {
                                    JSONObject dataRow = data.getJSONObject(i);
                                    JSONArray row = dataRow.getJSONArray("row");
                                    if (row != null && row.size() > 0) {
                                        // 安全地解析每个字段
                                        String type = null;
                                        String targetName = null;
                                        String targetId = null;
                                        
                                        // 逐个解析字段，避免JSON解析错误
                                        if (row.size() > 0) {
                                            Object typeObj = row.get(0);
                                            type = typeObj != null ? typeObj.toString() : null;
                                        }
                                        if (row.size() > 1) {
                                            Object targetNameObj = row.get(1);
                                            targetName = targetNameObj != null ? targetNameObj.toString() : null;
                                        }
                                        if (row.size() > 2) {
                                            Object targetIdObj = row.get(2);
                                            targetId = targetIdObj != null ? targetIdObj.toString() : null;
                                        }
                                        
                                        if (type != null) {
                                            Map<String, Object> relationship = new HashMap<>();
                                            relationship.put("type", type);
                                            relationship.put("targetName", targetName);
                                            relationship.put("targetId", targetId);
                                            outgoingResults.add(relationship);
                                            log.info("添加出边关系: {} -> {}", type, targetName);
                                        }
                                    }
                                } catch (Exception e) {
                                    log.warn("解析出边关系数据行 {} 失败: {}", i, e.getMessage());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("查询出边关系失败: {}", e.getMessage(), e);
            }
            
            // 发送入边关系查询
            List<Map<String, Object>> incomingResults = new ArrayList<>();
            try {
                HttpEntity<Map<String, Object>> incomingRequestEntity = new HttpEntity<>(incomingRequestBody, headers);
                ResponseEntity<String> incomingResponse = restTemplate.exchange(neo4jUrl, HttpMethod.POST, incomingRequestEntity, String.class);
                
                log.info("入边关系查询响应状态: {}", incomingResponse.getStatusCode());
                log.info("入边关系查询响应: {}", incomingResponse.getBody());
                
                if (incomingResponse.getStatusCode() == HttpStatus.OK && incomingResponse.getBody() != null) {
                    JSONObject incomingResult = JSON.parseObject(incomingResponse.getBody());
                    JSONArray incomingResultsArray = incomingResult.getJSONArray("results");
                    if (incomingResultsArray != null && incomingResultsArray.size() > 0) {
                        JSONObject firstResult = incomingResultsArray.getJSONObject(0);
                        JSONArray data = firstResult.getJSONArray("data");
                        if (data != null) {
                            log.info("入边关系数据条数: {}", data.size());
                            for (int i = 0; i < data.size(); i++) {
                                try {
                                    JSONObject dataRow = data.getJSONObject(i);
                                    JSONArray row = dataRow.getJSONArray("row");
                                    if (row != null && row.size() > 0) {
                                        // 安全地解析每个字段
                                        String type = null;
                                        String sourceName = null;
                                        String sourceId = null;
                                        
                                        // 逐个解析字段，避免JSON解析错误
                                        if (row.size() > 0) {
                                            Object typeObj = row.get(0);
                                            type = typeObj != null ? typeObj.toString() : null;
                                        }
                                        if (row.size() > 1) {
                                            Object sourceNameObj = row.get(1);
                                            sourceName = sourceNameObj != null ? sourceNameObj.toString() : null;
                                        }
                                        if (row.size() > 2) {
                                            Object sourceIdObj = row.get(2);
                                            sourceId = sourceIdObj != null ? sourceIdObj.toString() : null;
                                        }
                                        
                                        if (type != null) {
                                            Map<String, Object> relationship = new HashMap<>();
                                            relationship.put("type", type);
                                            relationship.put("sourceName", sourceName);
                                            relationship.put("sourceId", sourceId);
                                            incomingResults.add(relationship);
                                            log.info("添加入边关系: {} -> {}", sourceName, type);
                                        }
                                    }
                                } catch (Exception e) {
                                    log.warn("解析入边关系数据行 {} 失败: {}", i, e.getMessage());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("查询入边关系失败: {}", e.getMessage(), e);
            }
            
            relationships.put("outgoing", outgoingResults);
            relationships.put("incoming", incomingResults);
            
            log.info("实体 {} 的关系查询结果: 出边 {} 个, 入边 {} 个", entityId, outgoingResults.size(), incomingResults.size());
            
        } catch (Exception e) {
            log.warn("获取实体关系信息失败: {}", entityId, e);
            relationships.put("outgoing", new ArrayList<>());
            relationships.put("incoming", new ArrayList<>());
        }
        
        return relationships;
    }

    /**
     * 根据策略合并实体属性
     *
     * @param entityPool 实体池对象
     * @param candidateEntity 候选实体
     * @param mergeFields 合并字段配置
     * @return 合并后的属性
     */
    private Map<String, Object> mergeEntityPropertiesWithStrategy(ExtEntityPoolDO entityPool, DynamicEntity candidateEntity, List<Map<String, Object>> mergeFields) {
        Map<String, Object> mergedProperties = new HashMap<>();
        
        // 复制候选实体的现有属性
        mergedProperties.putAll(candidateEntity.getDynamicProperties());
        
        // 根据合并策略更新属性
        for (Map<String, Object> fieldConfig : mergeFields) {
            String fieldName = (String) fieldConfig.get("fieldName");
            String mergeType = (String) fieldConfig.get("mergeType");
            
            if (fieldName == null || mergeType == null) {
                continue;
            }
            
            Object fieldValue = null;
            if ("manual".equals(mergeType)) {
                // 手动合并模式：使用前端传递的值
                fieldValue = fieldConfig.get("value");
            } else {
                // 自动合并模式：从实体池获取值
                switch (fieldName) {
                    case "name":
                        fieldValue = entityPool.getEntityName();
                        break;
                    case "aliases":
                        fieldValue = entityPool.getAliases();
                        break;
                    case "definition":
                        fieldValue = entityPool.getDefinition();
                        break;
                    case "attributes":
                        fieldValue = entityPool.getAttributes();
                        break;
                    default:
                        log.warn("未知的合并字段: {}", fieldName);
                        continue;
                }
            }
            
            // 应用合并策略
            mergeFieldValue(mergedProperties, fieldName, fieldValue, mergeType);
        }
        
        // 添加来源信息
        mergedProperties.put("source", "extraction-task-" + entityPool.getTaskId());
        mergedProperties.put("merged_time", new Date());
        mergedProperties.put("merged_from_entity_id", entityPool.getEntityId());
        
        return mergedProperties;
    }
    
    /**
     * 根据合并类型合并字段值
     *
     * @param mergedProperties 合并后的属性
     * @param fieldName 字段名
     * @param currentValue 当前值
     * @param mergeType 合并类型
     */
    private void mergeFieldValue(Map<String, Object> mergedProperties, String fieldName, Object currentValue, String mergeType) {
        Object existingValue = mergedProperties.get(fieldName);
        
        switch (mergeType) {
            case "current":
                // 使用当前实体值
                if (currentValue != null && !currentValue.toString().isEmpty()) {
                    mergedProperties.put(fieldName, currentValue);
                }
                break;
            case "candidate":
                // 使用候选实体值（保持现有值）
                break;
            case "merge":
                // 智能合并
                if (currentValue != null && !currentValue.toString().isEmpty()) {
                    if (existingValue == null || existingValue.toString().isEmpty()) {
                        // 候选实体值为空，使用当前值
                        mergedProperties.put(fieldName, currentValue);
                    } else if (fieldName.equals("aliases")) {
                        // 别名合并：合并两个别名列表
                        mergedProperties.put(fieldName, mergeAliases(existingValue, currentValue));
                    } else if (fieldName.equals("attributes")) {
                        // 属性合并：合并两个属性对象
                        mergedProperties.put(fieldName, mergeAttributes(existingValue, currentValue));
                    } else {
                        // 其他字段：如果不同，则合并
                        if (!existingValue.toString().equals(currentValue.toString())) {
                            mergedProperties.put(fieldName, existingValue + "; " + currentValue);
                        }
                    }
                }
                break;
            case "manual":
                // 手动合并：使用前端传递的值
                if (currentValue != null && !currentValue.toString().isEmpty()) {
                    mergedProperties.put(fieldName, currentValue);
                }
                break;
            case "skip":
                // 跳过此字段
                break;
            default:
                log.warn("未知的合并类型: {}", mergeType);
                break;
        }
    }
    
    /**
     * 合并别名
     */
    private Object mergeAliases(Object existing, Object current) {
        Set<String> aliases = new HashSet<>();
        
        // 处理现有别名
        if (existing != null) {
            if (existing instanceof String) {
                String[] existingArray = existing.toString().split(",");
                for (String alias : existingArray) {
                    if (!alias.trim().isEmpty()) {
                        aliases.add(alias.trim());
                    }
                }
            } else if (existing instanceof List) {
                for (Object item : (List<?>) existing) {
                    if (item != null && !item.toString().trim().isEmpty()) {
                        aliases.add(item.toString().trim());
                    }
                }
            }
        }
        
        // 处理当前别名
        if (current != null) {
            if (current instanceof String) {
                String[] currentArray = current.toString().split(",");
                for (String alias : currentArray) {
                    if (!alias.trim().isEmpty()) {
                        aliases.add(alias.trim());
                    }
                }
            } else if (current instanceof List) {
                for (Object item : (List<?>) current) {
                    if (item != null && !item.toString().trim().isEmpty()) {
                        aliases.add(item.toString().trim());
                    }
                }
            }
        }
        
        return new ArrayList<>(aliases);
    }
    
    /**
     * 合并属性
     */
    private Object mergeAttributes(Object existing, Object current) {
        Map<String, Object> mergedAttributes = new HashMap<>();
        
        // 处理现有属性
        if (existing != null) {
            if (existing instanceof String) {
                try {
                    JSONObject existingObj = JSON.parseObject(existing.toString());
                    mergedAttributes.putAll(existingObj);
                } catch (Exception e) {
                    log.warn("解析现有属性失败: {}", existing);
                }
            } else if (existing instanceof Map) {
                mergedAttributes.putAll((Map<String, Object>) existing);
            }
        }
        
        // 处理当前属性
        if (current != null) {
            if (current instanceof String) {
                try {
                    JSONObject currentObj = JSON.parseObject(current.toString());
                    for (String key : currentObj.keySet()) {
                        if (mergedAttributes.containsKey(key)) {
                            // 如果键已存在，合并值
                            Object existingValue = mergedAttributes.get(key);
                            Object currentValue = currentObj.get(key);
                            mergedAttributes.put(key, mergeAttributeValues(existingValue, currentValue));
                        } else {
                            mergedAttributes.put(key, currentObj.get(key));
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析当前属性失败: {}", current);
                }
            } else if (current instanceof Map) {
                Map<String, Object> currentMap = (Map<String, Object>) current;
                for (String key : currentMap.keySet()) {
                    if (mergedAttributes.containsKey(key)) {
                        // 如果键已存在，合并值
                        Object existingValue = mergedAttributes.get(key);
                        Object currentValue = currentMap.get(key);
                        mergedAttributes.put(key, mergeAttributeValues(existingValue, currentValue));
                    } else {
                        mergedAttributes.put(key, currentMap.get(key));
                    }
                }
            }
        }
        
        return mergedAttributes;
    }
    
    /**
     * 合并属性值
     */
    private Object mergeAttributeValues(Object existing, Object current) {
        if (existing instanceof List && current instanceof List) {
            Set<Object> mergedValues = new HashSet<>();
            mergedValues.addAll((List<?>) existing);
            mergedValues.addAll((List<?>) current);
            return new ArrayList<>(mergedValues);
        } else if (existing instanceof List) {
            List<Object> mergedValues = new ArrayList<>((List<?>) existing);
            if (!mergedValues.contains(current)) {
                mergedValues.add(current);
            }
            return mergedValues;
        } else if (current instanceof List) {
            List<Object> mergedValues = new ArrayList<>((List<?>) current);
            if (!mergedValues.contains(existing)) {
                mergedValues.add(existing);
            }
            return mergedValues;
        } else {
            // 如果不同，返回数组
            if (!existing.equals(current)) {
                return Arrays.asList(existing, current);
            } else {
                return existing;
            }
        }
    }

    /**
     * 追加到数组
     */
    private Object appendToArray(Object existing, Object newValue) {
        List<Object> result = new ArrayList<>();
        
        // 处理现有值
        if (existing != null) {
            if (existing instanceof List) {
                result.addAll((List<?>) existing);
            } else if (existing instanceof String) {
                try {
                    JSONArray array = JSON.parseArray(existing.toString());
                    result.addAll(array);
                } catch (Exception e) {
                    result.add(existing);
                }
            } else {
                result.add(existing);
            }
        }
        
        // 处理新值
        if (newValue != null) {
            if (newValue instanceof List) {
                result.addAll((List<?>) newValue);
            } else if (newValue instanceof String) {
                try {
                    JSONArray array = JSON.parseArray(newValue.toString());
                    result.addAll(array);
                } catch (Exception e) {
                    result.add(newValue);
                }
            } else {
                result.add(newValue);
            }
        }
        
        return result;
    }
    
    /**
     * 追加到对象
     */
    private Object appendToObject(Object existing, Object newValue) {
        Map<String, Object> result = new HashMap<>();
        
        // 处理现有值
        if (existing != null) {
            if (existing instanceof Map) {
                result.putAll((Map<String, Object>) existing);
            } else if (existing instanceof String) {
                try {
                    JSONObject obj = JSON.parseObject(existing.toString());
                    result.putAll(obj);
                } catch (Exception e) {
                    log.warn("解析现有对象失败: {}", existing);
                }
            }
        }
        
        // 处理新值
        if (newValue != null) {
            if (newValue instanceof Map) {
                result.putAll((Map<String, Object>) newValue);
            } else if (newValue instanceof String) {
                try {
                    JSONObject obj = JSON.parseObject(newValue.toString());
                    result.putAll(obj);
                } catch (Exception e) {
                    log.warn("解析新对象失败: {}", newValue);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 合并数组（去重）
     */
    private Object mergeArrays(Object existing, Object newValue) {
        Set<Object> result = new HashSet<>();
        
        // 处理现有值
        if (existing != null) {
            if (existing instanceof List) {
                result.addAll((List<?>) existing);
            } else if (existing instanceof String) {
                try {
                    JSONArray array = JSON.parseArray(existing.toString());
                    result.addAll(array);
                } catch (Exception e) {
                    result.add(existing);
                }
            } else {
                result.add(existing);
            }
        }
        
        // 处理新值
        if (newValue != null) {
            if (newValue instanceof List) {
                result.addAll((List<?>) newValue);
            } else if (newValue instanceof String) {
                try {
                    JSONArray array = JSON.parseArray(newValue.toString());
                    result.addAll(array);
                } catch (Exception e) {
                    result.add(newValue);
                }
            } else {
                result.add(newValue);
            }
        }
        
        return new ArrayList<>(result);
    }
    
    /**
     * 合并对象（智能合并属性值）
     */
    private Object mergeObjects(Object existing, Object newValue) {
        Map<String, Object> result = new HashMap<>();
        
        // 处理现有值
        if (existing != null) {
            if (existing instanceof Map) {
                result.putAll((Map<String, Object>) existing);
            } else if (existing instanceof String) {
                try {
                    JSONObject obj = JSON.parseObject(existing.toString());
                    result.putAll(obj);
                } catch (Exception e) {
                    log.warn("解析现有对象失败: {}", existing);
                }
            }
        }
        
        // 处理新值
        if (newValue != null) {
            if (newValue instanceof Map) {
                Map<String, Object> newMap = (Map<String, Object>) newValue;
                for (String key : newMap.keySet()) {
                    if (result.containsKey(key)) {
                        // 如果键已存在，合并值
                        Object existingValue = result.get(key);
                        Object currentValue = newMap.get(key);
                        result.put(key, mergeAttributeValues(existingValue, currentValue));
                    } else {
                        result.put(key, newMap.get(key));
                    }
                }
            } else if (newValue instanceof String) {
                try {
                    JSONObject obj = JSON.parseObject(newValue.toString());
                    for (String key : obj.keySet()) {
                        if (result.containsKey(key)) {
                            Object existingValue = result.get(key);
                            Object currentValue = obj.get(key);
                            result.put(key, mergeAttributeValues(existingValue, currentValue));
                        } else {
                            result.put(key, obj.get(key));
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析新对象失败: {}", newValue);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 合并定义（智能文本合并）
     */
    private Object mergeDefinitions(Object existing, Object newValue) {
        String existingStr = existing != null ? existing.toString().trim() : "";
        String newStr = newValue != null ? newValue.toString().trim() : "";
        
        if (existingStr.isEmpty()) {
            return newStr;
        }
        if (newStr.isEmpty()) {
            return existingStr;
        }
        
        // 如果其中一个包含另一个，返回更长的
        if (existingStr.contains(newStr)) {
            return existingStr;
        }
        if (newStr.contains(existingStr)) {
            return newStr;
        }
        
        // 如果都很短，用分号连接
        if (existingStr.length() < 50 && newStr.length() < 50) {
            return existingStr + "；" + newStr;
        }
        
        // 如果其中一个明显更长，使用更长的
        if (existingStr.length() > newStr.length() * 1.5) {
            return existingStr;
        }
        if (newStr.length() > existingStr.length() * 1.5) {
            return newStr;
        }
        
        // 否则用句号连接
        return existingStr + "。" + newStr;
    }
    
    /**
     * 合并字符串（智能文本合并）
     */
    private Object mergeStrings(Object existing, Object newValue) {
        String existingStr = existing != null ? existing.toString().trim() : "";
        String newStr = newValue != null ? newValue.toString().trim() : "";
        
        if (existingStr.isEmpty()) {
            return newStr;
        }
        if (newStr.isEmpty()) {
            return existingStr;
        }
        
        // 如果相同，直接返回
        if (existingStr.equals(newStr)) {
            return existingStr;
        }
        
        // 如果其中一个包含另一个，返回更长的
        if (existingStr.contains(newStr)) {
            return existingStr;
        }
        if (newStr.contains(existingStr)) {
            return newStr;
        }
        
        // 否则用分隔符连接
        return existingStr + " | " + newStr;
    }

    /**
     * 获取实体的合并历史和统计信息
     *
     * @param entityId 实体ID
     * @return 合并历史和统计信息
     */
    public Map<String, Object> getEntityMergeHistory(String entityId) {
        try {
            DynamicEntity entity = getCandidateEntityFromNeo4j(entityId);
            if (entity == null) {
                return null;
            }
            
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> properties = entity.getDynamicProperties();
            
            // 基本信息
            result.put("entity_id", entity.getId());
            result.put("entity_name", entity.getName());
            result.put("entity_type", entity.getType());
            
            // 合并统计
            result.put("merge_count", getMergeCount(properties));
            result.put("last_merge_time", properties.get("_last_merge_time"));
            result.put("last_merge_source", properties.get("_last_merge_source"));
            result.put("last_merge_task", properties.get("_last_merge_task"));
            
            // 合并历史
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mergeHistory = (List<Map<String, Object>>) properties.get("_merge_history");
            if (mergeHistory != null) {
                result.put("merge_history", mergeHistory);
                
                // 按字段统计合并次数
                Map<String, Integer> fieldMergeCount = new HashMap<>();
                for (Map<String, Object> record : mergeHistory) {
                    String field = (String) record.get("field");
                    fieldMergeCount.put(field, fieldMergeCount.getOrDefault(field, 0) + 1);
                }
                result.put("field_merge_count", fieldMergeCount);
                
                // 按来源统计合并次数
                Map<String, Integer> sourceMergeCount = new HashMap<>();
                for (Map<String, Object> record : mergeHistory) {
                    String source = (String) record.get("source_entity_name");
                    sourceMergeCount.put(source, sourceMergeCount.getOrDefault(source, 0) + 1);
                }
                result.put("source_merge_count", sourceMergeCount);
            } else {
                result.put("merge_history", new ArrayList<>());
                result.put("field_merge_count", new HashMap<>());
                result.put("source_merge_count", new HashMap<>());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("获取实体合并历史失败: {}", entityId, e);
            return null;
        }
    }
    
    /**
     * 获取实体的属性变更历史
     *
     * @param entityId 实体ID
     * @param fieldName 字段名（可选，如果为null则返回所有字段）
     * @return 属性变更历史
     */
    public List<Map<String, Object>> getEntityFieldHistory(String entityId, String fieldName) {
        try {
            DynamicEntity entity = getCandidateEntityFromNeo4j(entityId);
            if (entity == null) {
                return new ArrayList<>();
            }
            
            Map<String, Object> properties = entity.getDynamicProperties();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mergeHistory = (List<Map<String, Object>>) properties.get("_merge_history");
            
            if (mergeHistory == null) {
                return new ArrayList<>();
            }
            
            if (fieldName != null) {
                // 过滤指定字段的历史
                return mergeHistory.stream()
                    .filter(record -> fieldName.equals(record.get("field")))
                    .collect(Collectors.toList());
            } else {
                // 返回所有字段的历史
                return new ArrayList<>(mergeHistory);
            }
            
        } catch (Exception e) {
            log.error("获取实体字段历史失败: {}", entityId, e);
            return new ArrayList<>();
        }
    }
} 
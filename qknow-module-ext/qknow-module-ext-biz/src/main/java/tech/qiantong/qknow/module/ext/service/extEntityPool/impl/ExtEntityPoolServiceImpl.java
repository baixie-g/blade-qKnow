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

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Lazy;

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
                
                // 处理相关的关系
                processRelatedRelationships(entityPool);
                
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
                    try {
                        JSONArray aliasesArray = JSON.parseArray(entityPool.getAliases());
                        entity.put("aliases", aliasesArray);
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
                    try {
                        JSONObject attributesObj = JSON.parseObject(entityPool.getAttributes());
                        entity.put("attributes", attributesObj);
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
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult batchProcessEntities(List<Long> idList, Integer status, String remark) {
        if (idList == null || idList.isEmpty()) {
            return AjaxResult.error("请选择要处理的实体");
        }
        
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        for (Long id : idList) {
            try {
                AjaxResult result = processEntity(id, status, remark);
                if (result.isSuccess()) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("实体ID " + id + ": " + result.get("msg"));
                }
            } catch (Exception e) {
                failCount++;
                errorMessages.add("实体ID " + id + ": " + e.getMessage());
                log.error("批量处理实体失败，实体ID: {}", id, e);
            }
        }
        
        String message = String.format("批量处理完成：成功 %d 个，失败 %d 个", successCount, failCount);
        if (!errorMessages.isEmpty()) {
            message += "。失败详情：" + String.join("; ", errorMessages);
        }
        
        return AjaxResult.success(message);
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
            
            // 创建Neo4j节点
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            
            // 构建合并条件
            Map<String, Object> mergeMap = new HashMap<>();
            mergeMap.put("entity_id", entityPool.getEntityId());
            mergeMap.put("task_id", entityPool.getTaskId());
            
            log.info("合并条件: {}", JSON.toJSONString(mergeMap));
            
            // 构建节点属性
            Map<String, Object> propertiesMap = new ConcurrentHashMap<>();
            propertiesMap.put("name", entityPool.getEntityName());
            propertiesMap.put("entity_id", entityPool.getEntityId());
            propertiesMap.put("entity_type", entityPool.getEntityType());
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
            
            // 创建节点
            String label = Neo4jLabelEnum.DYNAMICENTITY.getLabel() + ":" + Neo4jLabelEnum.UNSTRUCTURED.getLabel();
            log.info("使用标签: {}", label);
            
            // 调用Neo4j API
            dynamicRepository.mergeCreateNode(label, wrapper, mergeMap, propertiesMap);
            
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
            
            // 构建源节点和目标节点的属性映射
            Map<String, Object> sourceNodeMap = new HashMap<>();
            sourceNodeMap.put("entity_id", sourceEntity.getEntityId());
            sourceNodeMap.put("task_id", sourceEntity.getTaskId());
            
            Map<String, Object> targetNodeMap = new HashMap<>();
            targetNodeMap.put("entity_id", targetEntity.getEntityId());
            targetNodeMap.put("task_id", targetEntity.getTaskId());
            
            // 创建关系
            String label = Neo4jLabelEnum.DYNAMICENTITY.getLabel() + ":" + Neo4jLabelEnum.UNSTRUCTURED.getLabel();
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
} 
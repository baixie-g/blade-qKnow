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
            throw new RuntimeException("导入数据不能为空");
        }
        
        StringBuilder resultMsg = new StringBuilder();
        int successNum = 0;
        int failureNum = 0;
        
        for (ExtEntityPoolRespVO data : importExcelList) {
            try {
                // 验证数据
                if (data.getEntityName() == null || data.getEntityName().trim().isEmpty()) {
                    failureNum++;
                    resultMsg.append("<br/>第 ").append(failureNum).append(" 条数据格式不正确");
                    continue;
                }
                
                // 检查是否存在
                ExtEntityPoolPageReqVO queryVO = new ExtEntityPoolPageReqVO();
                queryVO.setEntityName(data.getEntityName());
                queryVO.setTaskId(data.getTaskId());
                List<ExtEntityPoolDO> existingList = extEntityPoolMapper.selectList(queryVO);
                
                if (!existingList.isEmpty()) {
                    if (updateSupport) {
                        // 更新
                        ExtEntityPoolDO updateObj = new ExtEntityPoolDO();
                        updateObj.setId(existingList.get(0).getId());
                        updateObj.setWorkspaceId(data.getWorkspaceId());
                        updateObj.setTaskId(data.getTaskId());
                        updateObj.setDocId(data.getDocId());
                        updateObj.setParagraphIndex(data.getParagraphIndex());
                        updateObj.setEntityId(data.getEntityId());
                        updateObj.setEntityName(data.getEntityName());
                        updateObj.setEntityType(data.getEntityType());
                        updateObj.setAliases(data.getAliases());
                        updateObj.setDefinition(data.getDefinition());
                        updateObj.setAttributes(data.getAttributes());
                        updateObj.setStatus(data.getStatus());
                        updateObj.setProcessRemark(data.getProcessRemark());
                        updateObj.setUpdateBy(operName);
                        updateObj.setUpdateTime(new Date());
                        extEntityPoolMapper.updateById(updateObj);
                        successNum++;
                        resultMsg.append("<br/>第 ").append(successNum).append(" 条数据更新成功");
                    } else {
                        failureNum++;
                        resultMsg.append("<br/>第 ").append(failureNum).append(" 条数据已存在");
                    }
                } else {
                    // 新增
                    ExtEntityPoolDO insertObj = new ExtEntityPoolDO();
                    insertObj.setWorkspaceId(data.getWorkspaceId());
                    insertObj.setTaskId(data.getTaskId());
                    insertObj.setDocId(data.getDocId());
                    insertObj.setParagraphIndex(data.getParagraphIndex());
                    insertObj.setEntityId(data.getEntityId());
                    insertObj.setEntityName(data.getEntityName());
                    insertObj.setEntityType(data.getEntityType());
                    insertObj.setAliases(data.getAliases());
                    insertObj.setDefinition(data.getDefinition());
                    insertObj.setAttributes(data.getAttributes());
                    insertObj.setStatus(data.getStatus());
                    insertObj.setProcessRemark(data.getProcessRemark());
                    insertObj.setCreateBy(operName);
                    insertObj.setCreateTime(new Date());
                    extEntityPoolMapper.insert(insertObj);
                    successNum++;
                    resultMsg.append("<br/>第 ").append(successNum).append(" 条数据导入成功");
                }
            } catch (Exception e) {
                failureNum++;
                resultMsg.append("<br/>第 ").append(failureNum).append(" 条数据导入失败：").append(e.getMessage());
                log.error("导入实体池数据失败", e);
            }
        }
        
        if (failureNum > 0) {
            resultMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
        } else {
            resultMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
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
                // 存入Neo4j
                saveEntityToNeo4j(entityPool);
                
                // 处理相关的关系
                processRelatedRelationships(entityPool);
                
                log.info("实体确认成功，已存入Neo4j，实体ID: {}", id);
            } catch (Exception e) {
                log.error("实体确认后存入Neo4j失败，实体ID: {}", id, e);
                throw new RuntimeException("实体确认后存入Neo4j失败: " + e.getMessage());
            }
        }
        
        return AjaxResult.success("处理成功");
    }
    
    @Override
    public AjaxResult disambiguateEntity(Long entityPoolId, Integer topK) {
        try {
            // 获取实体池信息
            ExtEntityPoolDO entityPool = extEntityPoolMapper.selectById(entityPoolId);
            if (entityPool == null) {
                return AjaxResult.error("实体池不存在");
            }
            
            // 构建请求体，适配消歧API格式
            Map<String, Object> requestBody = new HashMap<>();
            
            // 构建entity对象
            Map<String, Object> entity = new HashMap<>();
            entity.put("name", entityPool.getEntityName());
            entity.put("type", entityPool.getEntityType());
            entity.put("definition", entityPool.getDefinition() != null ? entityPool.getDefinition() : "");
            entity.put("source", "文档抽取");
            
            // 处理别名
            List<String> aliases = new ArrayList<>();
            if (entityPool.getAliases() != null && !entityPool.getAliases().isEmpty()) {
                try {
                    JSONArray aliasesArray = JSON.parseArray(entityPool.getAliases());
                    for (int i = 0; i < aliasesArray.size(); i++) {
                        aliases.add(aliasesArray.getString(i));
                    }
                } catch (Exception e) {
                    log.warn("解析实体别名失败: {}", entityPool.getAliases());
                }
            }
            entity.put("aliases", aliases);
            
            // 处理属性
            Map<String, Object> attributes = new HashMap<>();
            if (entityPool.getAttributes() != null && !entityPool.getAttributes().isEmpty()) {
                try {
                    JSONObject attributesObj = JSON.parseObject(entityPool.getAttributes());
                    attributes.putAll(attributesObj);
                } catch (Exception e) {
                    log.warn("解析实体属性失败: {}", entityPool.getAttributes());
                }
            }
            entity.put("attributes", attributes);
            
            requestBody.put("entity", entity);
            requestBody.put("include_scores", true);
            requestBody.put("top_k", topK != null ? topK : 5);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("调用实体消歧API，请求URL: {}", disambiguationApiUrl);
            log.info("请求参数: {}", JSON.toJSONString(requestBody));

            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                disambiguationApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                log.info("实体消歧API响应: {}", responseBody);
                
                // 解析响应结果
                JSONObject result = JSON.parseObject(responseBody);
                return AjaxResult.success("消歧成功", result);
            } else {
                log.error("实体消歧API调用失败，状态码: {}", response.getStatusCode());
                return AjaxResult.error("实体消歧API调用失败");
            }

        } catch (Exception e) {
            log.error("调用实体消歧API异常", e);
            return AjaxResult.error("实体消歧API调用异常: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirmDisambiguation(Long entityPoolId, String candidateId, String remark) {
        try {
            // 获取实体池信息
            ExtEntityPoolDO entityPool = extEntityPoolMapper.selectById(entityPoolId);
            if (entityPool == null) {
                return AjaxResult.error("实体池不存在");
            }
            
            // 更新实体信息（使用候选实体的信息）
            ExtEntityPoolDO updateObj = new ExtEntityPoolDO();
            updateObj.setId(entityPoolId);
            updateObj.setEntityId(candidateId); // 使用候选实体的ID
            updateObj.setProcessRemark(remark != null ? remark : "消歧确认");
            updateObj.setProcessTime(new Date());
            updateObj.setStatus(1); // 已确认
            
            extEntityPoolMapper.updateById(updateObj);
            
            // 存入Neo4j
            saveEntityToNeo4j(entityPool);
            
            // 处理相关的关系
            processRelatedRelationships(entityPool);
            
            log.info("消歧确认成功，实体ID: {}, 候选实体ID: {}", entityPoolId, candidateId);
            return AjaxResult.success("消歧确认成功");
            
        } catch (Exception e) {
            log.error("消歧确认失败，实体ID: {}", entityPoolId, e);
            throw new RuntimeException("消歧确认失败: " + e.getMessage());
        }
    }
    
    /**
     * 将实体存入Neo4j
     *
     * @param entityPool 实体池对象
     */
    private void saveEntityToNeo4j(ExtEntityPoolDO entityPool) {
        try {
            // 创建Neo4j节点
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            
            // 构建合并条件
            Map<String, Object> mergeMap = new HashMap<>();
            mergeMap.put("entity_id", entityPool.getEntityId());
            mergeMap.put("task_id", entityPool.getTaskId());
            
            // 构建节点属性
            Map<String, Object> propertiesMap = new ConcurrentHashMap<>();
            propertiesMap.put("name", entityPool.getEntityName());
            propertiesMap.put("entity_id", entityPool.getEntityId());
            propertiesMap.put("entity_type", entityPool.getEntityType());
            propertiesMap.put("task_id", entityPool.getTaskId());
            propertiesMap.put("doc_id", entityPool.getDocId());
            propertiesMap.put("paragraph_index", entityPool.getParagraphIndex());
            propertiesMap.put("workspace_id", entityPool.getWorkspaceId());
            propertiesMap.put("release_status", 1); // 已发布
            propertiesMap.put("process_time", entityPool.getProcessTime());
            propertiesMap.put("process_by", entityPool.getProcessBy());
            
            // 添加别名和属性
            if (entityPool.getAliases() != null) {
                propertiesMap.put("aliases", entityPool.getAliases());
            }
            if (entityPool.getDefinition() != null) {
                propertiesMap.put("definition", entityPool.getDefinition());
            }
            if (entityPool.getAttributes() != null) {
                propertiesMap.put("attributes", entityPool.getAttributes());
            }
            
            // 创建节点
            String label = Neo4jLabelEnum.DYNAMICENTITY.getLabel() + ":" + Neo4jLabelEnum.UNSTRUCTURED.getLabel();
            dynamicRepository.mergeCreateNode(label, wrapper, mergeMap, propertiesMap);
            
            log.info("实体已成功存入Neo4j: {}", entityPool.getEntityName());
            
        } catch (Exception e) {
            log.error("存入Neo4j失败，实体: {}", entityPool.getEntityName(), e);
            throw e;
        }
    }
    
    /**
     * 处理相关的关系
     *
     * @param entityPool 实体池对象
     */
    private void processRelatedRelationships(ExtEntityPoolDO entityPool) {
        try {
            // 查询与该实体相关的关系
            List<ExtRelationshipPoolDO> relatedRelationships = extRelationshipPoolService.getRelationshipsByEntityId(
                entityPool.getEntityId(), entityPool.getTaskId());
            
            for (ExtRelationshipPoolDO relationship : relatedRelationships) {
                // 检查关系的两个实体是否都已确认
                ExtEntityPoolDO sourceEntity = extEntityPoolMapper.selectByEntityId(relationship.getSourceEntityId(), entityPool.getTaskId());
                ExtEntityPoolDO targetEntity = extEntityPoolMapper.selectByEntityId(relationship.getTargetEntityId(), entityPool.getTaskId());
                
                if (sourceEntity != null && targetEntity != null && 
                    sourceEntity.getStatus() == 1 && targetEntity.getStatus() == 1) {
                    // 两个实体都已确认，创建关系
                    saveRelationshipToNeo4j(relationship, sourceEntity, targetEntity);
                    
                    // 更新关系状态为已确认
                    extRelationshipPoolService.processRelationship(relationship.getId(), 1, "实体确认后自动处理");
                }
            }
            
        } catch (Exception e) {
            log.error("处理相关关系失败，实体ID: {}", entityPool.getEntityId(), e);
            throw e;
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
            // 构建源节点查询条件
            Map<String, Object> sourceMergeMap = new HashMap<>();
            sourceMergeMap.put("entity_id", sourceEntity.getEntityId());
            sourceMergeMap.put("task_id", sourceEntity.getTaskId());
            
            // 构建目标节点查询条件
            Map<String, Object> targetMergeMap = new HashMap<>();
            targetMergeMap.put("entity_id", targetEntity.getEntityId());
            targetMergeMap.put("task_id", targetEntity.getTaskId());
            
            // 构建关系属性
            Map<String, Object> relProperties = new ConcurrentHashMap<>();
            relProperties.put("task_id", relationship.getTaskId());
            relProperties.put("doc_id", relationship.getDocId());
            relProperties.put("paragraph_index", relationship.getParagraphIndex());
            relProperties.put("workspace_id", relationship.getWorkspaceId());
            relProperties.put("release_status", 1); // 已发布
            
            // 创建关系
            String label = Neo4jLabelEnum.UNSTRUCTURED.getLabel();
            dynamicRepository.mergeRelationship(label, 
                new Neo4jBuildWrapper<>(DynamicEntity.class), 
                sourceMergeMap, 
                targetMergeMap, 
                relationship.getRelationshipType(), 
                relProperties);
            
            log.info("关系已成功存入Neo4j: {} -> {} -> {}", 
                sourceEntity.getEntityName(), 
                relationship.getRelationshipType(), 
                targetEntity.getEntityName());
            
        } catch (Exception e) {
            log.error("存入Neo4j关系失败: {} -> {} -> {}", 
                sourceEntity.getEntityName(), 
                relationship.getRelationshipType(), 
                targetEntity.getEntityName(), e);
            throw e;
        }
    }
} 
package tech.qiantong.qknow.module.ext.service.extRelationshipPool.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.page.PageParam;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.common.core.page.TableDataInfo;
import tech.qiantong.qknow.common.exception.ServiceException;
import tech.qiantong.qknow.mybatis.core.query.LambdaQueryWrapperX;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolSaveReqVO;
import tech.qiantong.qknow.module.ext.convert.extRelationshipPool.ExtRelationshipPoolConvert;
import tech.qiantong.qknow.module.ext.dal.dataobject.extRelationshipPool.ExtRelationshipPoolDO;
import tech.qiantong.qknow.module.ext.dal.mapper.extRelationshipPool.ExtRelationshipPoolMapper;
import tech.qiantong.qknow.module.ext.service.extRelationshipPool.IExtRelationshipPoolService;
import tech.qiantong.qknow.module.ext.service.extEntityPool.IExtEntityPoolService;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;
import tech.qiantong.qknow.module.ext.service.neo4j.service.ExtNeo4jService;
import tech.qiantong.qknow.neo4j.domain.DynamicEntity;
import tech.qiantong.qknow.neo4j.enums.Neo4jLabelEnum;
import tech.qiantong.qknow.neo4j.repository.DynamicRepository;
import tech.qiantong.qknow.neo4j.wrapper.Neo4jBuildWrapper;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static tech.qiantong.qknow.module.ext.enums.ErrorCodeConstants.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.context.annotation.Lazy;

/**
 * 关系池 Service 实现类
 *
 * @author qknow
 * @date 2025-01-20
 */
@Service
@Validated
@Slf4j
public class ExtRelationshipPoolServiceImpl implements IExtRelationshipPoolService {

    @Resource
    private ExtRelationshipPoolMapper extRelationshipPoolMapper;

    @Resource
    @Lazy
    private IExtEntityPoolService extEntityPoolService;
    
    @Resource
    private ExtNeo4jService extNeo4jService;
    
    @Resource
    private DynamicRepository dynamicRepository;

    @Override
    public Long createExtRelationshipPool(@Valid ExtRelationshipPoolSaveReqVO createReqVO) {
        // 插入
        ExtRelationshipPoolDO extRelationshipPool = ExtRelationshipPoolConvert.INSTANCE.convert(createReqVO);
        extRelationshipPoolMapper.insert(extRelationshipPool);
        // 返回
        return extRelationshipPool.getId();
    }

    @Override
    public void updateExtRelationshipPool(@Valid ExtRelationshipPoolSaveReqVO updateReqVO) {
        // 校验存在
        validateExtRelationshipPoolExists(updateReqVO.getId());
        // 更新
        ExtRelationshipPoolDO updateObj = ExtRelationshipPoolConvert.INSTANCE.convert(updateReqVO);
        extRelationshipPoolMapper.updateById(updateObj);
    }

    @Override
    public void deleteExtRelationshipPool(Long id) {
        // 校验存在
        validateExtRelationshipPoolExists(id);
        // 删除
        extRelationshipPoolMapper.deleteById(id);
    }

    @Override
    public int removeExtRelationshipPool(Collection<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return 0;
        }
        // 批量删除
        return extRelationshipPoolMapper.deleteBatchIds(idList);
    }

    @Override
    public String importExtRelationshipPool(List<ExtRelationshipPoolRespVO> importExcelList, boolean updateSupport, String operName) {
        if (importExcelList == null || importExcelList.isEmpty()) {
            throw new RuntimeException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (ExtRelationshipPoolRespVO respVO : importExcelList) {
            try {
                ExtRelationshipPoolDO extRelationshipPoolDO = ExtRelationshipPoolConvert.INSTANCE.convert(respVO);
                Long extRelationshipPoolId = respVO.getId();
                if (updateSupport) {
                    if (extRelationshipPoolId != null) {
                        ExtRelationshipPoolDO existingExtRelationshipPool = extRelationshipPoolMapper.selectById(extRelationshipPoolId);
                        if (existingExtRelationshipPool != null) {
                            extRelationshipPoolMapper.updateById(extRelationshipPoolDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + extRelationshipPoolId + " 的关系池记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + extRelationshipPoolId + " 的关系池记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    extRelationshipPoolMapper.insert(extRelationshipPoolDO);
                    successNum++;
                    successMessages.add("数据插入成功，ID为 " + extRelationshipPoolDO.getId() + " 的关系池记录。");
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

    private void validateExtRelationshipPoolExists(Long id) {
        if (extRelationshipPoolMapper.selectById(id) == null) {
            throw new ServiceException("关系池不存在");
        }
    }

    @Override
    public ExtRelationshipPoolDO getExtRelationshipPool(Long id) {
        return extRelationshipPoolMapper.selectById(id);
    }

    @Override
    public PageResult<ExtRelationshipPoolRespVO> getExtRelationshipPoolPage(ExtRelationshipPoolPageReqVO pageReqVO) {
        PageResult<ExtRelationshipPoolDO> pageResult = extRelationshipPoolMapper.selectPage(pageReqVO);
        return new PageResult<>(ExtRelationshipPoolConvert.INSTANCE.convertList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public List<ExtRelationshipPoolDO> getExtRelationshipPoolList(ExtRelationshipPoolPageReqVO exportReqVO) {
        // 构造查询条件
        LambdaQueryWrapperX<ExtRelationshipPoolDO> queryWrapper = new LambdaQueryWrapperX<ExtRelationshipPoolDO>()
                .eqIfPresent(ExtRelationshipPoolDO::getWorkspaceId, exportReqVO.getWorkspaceId())
                .eqIfPresent(ExtRelationshipPoolDO::getTaskId, exportReqVO.getTaskId())
                .eqIfPresent(ExtRelationshipPoolDO::getDocId, exportReqVO.getDocId())
                .likeIfPresent(ExtRelationshipPoolDO::getSourceEntityId, exportReqVO.getSourceEntityId())
                .likeIfPresent(ExtRelationshipPoolDO::getTargetEntityId, exportReqVO.getTargetEntityId())
                .likeIfPresent(ExtRelationshipPoolDO::getRelationshipType, exportReqVO.getRelationshipType())
                .eqIfPresent(ExtRelationshipPoolDO::getStatus, exportReqVO.getStatus())
                .betweenIfPresent(ExtRelationshipPoolDO::getCreateTime, exportReqVO.getCreateTimeStart(), exportReqVO.getCreateTimeEnd());
        
        return extRelationshipPoolMapper.selectList(queryWrapper);
    }

    @Override
    public void batchSaveRelationships(List<ExtRelationshipPoolDO> relationshipPoolList) {
        if (relationshipPoolList != null && !relationshipPoolList.isEmpty()) {
            extRelationshipPoolMapper.insertBatch(relationshipPoolList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult processRelationship(Long id, Integer status, String remark) {
        // 校验存在
        ExtRelationshipPoolDO relationshipPool = validateExtRelationshipPoolExistsAndReturn(id);
        
        // 更新处理状态
        ExtRelationshipPoolDO updateObj = new ExtRelationshipPoolDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        updateObj.setProcessRemark(remark);
        updateObj.setProcessTime(new Date());
        // TODO: 设置处理人信息，需要从当前登录用户获取
        // updateObj.setProcessorId(getUserId());
        // updateObj.setProcessBy(getNickName());
        
        extRelationshipPoolMapper.updateById(updateObj);
        
        // 如果确认关系，则存入Neo4j
        if (status == 1) { // 已确认
            try {
                // 检查两个实体是否都已确认
                ExtEntityPoolDO sourceEntity = extEntityPoolService.getEntityByEntityId(
                    relationshipPool.getSourceEntityId(), relationshipPool.getTaskId());
                ExtEntityPoolDO targetEntity = extEntityPoolService.getEntityByEntityId(
                    relationshipPool.getTargetEntityId(), relationshipPool.getTaskId());
                
                if (sourceEntity != null && targetEntity != null && 
                    sourceEntity.getStatus() == 1 && targetEntity.getStatus() == 1) {
                    // 两个实体都已确认，可以创建关系
                    saveRelationshipToNeo4j(relationshipPool, sourceEntity, targetEntity);
                    log.info("关系确认成功，已存入Neo4j，关系ID: {}", id);
                } else {
                    log.warn("关系确认失败，源实体或目标实体未确认，关系ID: {}", id);
                    return AjaxResult.error("关系确认失败：源实体或目标实体未确认");
                }
            } catch (Exception e) {
                log.error("关系确认后存入Neo4j失败，关系ID: {}", id, e);
                throw new RuntimeException("关系确认后存入Neo4j失败: " + e.getMessage());
            }
        }
        
        return AjaxResult.success("处理成功");
    }

    @Override
    public List<ExtRelationshipPoolDO> getRelationshipsByEntityId(String entityId, Long taskId) {
        // 查询源实体为该实体的关系
        LambdaQueryWrapperX<ExtRelationshipPoolDO> sourceQuery = new LambdaQueryWrapperX<ExtRelationshipPoolDO>()
                .eq(ExtRelationshipPoolDO::getTaskId, taskId)
                .eq(ExtRelationshipPoolDO::getSourceEntityId, entityId);
        
        // 查询目标实体为该实体的关系
        LambdaQueryWrapperX<ExtRelationshipPoolDO> targetQuery = new LambdaQueryWrapperX<ExtRelationshipPoolDO>()
                .eq(ExtRelationshipPoolDO::getTaskId, taskId)
                .eq(ExtRelationshipPoolDO::getTargetEntityId, entityId);
        
        List<ExtRelationshipPoolDO> sourceRelationships = extRelationshipPoolMapper.selectList(sourceQuery);
        List<ExtRelationshipPoolDO> targetRelationships = extRelationshipPoolMapper.selectList(targetQuery);
        
        // 合并结果并去重
        List<ExtRelationshipPoolDO> allRelationships = new ArrayList<>();
        allRelationships.addAll(sourceRelationships);
        allRelationships.addAll(targetRelationships);
        
        return allRelationships;
    }

    /**
     * 批量处理关系（确认或拒绝）
     *
     * @param idList 关系ID列表
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult batchProcessRelationships(List<Long> idList, Integer status, String remark) {
        if (idList == null || idList.isEmpty()) {
            return AjaxResult.error("请选择要处理的关系");
        }
        
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        for (Long id : idList) {
            try {
                AjaxResult result = processRelationship(id, status, remark);
                if (result.isSuccess()) {
                    successCount++;
                } else {
                    failCount++;
                    errorMessages.add("关系ID " + id + ": " + result.get("msg"));
                }
            } catch (Exception e) {
                failCount++;
                errorMessages.add("关系ID " + id + ": " + e.getMessage());
                log.error("批量处理关系失败，关系ID: {}", id, e);
            }
        }
        
        String message = String.format("批量处理完成：成功 %d 个，失败 %d 个", successCount, failCount);
        if (!errorMessages.isEmpty()) {
            message += "。失败详情：" + String.join("; ", errorMessages);
        }
        
        return AjaxResult.success(message);
    }

    /**
     * 根据关系ID查询关系池记录
     *
     * @param relationshipId 关系ID
     * @param taskId 任务ID
     * @return 关系池记录
     */
    @Override
    public ExtRelationshipPoolDO getRelationshipById(String relationshipId, Long taskId) {
        LambdaQueryWrapper<ExtRelationshipPoolDO> queryWrapper = new LambdaQueryWrapper<ExtRelationshipPoolDO>()
                .eq(ExtRelationshipPoolDO::getTaskId, taskId)
                .and(wrapper -> wrapper
                    .eq(ExtRelationshipPoolDO::getSourceEntityId, relationshipId)
                    .or()
                    .eq(ExtRelationshipPoolDO::getTargetEntityId, relationshipId)
                );
        
        return extRelationshipPoolMapper.selectOne(queryWrapper);
    }

    private ExtRelationshipPoolDO validateExtRelationshipPoolExistsAndReturn(Long id) {
        ExtRelationshipPoolDO relationshipPool = extRelationshipPoolMapper.selectById(id);
        if (relationshipPool == null) {
            throw new ServiceException("关系池不存在");
        }
        return relationshipPool;
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
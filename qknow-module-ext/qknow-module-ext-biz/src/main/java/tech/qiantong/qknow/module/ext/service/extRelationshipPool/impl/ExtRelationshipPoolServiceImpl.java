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
import com.alibaba.fastjson2.JSON;

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
        
        log.info("============ 开始处理关系 ============");
        log.info("关系ID: {}, 状态: {}, 备注: {}", id, status, remark);
        log.info("关系信息: {}", JSON.toJSONString(relationshipPool));
        
        // 如果确认关系，先检查实体状态
        if (status == 1) { // 已确认
            try {
                log.info("============ 开始检查源实体和目标实体 ============");
                log.info("源实体ID: {}, 目标实体ID: {}, 任务ID: {}", 
                    relationshipPool.getSourceEntityId(), 
                    relationshipPool.getTargetEntityId(), 
                    relationshipPool.getTaskId());
                
                // 检查两个实体是否都已确认
                ExtEntityPoolDO sourceEntity = extEntityPoolService.getEntityByEntityId(
                    relationshipPool.getSourceEntityId(), relationshipPool.getTaskId());
                ExtEntityPoolDO targetEntity = extEntityPoolService.getEntityByEntityId(
                    relationshipPool.getTargetEntityId(), relationshipPool.getTaskId());
                
                log.info("============ 实体查询结果 ============");
                log.info("源实体查询结果: {}", sourceEntity != null ? JSON.toJSONString(sourceEntity) : "null");
                log.info("目标实体查询结果: {}", targetEntity != null ? JSON.toJSONString(targetEntity) : "null");
                
                if (sourceEntity != null && targetEntity != null && 
                    sourceEntity.getStatus() == 1 && targetEntity.getStatus() == 1) {
                    log.info("============ 两个实体都已确认，开始创建关系 ============");
                    // 两个实体都已确认，可以创建关系
                    saveRelationshipToNeo4j(relationshipPool, sourceEntity, targetEntity);
                    log.info("关系确认成功，已存入Neo4j，关系ID: {}", id);
                } else {
                    log.warn("============ 关系确认失败 ============");
                    log.warn("源实体状态: {}", sourceEntity != null ? sourceEntity.getStatus() : "null");
                    log.warn("目标实体状态: {}", targetEntity != null ? targetEntity.getStatus() : "null");
                    log.warn("关系确认失败，源实体或目标实体未确认，关系ID: {}", id);
                    // 关系确认失败，不更新数据库状态，直接返回错误
                    return AjaxResult.error("关系确认失败：源实体或目标实体未确认");
                }
            } catch (Exception e) {
                log.error("============ 关系确认后存入Neo4j失败 ============");
                log.error("关系ID: {}", id, e);
                throw new RuntimeException("关系确认后存入Neo4j失败: " + e.getMessage());
            }
        }
        
        // 只有在确认成功或拒绝时才更新数据库状态
        ExtRelationshipPoolDO updateObj = new ExtRelationshipPoolDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        updateObj.setProcessRemark(remark);
        updateObj.setProcessTime(new Date());
        // TODO: 设置处理人信息，需要从当前登录用户获取
        // updateObj.setProcessorId(getUserId());
        // updateObj.setProcessBy(getNickName());
        
        extRelationshipPoolMapper.updateById(updateObj);
        
        log.info("============ 关系处理完成 ============");
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
    public AjaxResult batchProcessRelationships(List<Long> idList, Integer status, String remark) {
        if (idList == null || idList.isEmpty()) {
            return AjaxResult.error("请选择要处理的关系");
        }
        
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();
        List<Long> successIds = new ArrayList<>();
        List<Long> failIds = new ArrayList<>();
        
        log.info("============ 开始批量处理关系 ============");
        log.info("待处理关系数量: {}, 状态: {}, 备注: {}", idList.size(), status, remark);
        
        // 逐个处理每个关系，每个关系独立处理，成功多少存入多少
        for (Long id : idList) {
            try {
                log.info("正在处理关系ID: {}", id);
                AjaxResult result = processRelationship(id, status, remark);
                if (result.isSuccess()) {
                    successCount++;
                    successIds.add(id);
                    log.info("关系ID {} 处理成功", id);
                } else {
                    failCount++;
                    failIds.add(id);
                    String errorMsg = "关系ID " + id + ": " + result.get("msg");
                    errorMessages.add(errorMsg);
                    log.warn("关系ID {} 处理失败: {}", id, result.get("msg"));
                }
            } catch (Exception e) {
                failCount++;
                failIds.add(id);
                String errorMsg = "关系ID " + id + ": " + e.getMessage();
                errorMessages.add(errorMsg);
                log.error("批量处理关系异常，关系ID: {}", id, e);
            }
        }
        
        log.info("============ 批量处理关系完成 ============");
        log.info("成功: {} 个, 失败: {} 个", successCount, failCount);
        
        // 构建返回结果
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("totalCount", idList.size());
        resultData.put("successCount", successCount);
        resultData.put("failCount", failCount);
        resultData.put("successIds", successIds);
        resultData.put("failIds", failIds);
        resultData.put("errorMessages", errorMessages);
        
        String message;
        if (failCount == 0) {
            message = "批量处理成功";
        } else if (successCount == 0) {
            message = String.format("批量处理失败：全部 %d 个关系处理失败", idList.size());
        } else {
            message = String.format("批量处理部分成功：成功 %d 个，失败 %d 个", successCount, failCount);
        }
        resultData.put("message", message);
        
        return AjaxResult.success(resultData);
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
            // 构建关系属性 - 移除前缀，并处理日期格式
            Map<String, Object> relationshipProperties = new HashMap<>();
            relationshipProperties.put("relationship_type", relationship.getRelationshipType());
            relationshipProperties.put("task_id", relationship.getTaskId());
            relationshipProperties.put("doc_id", relationship.getDocId());
            relationshipProperties.put("paragraph_index", relationship.getParagraphIndex());
            relationshipProperties.put("workspace_id", relationship.getWorkspaceId());
            relationshipProperties.put("release_status", 1); // 已发布
            
            // 处理日期格式，转换为ISO 8601格式或时间戳
            if (relationship.getProcessTime() != null) {
                // 使用时间戳格式，Neo4j可以正确处理
                relationshipProperties.put("process_time", relationship.getProcessTime().getTime());
            }
            
            // 处理处理人信息
            if (relationship.getProcessBy() != null) {
                relationshipProperties.put("process_by", relationship.getProcessBy());
            }
            
            // 构建源节点和目标节点的属性映射
            Map<String, Object> sourceNodeMap = new HashMap<>();
            sourceNodeMap.put("id", sourceEntity.getEntityId());
            sourceNodeMap.put("task_id", sourceEntity.getTaskId());
            
            Map<String, Object> targetNodeMap = new HashMap<>();
            targetNodeMap.put("id", targetEntity.getEntityId());
            targetNodeMap.put("task_id", targetEntity.getTaskId());
            
            // 创建关系
            String label = Neo4jLabelEnum.DYNAMICENTITY.getLabel() + ":" + Neo4jLabelEnum.UNSTRUCTURED.getLabel();
            Neo4jBuildWrapper<DynamicEntity> wrapper = new Neo4jBuildWrapper<>(DynamicEntity.class);
            dynamicRepository.mergeRelationship(label, wrapper, sourceNodeMap, targetNodeMap, 
                relationship.getRelationshipType(), relationshipProperties);
            
            log.info("============ 关系已成功存入Neo4j ============");
            log.info("关系类型: {}", relationship.getRelationshipType());
            log.info("源实体: {}", sourceEntity.getEntityName());
            log.info("目标实体: {}", targetEntity.getEntityName());
            
        } catch (Exception e) {
            log.error("============ 关系存入Neo4j失败 ============");
            log.error("关系信息: {}", JSON.toJSONString(relationship));
            log.error("错误详情: ", e);
            throw new RuntimeException("关系存入Neo4j失败: " + e.getMessage());
        }
    }
} 
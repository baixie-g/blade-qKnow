package tech.qiantong.qknow.module.ext.service.extRelationshipPool.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import static tech.qiantong.qknow.module.ext.enums.ErrorCodeConstants.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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
            throw new RuntimeException("导入数据不能为空");
        }
        
        StringBuilder resultMsg = new StringBuilder();
        int successNum = 0;
        int failureNum = 0;
        
        for (ExtRelationshipPoolRespVO data : importExcelList) {
            try {
                // 验证数据
                if (data.getSourceEntityId() == null || data.getSourceEntityId().trim().isEmpty() ||
                    data.getTargetEntityId() == null || data.getTargetEntityId().trim().isEmpty() ||
                    data.getRelationshipType() == null || data.getRelationshipType().trim().isEmpty()) {
                    failureNum++;
                    resultMsg.append("<br/>第 ").append(failureNum).append(" 条数据格式不正确");
                    continue;
                }
                
                // 检查是否存在
                LambdaQueryWrapperX<ExtRelationshipPoolDO> queryWrapper = new LambdaQueryWrapperX<ExtRelationshipPoolDO>()
                        .eq(ExtRelationshipPoolDO::getSourceEntityId, data.getSourceEntityId())
                        .eq(ExtRelationshipPoolDO::getTargetEntityId, data.getTargetEntityId())
                        .eq(ExtRelationshipPoolDO::getRelationshipType, data.getRelationshipType())
                        .eq(ExtRelationshipPoolDO::getTaskId, data.getTaskId());
                
                List<ExtRelationshipPoolDO> existingList = extRelationshipPoolMapper.selectList(queryWrapper);
                
                if (!existingList.isEmpty()) {
                    if (updateSupport) {
                        // 更新
                        ExtRelationshipPoolDO updateObj = new ExtRelationshipPoolDO();
                        updateObj.setId(existingList.get(0).getId());
                        updateObj.setWorkspaceId(data.getWorkspaceId());
                        updateObj.setTaskId(data.getTaskId());
                        updateObj.setDocId(data.getDocId());
                        updateObj.setParagraphIndex(data.getParagraphIndex());
                        updateObj.setSourceEntityId(data.getSourceEntityId());
                        updateObj.setTargetEntityId(data.getTargetEntityId());
                        updateObj.setRelationshipType(data.getRelationshipType());
                        updateObj.setStatus(data.getStatus());
                        updateObj.setProcessRemark(data.getProcessRemark());
                        updateObj.setUpdateBy(operName);
                        updateObj.setUpdateTime(new Date());
                        extRelationshipPoolMapper.updateById(updateObj);
                        successNum++;
                        resultMsg.append("<br/>第 ").append(successNum).append(" 条数据更新成功");
                    } else {
                        failureNum++;
                        resultMsg.append("<br/>第 ").append(failureNum).append(" 条数据已存在");
                    }
                } else {
                    // 新增
                    ExtRelationshipPoolDO insertObj = new ExtRelationshipPoolDO();
                    insertObj.setWorkspaceId(data.getWorkspaceId());
                    insertObj.setTaskId(data.getTaskId());
                    insertObj.setDocId(data.getDocId());
                    insertObj.setParagraphIndex(data.getParagraphIndex());
                    insertObj.setSourceEntityId(data.getSourceEntityId());
                    insertObj.setTargetEntityId(data.getTargetEntityId());
                    insertObj.setRelationshipType(data.getRelationshipType());
                    insertObj.setStatus(data.getStatus());
                    insertObj.setProcessRemark(data.getProcessRemark());
                    insertObj.setCreateBy(operName);
                    insertObj.setCreateTime(new Date());
                    extRelationshipPoolMapper.insert(insertObj);
                    successNum++;
                    resultMsg.append("<br/>第 ").append(successNum).append(" 条数据导入成功");
                }
            } catch (Exception e) {
                failureNum++;
                resultMsg.append("<br/>第 ").append(failureNum).append(" 条数据导入失败：").append(e.getMessage());
                log.error("导入关系池数据失败", e);
            }
        }
        
        if (failureNum > 0) {
            resultMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
        } else {
            resultMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
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

    private ExtRelationshipPoolDO validateExtRelationshipPoolExistsAndReturn(Long id) {
        ExtRelationshipPoolDO relationshipPool = extRelationshipPoolMapper.selectById(id);
        if (relationshipPool == null) {
            throw new ServiceException("关系池不存在");
        }
        return relationshipPool;
    }
} 
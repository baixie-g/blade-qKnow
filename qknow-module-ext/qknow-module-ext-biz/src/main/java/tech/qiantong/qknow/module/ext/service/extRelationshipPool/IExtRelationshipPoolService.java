package tech.qiantong.qknow.module.ext.service.extRelationshipPool;

import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.page.PageParam;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolSaveReqVO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extRelationshipPool.ExtRelationshipPoolDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 关系池 Service 接口
 *
 * @author qknow
 * @date 2025-01-20
 */
public interface IExtRelationshipPoolService {

    /**
     * 创建关系池
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExtRelationshipPool(@Valid ExtRelationshipPoolSaveReqVO createReqVO);

    /**
     * 更新关系池
     *
     * @param updateReqVO 更新信息
     */
    void updateExtRelationshipPool(@Valid ExtRelationshipPoolSaveReqVO updateReqVO);

    /**
     * 删除关系池
     *
     * @param id 编号
     */
    void deleteExtRelationshipPool(Long id);

    /**
     * 批量删除关系池
     *
     * @param idList 编号列表
     * @return 删除数量
     */
    int removeExtRelationshipPool(Collection<Long> idList);

    /**
     * 获得关系池
     *
     * @param id 编号
     * @return 关系池
     */
    ExtRelationshipPoolDO getExtRelationshipPool(Long id);

    /**
     * 获得关系池分页
     *
     * @param pageReqVO 分页查询
     * @return 关系池分页
     */
    PageResult<ExtRelationshipPoolRespVO> getExtRelationshipPoolPage(ExtRelationshipPoolPageReqVO pageReqVO);

    /**
     * 获得关系池列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 关系池列表
     */
    List<ExtRelationshipPoolDO> getExtRelationshipPoolList(ExtRelationshipPoolPageReqVO exportReqVO);

    /**
     * 批量保存关系到池子
     *
     * @param relationshipPoolList 关系列表
     */
    void batchSaveRelationships(List<ExtRelationshipPoolDO> relationshipPoolList);

    /**
     * 导入关系池数据
     *
     * @param importExcelList 导入数据列表
     * @param updateSupport 是否更新支持
     * @param operName 操作用户
     * @return 导入结果
     */
    String importExtRelationshipPool(List<ExtRelationshipPoolRespVO> importExcelList, boolean updateSupport, String operName);

    /**
     * 处理关系（确认或拒绝）
     *
     * @param id 关系ID
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    AjaxResult processRelationship(Long id, Integer status, String remark);

    /**
     * 根据实体ID和任务ID查询相关关系
     *
     * @param entityId 实体ID
     * @param taskId 任务ID
     * @return 关系列表
     */
    List<ExtRelationshipPoolDO> getRelationshipsByEntityId(String entityId, Long taskId);

    /**
     * 批量处理关系（确认或拒绝）
     *
     * @param idList 关系ID列表
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    AjaxResult batchProcessRelationships(List<Long> idList, Integer status, String remark);

    /**
     * 根据关系ID和任务ID查询关系池记录
     *
     * @param relationshipId 关系ID
     * @param taskId 任务ID
     * @return 关系池记录
     */
    ExtRelationshipPoolDO getRelationshipById(String relationshipId, Long taskId);
} 
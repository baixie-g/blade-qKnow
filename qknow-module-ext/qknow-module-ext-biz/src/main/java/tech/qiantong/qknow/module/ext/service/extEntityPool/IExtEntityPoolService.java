package tech.qiantong.qknow.module.ext.service.extEntityPool;

import java.util.List;
import java.util.Collection;
import javax.validation.Valid;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolSaveReqVO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;

/**
 * 实体池 Service 接口
 *
 * @author qknow
 * @date 2025-01-20
 */
public interface IExtEntityPoolService {

    /**
     * 创建实体池
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExtEntityPool(@Valid ExtEntityPoolSaveReqVO createReqVO);

    /**
     * 更新实体池
     *
     * @param updateReqVO 更新信息
     */
    void updateExtEntityPool(@Valid ExtEntityPoolSaveReqVO updateReqVO);

    /**
     * 删除实体池
     *
     * @param id 编号
     */
    void deleteExtEntityPool(Long id);

    /**
     * 批量删除实体池
     *
     * @param idList 编号列表
     * @return 删除数量
     */
    int removeExtEntityPool(Collection<Long> idList);

    /**
     * 获得实体池
     *
     * @param id 编号
     * @return 实体池
     */
    ExtEntityPoolRespVO getExtEntityPool(Long id);

    /**
     * 获得实体池分页
     *
     * @param pageReqVO 分页查询
     * @return 实体池分页
     */
    PageResult<ExtEntityPoolDO> getExtEntityPoolPage(ExtEntityPoolPageReqVO pageReqVO);

    /**
     * 获得实体池列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 实体池列表
     */
    List<ExtEntityPoolDO> getExtEntityPoolList(ExtEntityPoolPageReqVO exportReqVO);

    /**
     * 获得所有实体池列表
     *
     * @return 实体池列表
     */
    List<ExtEntityPoolDO> getExtEntityPoolList();

    /**
     * 批量保存实体到池子
     *
     * @param entityPoolList 实体列表
     */
    void batchSaveEntities(List<ExtEntityPoolDO> entityPoolList);

    /**
     * 导入实体池数据
     *
     * @param importExcelList 导入数据列表
     * @param updateSupport 是否更新支持
     * @param operName 操作用户
     * @return 导入结果
     */
    String importExtEntityPool(List<ExtEntityPoolRespVO> importExcelList, boolean updateSupport, String operName);

    /**
     * 处理实体（确认或拒绝）
     *
     * @param id 实体ID
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    AjaxResult processEntity(Long id, Integer status, String remark);

    /**
     * 批量处理实体（确认或拒绝）
     *
     * @param idList 实体ID列表
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    AjaxResult batchProcessEntities(List<Long> idList, Integer status, String remark);

    /**
     * 实体消歧 - 获取候选实体
     *
     * @param entityPoolId 实体池ID
     * @param topK 返回候选数量
     * @return 消歧结果
     */
    AjaxResult disambiguateEntity(Long entityPoolId, Integer topK);

    /**
     * 实体消歧 - 确认消歧结果
     *
     * @param entityPoolId 实体池ID
     * @param candidateId 候选实体ID
     * @param remark 备注
     * @return 处理结果
     */
    AjaxResult confirmDisambiguation(Long entityPoolId, String candidateId, String remark);

    /**
     * 根据实体ID查询实体池记录
     *
     * @param entityId 实体ID
     * @param taskId 任务ID
     * @return 实体池记录
     */
    ExtEntityPoolDO getEntityByEntityId(String entityId, Long taskId);
} 
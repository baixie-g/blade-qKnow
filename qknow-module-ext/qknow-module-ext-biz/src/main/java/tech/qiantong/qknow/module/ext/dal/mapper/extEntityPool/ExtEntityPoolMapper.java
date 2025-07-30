package tech.qiantong.qknow.module.ext.dal.mapper.extEntityPool;

import tech.qiantong.qknow.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qknow.mybatis.core.query.LambdaQueryWrapperX;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolPageReqVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 实体池 Mapper
 *
 * @author qknow
 * @date 2025-01-20
 */
@Mapper
public interface ExtEntityPoolMapper extends BaseMapperX<ExtEntityPoolDO> {

    /**
     * 批量插入实体
     *
     * @param entityList 实体列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<ExtEntityPoolDO> entityList);

    /**
     * 根据查询条件获取实体池列表
     *
     * @param pageReqVO 查询条件
     * @return 实体池列表
     */
    List<ExtEntityPoolDO> selectList(ExtEntityPoolPageReqVO pageReqVO);

    /**
     * 根据实体ID和任务ID查询实体池记录
     *
     * @param entityId 实体ID
     * @param taskId 任务ID
     * @return 实体池记录
     */
    ExtEntityPoolDO selectByEntityId(@Param("entityId") String entityId, @Param("taskId") Long taskId);

    /**
     * 分页查询实体池
     *
     * @param reqVO 查询条件
     * @return 实体池分页结果
     */
    default PageResult<ExtEntityPoolDO> selectPage(ExtEntityPoolPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time", "entity_name", "entity_type", "status"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<ExtEntityPoolDO>()
                .eqIfPresent(ExtEntityPoolDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(ExtEntityPoolDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(ExtEntityPoolDO::getDocId, reqVO.getDocId())
                .likeIfPresent(ExtEntityPoolDO::getEntityName, reqVO.getEntityName())
                .eqIfPresent(ExtEntityPoolDO::getEntityType, reqVO.getEntityType())
                .eqIfPresent(ExtEntityPoolDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ExtEntityPoolDO::getCreateTime, reqVO.getCreateTimeStart(), reqVO.getCreateTimeEnd())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
} 
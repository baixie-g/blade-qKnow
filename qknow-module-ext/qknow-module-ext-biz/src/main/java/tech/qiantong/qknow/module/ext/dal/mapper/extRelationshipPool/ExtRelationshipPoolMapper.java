package tech.qiantong.qknow.module.ext.dal.mapper.extRelationshipPool;

import tech.qiantong.qknow.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qknow.mybatis.core.query.LambdaQueryWrapperX;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.module.ext.dal.dataobject.extRelationshipPool.ExtRelationshipPoolDO;
import tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo.ExtRelationshipPoolPageReqVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 关系池 Mapper
 *
 * @author qknow
 * @date 2025-01-20
 */
@Mapper
public interface ExtRelationshipPoolMapper extends BaseMapperX<ExtRelationshipPoolDO> {

    /**
     * 批量插入关系
     *
     * @param relationshipList 关系列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<ExtRelationshipPoolDO> relationshipList);

    /**
     * 分页查询关系池
     *
     * @param reqVO 查询条件
     * @return 关系池分页结果
     */
    default PageResult<ExtRelationshipPoolDO> selectPage(ExtRelationshipPoolPageReqVO reqVO) {
        // 定义排序的字段（防止 SQL 注入，与数据库字段名称一致）
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));

        // 构造动态查询条件
        return selectPage(reqVO, new LambdaQueryWrapperX<ExtRelationshipPoolDO>()
                .eqIfPresent(ExtRelationshipPoolDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(ExtRelationshipPoolDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(ExtRelationshipPoolDO::getDocId, reqVO.getDocId())
                .likeIfPresent(ExtRelationshipPoolDO::getSourceEntityId, reqVO.getSourceEntityId())
                .likeIfPresent(ExtRelationshipPoolDO::getTargetEntityId, reqVO.getTargetEntityId())
                .likeIfPresent(ExtRelationshipPoolDO::getRelationshipType, reqVO.getRelationshipType())
                .eqIfPresent(ExtRelationshipPoolDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ExtRelationshipPoolDO::getCreateTime, reqVO.getCreateTimeStart(), reqVO.getCreateTimeEnd())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }

} 
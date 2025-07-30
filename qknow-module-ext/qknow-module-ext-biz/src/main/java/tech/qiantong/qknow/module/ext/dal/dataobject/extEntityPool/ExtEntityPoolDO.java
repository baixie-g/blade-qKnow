package tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import tech.qiantong.qknow.common.core.domain.BaseEntity;

/**
 * 实体池 DO 对象 ext_entity_pool
 *
 * @author qknow
 * @date 2025-01-20
 */
@Data
@TableName(value = "ext_entity_pool")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtEntityPoolDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工作区id */
    private Long workspaceId;

    /** 任务id */
    private Long taskId;

    /** 文档id */
    private Long docId;

    /** 段落索引 */
    private Integer paragraphIndex;

    /** 实体ID */
    private String entityId;

    /** 实体名称 */
    private String entityName;

    /** 实体类型 */
    private String entityType;

    /** 实体别名，JSON格式 */
    private String aliases;

    /** 实体定义 */
    private String definition;

    /** 实体属性，JSON格式 */
    private String attributes;

    /** 处理状态;0：待处理，1：已确认，2：已拒绝 */
    private Integer status;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;

    /** 处理人id */
    private Long processorId;

    /** 处理人 */
    private String processBy;

    /** 处理备注 */
    private String processRemark;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    @TableLogic
    private Boolean delFlag;
} 
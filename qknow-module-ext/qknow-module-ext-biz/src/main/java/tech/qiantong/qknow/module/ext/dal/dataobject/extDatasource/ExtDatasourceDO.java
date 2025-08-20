package tech.qiantong.qknow.module.ext.dal.dataobject.extDatasource;

import lombok.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import tech.qiantong.qknow.common.core.domain.BaseEntity;

/**
 * 数据源 DO 对象 ext_datasource
 *
 * @author qknow
 * @date 2025-02-25
 */
@Data
@TableName(value = "ext_datasource")
// 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
// @KeySequence("ext_datasource_seq")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtDatasourceDO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 数据库连接名称 */
    private String name;

    /** 数据库类型 */
    private Integer type;

    /** 数据库地址 */
    private String host;

    /** 端口号 */
    private Long port;

    /** 数据库名称 */
    private String databaseName;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 连接状态 */
    private Integer status;

    /** 是否有效 */
    private Boolean validFlag;

    /** 删除标志 */
    @TableLogic
    private Boolean delFlag;

    /** 模式/数据库实例名（用于 Oracle、PostgreSQL 等） */
    @TableField("`schema`")
    private String schema;

    /** 数据源配置（JSON格式，存储额外的连接参数） */
    private String connectionConfig;

    /** 数据源来源：EXT-原有EXT数据源，UNIFIED-新统一数据源 */
    private String source;

    /** 原始数据源ID（用于迁移时关联） */
    private Long originalId;

    /** 备注 */
    private String remark;

}

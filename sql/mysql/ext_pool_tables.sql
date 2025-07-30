-- ----------------------------
-- 实体池表
-- ----------------------------
drop table if exists ext_entity_pool;
create table ext_entity_pool (
    id bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    workspace_id bigint NOT NULL COMMENT '工作区id',
    task_id bigint NOT NULL COMMENT '任务id',
    doc_id bigint NOT NULL COMMENT '文档id',
    paragraph_index int NOT NULL COMMENT '段落索引',
    entity_id varchar(128) NOT NULL COMMENT '实体ID',
    entity_name varchar(255) NOT NULL COMMENT '实体名称',
    entity_type varchar(128) NOT NULL COMMENT '实体类型',
    aliases text COMMENT '实体别名，JSON格式',
    definition text COMMENT '实体定义',
    attributes text COMMENT '实体属性，JSON格式',
    status tinyint unsigned NOT NULL DEFAULT '0' COMMENT '处理状态;0：待处理，1：已确认，2：已拒绝',
    process_time datetime DEFAULT NULL COMMENT '处理时间',
    processor_id bigint DEFAULT NULL COMMENT '处理人id',
    process_by varchar(128) DEFAULT NULL COMMENT '处理人',
    process_remark varchar(512) DEFAULT NULL COMMENT '处理备注',
    valid_flag tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
    del_flag tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
    create_by varchar(32) DEFAULT NULL COMMENT '创建人',
    creator_id bigint DEFAULT NULL COMMENT '创建人id',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by varchar(32) DEFAULT NULL COMMENT '更新人',
    updater_id bigint DEFAULT NULL COMMENT '更新人id',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    remark varchar(512) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_status (status),
    KEY idx_entity_name (entity_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COMMENT='实体池表';

-- ----------------------------
-- 关系池表
-- ----------------------------
drop table if exists ext_relationship_pool;
create table ext_relationship_pool (
    id bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    workspace_id bigint NOT NULL COMMENT '工作区id',
    task_id bigint NOT NULL COMMENT '任务id',
    doc_id bigint NOT NULL COMMENT '文档id',
    paragraph_index int NOT NULL COMMENT '段落索引',
    source_entity_id varchar(128) NOT NULL COMMENT '源实体ID',
    target_entity_id varchar(128) NOT NULL COMMENT '目标实体ID',
    relationship_type varchar(128) NOT NULL COMMENT '关系类型',
    status tinyint unsigned NOT NULL DEFAULT '0' COMMENT '处理状态;0：待处理，1：已确认，2：已拒绝',
    process_time datetime DEFAULT NULL COMMENT '处理时间',
    processor_id bigint DEFAULT NULL COMMENT '处理人id',
    process_by varchar(128) DEFAULT NULL COMMENT '处理人',
    process_remark varchar(512) DEFAULT NULL COMMENT '处理备注',
    valid_flag tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
    del_flag tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
    create_by varchar(32) DEFAULT NULL COMMENT '创建人',
    creator_id bigint DEFAULT NULL COMMENT '创建人id',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by varchar(32) DEFAULT NULL COMMENT '更新人',
    updater_id bigint DEFAULT NULL COMMENT '更新人id',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    remark varchar(512) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_status (status),
    KEY idx_source_target (source_entity_id, target_entity_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COMMENT='关系池表'; 
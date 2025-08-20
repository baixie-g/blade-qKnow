-- 创建统一数据源表
CREATE TABLE IF NOT EXISTS `unified_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据源编号',
  `name` varchar(100) NOT NULL COMMENT '数据源名称',
  `type` int(11) NOT NULL COMMENT '数据源类型：1-MySQL, 2-Neo4j, 3-Oracle, 4-PostgreSQL',
  `host` varchar(50) NOT NULL COMMENT '数据库地址',
  `port` bigint(20) NOT NULL COMMENT '端口号',
  `database_name` varchar(100) DEFAULT NULL COMMENT '数据库名称',
  `schema` varchar(100) DEFAULT NULL COMMENT '模式/数据库实例名',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `status` int(11) DEFAULT '0' COMMENT '连接状态：0-未连接，1-已连接，2-连接失败',
  `valid_flag` tinyint(1) DEFAULT '1' COMMENT '是否有效',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志',
  `description` varchar(500) DEFAULT NULL COMMENT '描述信息',
  `connection_config` text COMMENT '连接配置（JSON格式）',
  `source` varchar(20) DEFAULT 'UNIFIED' COMMENT '数据源来源：DM-原有DM数据源，EXT-原有EXT数据源，UNIFIED-新统一数据源',
  `original_id` bigint(20) DEFAULT NULL COMMENT '原始数据源ID（用于迁移时关联）',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` bigint(20) DEFAULT NULL COMMENT '更新者ID',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_host_port` (`name`, `host`, `port`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_valid_flag` (`valid_flag`),
  KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一数据源表';

-- 插入示例数据（本地 MySQL 数据源）
INSERT INTO `unified_datasource` (
  `name`, `type`, `host`, `port`, `database_name`, `username`, `password`, 
  `status`, `valid_flag`, `source`, `description`
) VALUES (
  '本地数据库', 1, '127.0.0.1', 3306, 'qknow_dev', 'root', '123456',
  0, 1, 'DM', '本地开发数据库'
) ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 插入示例数据（本地 Neo4j 数据源）
INSERT INTO `unified_datasource` (
  `name`, `type`, `host`, `port`, `username`, `password`, 
  `status`, `valid_flag`, `source`, `description`
) VALUES (
  '本地Neo4j', 2, '127.0.0.1', 7474, 'neo4j', 'neo4j',
  0, 1, 'EXT', '本地Neo4j图数据库'
) ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 插入示例数据（远程 Neo4j 数据源）
INSERT INTO `unified_datasource` (
  `name`, `type`, `host`, `port`, `username`, `password`, 
  `status`, `valid_flag`, `source`, `description`
) VALUES (
  '远程Neo4j', 2, '47.105.115.60', 7474, 'neo4j', 'neo4j',
  0, 1, 'EXT', '远程Neo4j图数据库'
) ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

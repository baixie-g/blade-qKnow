-- 扩展 ext_datasource 表结构，支持所有类型的数据源
-- 添加 MySQL 等关系型数据库需要的字段

-- 添加新字段
ALTER TABLE `ext_datasource` 
ADD COLUMN `schema` VARCHAR(100) DEFAULT NULL COMMENT '模式/数据库实例名（用于 Oracle、PostgreSQL 等）' AFTER `database_name`,
ADD COLUMN `connection_config` TEXT COMMENT '数据源配置（JSON格式，存储额外的连接参数）' AFTER `schema`,
ADD COLUMN `source` VARCHAR(20) DEFAULT 'EXT' COMMENT '数据源来源：EXT-原有EXT数据源，UNIFIED-新统一数据源' AFTER `connection_config`,
ADD COLUMN `original_id` BIGINT DEFAULT NULL COMMENT '原始数据源ID（用于迁移时关联）' AFTER `source`,
ADD COLUMN `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注' AFTER `original_id`;

-- 添加索引
ALTER TABLE `ext_datasource` 
ADD INDEX `idx_type` (`type`),
ADD INDEX `idx_status` (`status`),
ADD INDEX `idx_valid_flag` (`valid_flag`),
ADD INDEX `idx_source` (`source`);

-- 更新现有 Neo4j 数据源的 source 字段
UPDATE `ext_datasource` SET `source` = 'EXT' WHERE `type` = 2;

-- 插入示例 MySQL 数据源（如果不存在）
INSERT INTO `ext_datasource` (
    `name`, `type`, `host`, `port`, `database_name`, `username`, `password`, 
    `status`, `valid_flag`, `source`, `remark`
) VALUES (
    '本地MySQL', 1, '127.0.0.1', 3306, 'qknow_dev', 'root', '123456',
    0, 1, 'UNIFIED', '本地开发数据库'
) ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 插入示例 Oracle 数据源（如果不存在）
INSERT INTO `ext_datasource` (
    `name`, `type`, `host`, `port`, `database_name`, `schema`, `username`, `password`, 
    `status`, `valid_flag`, `source`, `remark`
) VALUES (
    '本地Oracle', 3, '127.0.0.1', 1521, 'XE', 'SYSTEM', 'oracle', 'oracle',
    0, 1, 'UNIFIED', '本地Oracle数据库'
) ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 插入示例 PostgreSQL 数据源（如果不存在）
INSERT INTO `ext_datasource` (
    `name`, `type`, `host`, `port`, `database_name`, `username`, `password`, 
    `status`, `valid_flag`, `source`, `remark`
) VALUES (
    '本地PostgreSQL', 4, '127.0.0.1', 5432, 'postgres', 'postgres', 'postgres',
    0, 1, 'UNIFIED', '本地PostgreSQL数据库'
) ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

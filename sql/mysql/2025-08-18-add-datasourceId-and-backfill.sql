-- 为实体池、关系池、非结构化任务增加 Neo4j 数据源ID 字段
ALTER TABLE ext_entity_pool 
    ADD COLUMN IF NOT EXISTS datasource_id BIGINT NULL COMMENT 'Neo4j数据源ID';

ALTER TABLE ext_relationship_pool 
    ADD COLUMN IF NOT EXISTS datasource_id BIGINT NULL COMMENT 'Neo4j数据源ID';

ALTER TABLE ext_unstruct_task 
    ADD COLUMN IF NOT EXISTS datasource_id BIGINT NULL COMMENT 'Neo4j数据源ID';

-- 索引（可选，提升按数据源筛选性能）
ALTER TABLE ext_entity_pool ADD INDEX IF NOT EXISTS idx_entity_pool_datasource_id (datasource_id);
ALTER TABLE ext_relationship_pool ADD INDEX IF NOT EXISTS idx_rel_pool_datasource_id (datasource_id);

-- 回填已有数据：将为空的记录设置为本地 Neo4j（127.0.0.1:7474）对应的数据源ID
-- 注意：请先在数据源管理中添加一条 host=127.0.0.1, port=7474 的 Neo4j 数据源记录

UPDATE ext_entity_pool 
SET datasource_id = (
    SELECT id FROM ext_datasource 
    WHERE host = '127.0.0.1' AND port = 7474 
    ORDER BY id DESC LIMIT 1
) 
WHERE datasource_id IS NULL;

UPDATE ext_relationship_pool 
SET datasource_id = (
    SELECT id FROM ext_datasource 
    WHERE host = '127.0.0.1' AND port = 7474 
    ORDER BY id DESC LIMIT 1
) 
WHERE datasource_id IS NULL;



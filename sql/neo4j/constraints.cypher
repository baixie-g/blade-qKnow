-- Neo4j约束管理脚本

-- 1. 删除现有的约束（如果存在）
DROP CONSTRAINT entity_id_unique IF EXISTS;

-- 2. 创建唯一性约束
CREATE CONSTRAINT entity_id_unique FOR (n:Entity) REQUIRE n.id IS UNIQUE;

-- 3. 查看所有约束
SHOW CONSTRAINTS;

-- 4. 查看重复的实体ID（用于调试）
MATCH (n:Entity)
WITH n.id as entityId, count(*) as count
WHERE count > 1
RETURN entityId, count
ORDER BY count DESC;

-- 5. 清理重复实体（谨慎使用）
-- 保留第一个创建的实体，删除其他重复的
MATCH (n:Entity)
WITH n.id as entityId, collect(n) as nodes
WHERE size(nodes) > 1
UNWIND tail(nodes) as duplicate
DETACH DELETE duplicate;

-- 6. 验证约束是否生效
MATCH (n:Entity)
RETURN n.id, count(*) as count
ORDER BY count DESC
LIMIT 10; 
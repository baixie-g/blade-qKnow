-- 测试关系创建修复的SQL脚本

-- 1. 检查Neo4j中的实体约束
SHOW CONSTRAINTS;

-- 2. 查看现有的实体节点
MATCH (n:Entity) 
RETURN n.id, n.name, n.type, labels(n) 
ORDER BY n.id 
LIMIT 10;

-- 3. 查看现有的关系
MATCH (n:Entity)-[r]->(m:Entity) 
RETURN n.name, type(r), m.name, r.relationship_type, r.task_id 
ORDER BY n.name 
LIMIT 10;

-- 4. 检查是否有重复的实体ID
MATCH (n:Entity)
WITH n.id as entityId, count(*) as count
WHERE count > 1
RETURN entityId, count
ORDER BY count DESC;

-- 5. 检查特定实体的关系
MATCH (n:Entity {id: 'person_002_1753926297509'})-[r]->(m:Entity)
RETURN n.name, type(r), m.name, r.relationship_type;

MATCH (n:Entity)-[r]->(m:Entity {id: 'person_002_1753926297509'})
RETURN n.name, type(r), m.name, r.relationship_type;

-- 6. 清理重复实体（如果需要）
-- 注意：这个操作会删除重复的实体，请谨慎使用
/*
MATCH (n:Entity)
WITH n.id as entityId, collect(n) as nodes
WHERE size(nodes) > 1
UNWIND tail(nodes) as duplicate
DETACH DELETE duplicate;
*/

-- 7. 验证约束是否正常工作
-- 尝试创建一个已存在的实体（应该失败）
-- MERGE (n:Entity {id: 'person_002_1753926297509', name: 'test'}) RETURN n;

-- 8. 测试关系创建（使用正确的MERGE语法）
-- 这个查询应该成功，因为节点已存在
/*
MATCH (a:Entity {id: 'person_002_1753926297509'})
MATCH (b:Entity {id: 'organization_001_1753926297509'})
MERGE (a)-[r:WORKS_FOR]->(b)
ON CREATE SET r.relationship_type = 'WORKS_FOR', r.task_id = 1
RETURN a.name, type(r), b.name;
*/ 
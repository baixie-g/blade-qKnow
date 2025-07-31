#!/bin/bash
# resetneo.sh

echo "开始重置所有数据..."

# 1. 清空Neo4j数据库
echo "正在清空Neo4j数据库..."
curl -X POST http://localhost:7474/db/neo4j/tx/commit \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic bmVvNGo6MTIzNDU2Nzg=" \
  -d '{
    "statements": [
      {
        "statement": "MATCH (n) DETACH DELETE n"
      }
    ]
  }'

echo "Neo4j数据库清空完成"

# 2. 重置实体池状态
echo "正在重置实体池状态..."
mysql -u root -p123456 qknow_dev -e "
UPDATE ext_entity_pool 
SET status = 0, 
    process_time = NULL, 
    process_by = NULL, 
    process_remark = NULL
WHERE del_flag = 0;
"

echo "实体池状态重置完成"

# 3. 重置关系池状态
echo "正在重置关系池状态..."
mysql -u root -p123456 qknow_dev -e "
UPDATE ext_relationship_pool 
SET status = 0, 
    process_time = NULL, 
    process_by = NULL, 
    process_remark = NULL
WHERE del_flag = 0;
"

echo "关系池状态重置完成"
echo "所有数据重置完成！"
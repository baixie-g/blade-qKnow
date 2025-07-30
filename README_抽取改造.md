# 知识抽取改造说明

## 概述

本次改造将原有的DeepKE抽取方式改为调用新的抽取API接口，并将抽取结果放入实体池和关系池中进行人工审核处理。

## 主要改动

### 1. 后端改造

#### 1.1 抽取服务改造
- **文件**: `qknow-module-ext/qknow-module-ext-biz/src/main/java/tech/qiantong/qknow/module/ext/service/extraction/ExtractionService.java`
- **改动**: 添加了新的抽取方法接口 `extractWithTaskSchema`

- **文件**: `qknow-module-ext/qknow-module-ext-biz/src/main/java/tech/qiantong/qknow/module/ext/service/extraction/impl/ExtractionServiceImpl.java`
- **改动**: 
  - 实现了新的抽取方法
  - 支持从任务配置中获取schema信息
  - 调用新的API接口 `http://127.0.0.1:8000/extract`

#### 1.2 非结构化抽取任务改造
- **文件**: `qknow-module-ext/qknow-module-ext-biz/src/main/java/tech/qiantong/qknow/module/ext/service/extUnstructTask/impl/ExtUnstructTaskServiceImpl.java`
- **改动**:
  - 修改抽取逻辑，使用新的API接口
  - 从任务配置中获取schema信息
  - 将抽取结果保存到实体池和关系池
  - 添加了 `getTaskSchemaList` 方法获取任务schema配置

#### 1.3 实体池和关系池功能
- **实体池**: 存储抽取到的实体信息，等待人工审核
- **关系池**: 存储抽取到的关系信息，等待人工审核
- **处理流程**: 实体需要经过消歧服务后人工确认，关系会随实体确认后自动存入Neo4j

#### 1.4 数据库表结构
- **实体池表**: `ext_entity_pool`
- **关系池表**: `ext_relationship_pool`
- **状态字段**: 0-待处理，1-已确认，2-已拒绝

#### 1.5 配置文件
- **文件**: `application-ext-dev.yml` 和 `application-ext-prod.yml`
- **改动**: 添加了抽取API配置
```yaml
extraction:
  api:
    url: http://127.0.0.1:8000/extract
```

### 2. 前端改造

#### 2.1 路由配置
- **文件**: `qknow-ui/src/router/ext/public/index.js`
- **改动**: 添加了实体池和关系池的路由配置

#### 2.2 API接口
- **实体池API**: `qknow-ui/src/api/ext/extEntityPool/index.js`
- **关系池API**: `qknow-ui/src/api/ext/extRelationshipPool/index.js`

#### 2.3 页面组件
- **实体池页面**: `qknow-ui/src/views/ext/extEntityPool/index.vue`
- **关系池页面**: `qknow-ui/src/views/ext/extRelationshipPool/index.vue`
- **功能**: 支持查询、新增、修改、删除、处理（确认/拒绝）、导出等操作

## 新的抽取流程

### 1. 任务配置
- 在非结构化抽取任务中配置需要抽取的三元组schema
- 格式: `起点-关系->终点`，例如: `人物-毕业院校->学校`

### 2. 抽取执行
- 任务执行时，从配置中获取schema列表
- 调用新的API接口进行抽取
- 请求格式:
```json
{
  "text": "小明毕业于清华大学，目前在字节跳动工作。",
  "schema": {
    "schema": "任务抽取",
    "triplet": ["人物-毕业院校->学校", "人物-工作单位->公司"]
  }
}
```

### 3. 结果处理
- API返回格式:
```json
{
  "nodes": [
    {
      "id": "person_001",
      "name": "小明",
      "type": "人物",
      "aliases": [],
      "definition": "小明毕业于清华大学，目前在字节跳动工作。",
      "attributes": {
        "毕业院校": ["清华大学"],
        "工作单位": ["字节跳动"]
      }
    }
  ],
  "relationships": [
    {
      "source": "person_001",
      "target": "school_001",
      "type": "毕业院校"
    }
  ]
}
```

### 4. 池子存储
- 实体信息保存到实体池
- 关系信息保存到关系池
- 状态设置为"待处理"

### 5. 人工审核
- 通过前端页面查看待处理的实体和关系
- 进行确认或拒绝操作
- 实体确认后存入Neo4j数据库
- 关系随实体确认后自动存入Neo4j数据库

## 部署说明

### 1. 数据库
- 执行 `sql/mysql/ext_pool_tables.sql` 创建实体池和关系池表
- 执行 `sql/mysql/ext_pool_menus.sql` 添加菜单配置

### 2. 后端
- 确保新的抽取API服务运行在 `127.0.0.1:8000`
- 重启后端服务

### 3. 前端
- 重新构建前端项目
- 确保新的路由和页面正常访问

## 注意事项

1. 新的抽取API需要支持指定的请求和响应格式
2. 实体池中的实体需要经过消歧服务处理
3. 关系池中的关系会随实体确认后自动处理
4. 原有的DeepKE抽取方式仍然保留，可通过配置文件切换

## 测试建议

1. 创建非结构化抽取任务，配置schema
2. 执行抽取任务，检查实体池和关系池数据
3. 通过前端页面进行人工审核操作
4. 验证确认后的数据是否正确存入Neo4j 
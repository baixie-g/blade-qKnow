# 图谱探索界面LLM问答功能集成说明

## 功能概述

本项目已成功为图谱探索界面（http://localhost:5173/app/graphExploration）集成了LLM问答交互功能，用户可以通过自然语言查询知识图谱数据。

## 新增功能

### 1. 智能问答按钮
- 在图谱探索界面顶部工具栏添加了"智能问答"按钮
- 点击按钮可以打开/关闭聊天面板
- 按钮状态会根据聊天面板的显示状态变化

### 2. 聊天面板
- 右侧滑出的聊天界面，宽度400px
- 支持与LLM进行自然语言对话
- 显示生成的Cypher查询语句
- 展示查询结果数据
- 支持清空聊天记录和设置配置

### 3. 核心特性
- **自然语言查询**：用户可以用中文描述查询需求
- **Cypher生成**：自动将自然语言转换为Cypher查询语句
- **结果展示**：以表格形式展示查询结果
- **代码复制**：支持复制生成的Cypher查询语句
- **设置配置**：可配置LLM模型、数据库、工作流类型等参数

## 技术实现

### 1. API集成
- 创建了 `qknow-ui/src/api/app/graph/text2cypher.js` 文件
- 集成了Text2Cypher API的所有接口
- 使用独立的axios实例处理API请求

### 2. 组件开发
- 创建了 `qknow-ui/src/components/ChatInterface.vue` 聊天组件
- 支持消息历史记录
- 实现了加载状态和错误处理
- 响应式设计，适配不同屏幕尺寸

### 3. 界面集成
- 修改了 `qknow-ui/src/views/app/graphExploration/index.vue`
- 添加了聊天按钮和面板
- 实现了聊天面板的显示/隐藏逻辑
- 添加了相应的CSS样式

## 使用方法

### 1. 启动服务
确保以下服务正在运行：
- 前端服务：`http://localhost:5173`
- Text2Cypher API服务：`http://localhost:8003`

### 2. 访问图谱探索页面
访问：`http://localhost:5173/app/graphExploration`

### 3. 使用问答功能
1. 点击顶部的"智能问答"按钮
2. 在聊天面板中输入问题，例如：
   - "查询所有人员信息"
   - "查找技术专家"
   - "分析实体关系"
3. 按回车或点击发送按钮
4. 查看生成的Cypher查询和结果

### 4. 配置设置
点击聊天面板右上角的设置按钮，可以配置：
- LLM模型选择
- 数据库选择
- 工作流类型
- 超时时间

## API配置

### Text2Cypher API地址
默认配置：`http://localhost:8003/api/v1`

如需修改API地址，请编辑以下文件：
- `qknow-ui/src/api/app/graph/text2cypher.js` 中的 `TEXT2CYPHER_BASE_URL`

### 默认配置
- LLM模型：`ark-model`
- 数据库：`neo4j`
- 工作流：`text2cypher_with_1_retry_and_output_check`
- 超时时间：60秒

## 测试验证

### 1. API连接测试
可以使用提供的测试页面验证API连接：
```
qknow-ui/test-chat.html
```

### 2. 功能测试
1. 打开图谱探索页面
2. 点击"智能问答"按钮
3. 输入测试问题
4. 验证响应和结果展示

## 错误处理

### 常见问题
1. **API连接失败**：检查Text2Cypher服务是否正常运行
2. **LLM模型不可用**：检查LLM配置和API密钥
3. **数据库连接错误**：检查Neo4j数据库连接
4. **查询超时**：调整超时时间或简化查询

### 调试方法
1. 打开浏览器开发者工具
2. 查看Console日志
3. 检查Network请求
4. 使用测试页面验证API

## 文件结构

```
qknow-ui/
├── src/
│   ├── api/app/graph/
│   │   └── text2cypher.js          # Text2Cypher API接口
│   ├── components/
│   │   └── ChatInterface.vue       # 聊天组件
│   └── views/app/graphExploration/
│       └── index.vue               # 图谱探索页面（已修改）
├── test-chat.html                  # API测试页面
└── CHAT_INTEGRATION_README.md      # 本说明文档
```

## 后续优化建议

1. **缓存机制**：对常见查询结果进行缓存
2. **历史记录**：持久化聊天历史记录
3. **语音输入**：支持语音转文字输入
4. **结果导出**：支持查询结果导出为Excel/CSV
5. **查询模板**：提供常用查询模板
6. **权限控制**：根据用户权限限制查询范围

## 技术支持

如遇到问题，请检查：
1. 服务状态和网络连接
2. API配置和参数
3. 浏览器控制台错误信息
4. 相关日志文件

---

**注意**：本功能需要Text2Cypher API服务正常运行，请确保相关依赖服务已正确配置和启动。 
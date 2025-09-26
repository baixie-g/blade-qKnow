# LLM模块 - 提示词管理

## 功能概述

LLM模块提供了完整的提示词模板管理功能，包括：

- 提示词类型管理
- 提示词模板的增删改查
- 模板复制、导入导出
- 与8003端口的提示词服务集成

## 主要功能

### 1. 提示词类型管理
- 查看所有可用的提示词类型
- 每个类型包含：类型标识、名称、描述、所属工作流、所属步骤

### 2. 提示词模板管理
- **创建模板**：支持自定义名称、类型、内容、描述等
- **编辑模板**：修改现有模板的各项属性
- **删除模板**：删除非默认模板
- **复制模板**：基于现有模板快速创建新模板
- **查看详情**：查看模板的完整信息
- **状态管理**：激活/禁用模板，设置默认模板

### 3. 批量操作
- **导入模板**：支持JSON格式的模板批量导入
- **导出模板**：导出所有模板为JSON格式

### 4. 搜索和筛选
- 按模板名称搜索
- 按提示词类型筛选
- 按激活状态筛选
- 按默认状态筛选
- 分页显示

## 技术架构

### 后端架构
```
qknow-module-llm/
├── qknow-module-llm-api/          # API接口定义
│   └── src/main/java/tech/qiantong/qknow/llm/dto/
│       ├── PromptType.java        # 提示词类型DTO
│       ├── PromptTemplate.java    # 提示词模板DTO
│       ├── CreateTemplateRequest.java  # 创建请求DTO
│       ├── UpdateTemplateRequest.java  # 更新请求DTO
│       ├── CopyTemplateRequest.java    # 复制请求DTO
│       ├── PromptTemplateFile.java    # 模板文件信息DTO
│       └── PageResult.java        # 分页结果DTO
├── qknow-module-llm-biz/          # 业务逻辑实现
│   └── src/main/java/tech/qiantong/qknow/llm/
│       ├── service/               # 服务接口
│       │   ├── PromptTemplateService.java
│       │   └── impl/
│       │       └── PromptTemplateServiceImpl.java
│       ├── controller/            # 控制器
│       │   └── PromptTemplateController.java
│       └── config/                # 配置类
│           └── RestTemplateConfig.java
```

### 前端架构
```
qknow-ui/src/
├── api/llm/
│   └── prompts.js                 # 提示词管理API
├── views/llm/
│   ├── prompts/                   # 提示词模板管理页面
│   │   └── index.vue
│   └── types/                     # 提示词类型管理页面
│       └── index.vue
└── router/system/dynamic/
    └── index.js                   # 路由配置
```

## API接口

### 基础路径
- 提示词管理：`/api/llm/prompts`

### 主要接口
1. **获取提示词类型** - `GET /types`
2. **分页获取模板列表** - `GET /templates`
3. **获取模板详情** - `GET /templates/detail/{templateId}`
4. **创建模板** - `POST /templates`
5. **更新模板** - `PUT /templates/{templateId}`
6. **删除模板** - `DELETE /templates/{templateId}`
7. **复制模板** - `POST /templates/{templateId}/copy`
8. **获取默认模板** - `GET /templates/default/{promptType}`
9. **按类型获取模板** - `GET /templates/by-type/{promptType}`
10. **批量导入** - `POST /templates/import`
11. **导出模板** - `GET /templates/export`
12. **获取文件信息** - `GET /templates/files`

## 配置说明

### 8003服务地址配置
在 `application.yml` 中配置8003服务的基础地址：

```yaml
llm:
  prompts:
    base-url: http://localhost:8003
```

### 权限配置
前端路由需要配置相应的权限：

```javascript
permissions: ['llm:prompts:list', 'llm:prompts:add', 'llm:prompts:edit', 'llm:prompts:remove']
```

## 使用说明

### 1. 启动服务
确保8003端口的提示词服务正在运行，然后启动Spring Boot应用。

### 2. 访问前端
- 提示词模板管理：`/llm/prompts`
- 提示词类型管理：`/llm/types`

### 3. 基本操作流程
1. 查看提示词类型，了解可用的模板类型
2. 创建或编辑提示词模板
3. 设置模板的激活状态和默认状态
4. 根据需要复制、删除模板
5. 使用导入导出功能进行批量操作

## 注意事项

1. **默认模板保护**：系统默认模板不允许删除
2. **类型关联**：模板必须关联到有效的提示词类型
3. **内容格式**：提示词内容支持多行文本，建议使用清晰的格式
4. **权限控制**：不同操作需要相应的权限配置
5. **服务依赖**：功能依赖8003端口的提示词服务，确保服务可用

## 扩展功能

### 1. 模板版本管理
- 支持模板的版本控制
- 版本回滚功能
- 变更历史记录

### 2. 模板测试
- 集成LLM服务进行模板测试
- 模板效果评估
- A/B测试支持

### 3. 模板推荐
- 基于使用频率推荐模板
- 智能模板匹配
- 模板质量评分

## 故障排除

### 常见问题
1. **8003服务连接失败**
   - 检查8003服务是否启动
   - 验证网络连接和防火墙设置
   - 检查配置文件中的服务地址

2. **模板创建失败**
   - 验证必填字段是否完整
   - 检查提示词类型是否存在
   - 查看后端日志获取详细错误信息

3. **前端页面加载异常**
   - 检查浏览器控制台错误
   - 验证API接口是否正常响应
   - 确认权限配置是否正确

### 日志查看
- 后端日志：查看Spring Boot应用日志
- 前端日志：查看浏览器开发者工具控制台
- 网络请求：查看Network面板的API调用情况

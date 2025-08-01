# 模型选择问题修复说明

## 问题描述

前端访问 `http://localhost:8090/api/llm/models` 获取到多个模型数据：
```json
[
    {
        "name": "ark-model",
        "status": "available",
        "provider": "ARK",
        "modelType": "Custom",
        "maxTokens": null,
        "temperature": 0.1
    },
    {
        "name": "doubao-seed-1.6",
        "status": "available",
        "provider": "unknown",
        "modelType": "unknown",
        "maxTokens": null,
        "temperature": 0.1
    }
]
```

但是在模型选择处只显示一个模型。

## 问题原因

前端代码在 `loadAvailableOptions` 函数中期望API返回格式为：
```javascript
{
  success: true,
  data: [模型列表]
}
```

但实际后端API直接返回模型数组：
```javascript
[模型列表]
```

导致前端无法正确解析模型数据。

## 修复内容

### 1. 修复API响应处理逻辑

**文件**: `qknow-ui/src/components/ChatInterface.vue`

**修改**: `loadAvailableOptions` 函数

```javascript
// 修复前
if (llmsRes.success) {
  availableLLMs.value = llmsRes.data
}

// 修复后
if (Array.isArray(llmsRes)) {
  availableLLMs.value = llmsRes
} else if (llmsRes && llmsRes.success && Array.isArray(llmsRes.data)) {
  availableLLMs.value = llmsRes.data
}
```

### 2. 优化模型选择界面

**修改**: 模型选择下拉框显示更多信息

- 显示模型名称和提供商
- 添加状态标签（可用/不可用）
- 显示可用模型数量

### 3. 添加智能默认值设置

**修改**: 在加载可用选项后自动设置合适的默认值

- 如果当前设置的模型不在可用列表中，自动选择第一个可用模型
- 对数据库和工作流也应用相同的逻辑

### 4. 添加调试信息

**修改**: 在控制台输出加载的选项信息，便于调试

## 修复效果

1. **正确显示所有模型**: 现在可以正确显示后端返回的所有可用模型
2. **更好的用户体验**: 模型选择界面显示更多有用信息
3. **智能默认值**: 自动选择可用的模型，避免无效设置
4. **向后兼容**: 同时支持直接数组和包装对象两种API响应格式

## 测试方法

1. 启动后端服务
2. 启动前端服务
3. 打开聊天界面
4. 点击设置按钮
5. 检查模型选择下拉框是否显示所有可用模型

## 测试页面

创建了 `qknow-ui/test-model-fix.html` 测试页面，可以独立测试：
- API响应格式
- 模型数据解析
- 前端处理逻辑

## 相关文件

- `qknow-ui/src/components/ChatInterface.vue` - 主要修复文件
- `qknow-ui/test-model-fix.html` - 测试页面
- `qknow-ui/src/api/app/graph/text2cypher.js` - API调用文件

## 注意事项

1. 确保后端服务正常运行在 `localhost:8090`
2. 确保API接口 `/api/llm/models` 返回正确的模型数据
3. 如果API响应格式发生变化，前端代码已经兼容两种格式 
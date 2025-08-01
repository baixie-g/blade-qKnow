import axios from 'axios'

// LLM API 基础配置 - 指向我们的Spring Boot后端服务
const LLM_API_BASE_URL = 'http://localhost:8090/api';

// 创建专用的axios实例用于LLM API
const llmService = axios.create({
  baseURL: LLM_API_BASE_URL,
  timeout: 60000, // 增加超时时间到60秒
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

// 响应拦截器
llmService.interceptors.response.use(
  response => {
    console.log('LLM API原始响应:', response)
    // 直接返回响应数据，让调用方处理
    return response.data;
  },
  error => {
    console.error('LLM API Error:', error);
    
    // 如果有响应数据，返回错误信息
    if (error.response && error.response.data) {
      return Promise.reject({
        response: error.response,
        message: error.response.data.errorMessage || error.response.data.message || 'API请求失败'
      });
    }
    
    // 网络错误或其他错误
    return Promise.reject({
      request: error.request,
      message: error.message || '网络连接失败'
    });
  }
);

// 健康检查
export function checkText2CypherHealth() {
  return llmService.get("/llm/health");
}

// 获取系统状态
export function getText2CypherStatus() {
  return llmService.get("/llm/status");
}

// 获取所有可用的LLM模型
export function getAvailableLLMs() {
  return llmService.get("/llm/models");
}

// 测试LLM连接
export function testLLMConnection(llmName) {
  return llmService.post(`/llm/models/${llmName}/test`);
}

// 获取所有可用的数据库
export function getAvailableDatabases() {
  return llmService.get("/llm/databases");
}

// 测试数据库连接
export function testDatabaseConnection(databaseName) {
  return llmService.post(`/llm/databases/${databaseName}/test`);
}

// 获取数据库模式信息
export function getDatabaseSchema(databaseName) {
  return llmService.get(`/llm/databases/${databaseName}/schema`);
}

// 获取所有可用的工作流
export function getAvailableWorkflows() {
  return llmService.get("/llm/workflows");
}

// 执行单个工作流
export function executeWorkflow(data) {
  return llmService.post("/llm/query", {
    question: data.input_text,
    context: data.context,
    llm_name: data.llm_name,
    database_name: data.database_name,
    workflow_type: data.workflow_type,
    timeout: data.timeout
  });
}

// 流式执行工作流
export function executeWorkflowStream(data) {
  return llmService.post("/llm/query/stream", {
    question: data.input_text,
    context: data.context
  }, {
    responseType: 'stream'
  });
}

// 批量执行工作流
export function executeWorkflowBatch(data) {
  return llmService.post("/llm/query/batch", data);
}

// 获取统计信息
export function getStatistics() {
  return llmService.get("/llm/statistics");
}

// 重置统计信息
export function resetStatistics() {
  return llmService.post("/llm/statistics/reset");
}

// 简化的问答接口 - 支持自定义配置
export function askQuestion(question, context = {}, settings = {}) {
  return executeWorkflow({
    input_text: question,
    context: context,
    llm_name: settings.llmName,
    database_name: settings.databaseName,
    workflow_type: settings.workflowType,
    timeout: settings.timeout
  });
} 
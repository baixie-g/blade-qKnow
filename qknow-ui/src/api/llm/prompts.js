import request from '@/utils/request'

// 提示词类型管理
export function getPromptTypes() {
  return request({
    url: '/api/llm/prompts/types',
    method: 'get'
  })
}

// 分页获取模板列表
export function getTemplates(params) {
  return request({
    url: '/api/llm/prompts/templates',
    method: 'get',
    params
  })
}

// 获取模板详情
export function getTemplateDetail(templateId) {
  return request({
    url: `/api/llm/prompts/templates/detail/${templateId}`,
    method: 'get'
  })
}

// 创建模板
export function createTemplate(data) {
  return request({
    url: '/api/llm/prompts/templates',
    method: 'post',
    data
  })
}

// 更新模板
export function updateTemplate(templateId, data) {
  return request({
    url: `/api/llm/prompts/templates/${templateId}`,
    method: 'put',
    data
  })
}

// 删除模板
export function deleteTemplate(templateId) {
  return request({
    url: `/api/llm/prompts/templates/${templateId}`,
    method: 'delete'
  })
}

// 复制模板
export function copyTemplate(templateId, data) {
  return request({
    url: `/api/llm/prompts/templates/${templateId}/copy`,
    method: 'post',
    data
  })
}

// 获取指定类型的默认模板
export function getDefaultTemplate(promptType) {
  return request({
    url: `/api/llm/prompts/templates/default/${promptType}`,
    method: 'get'
  })
}

// 按类型分页获取模板
export function getTemplatesByType(promptType, params) {
  return request({
    url: `/api/llm/prompts/templates/by-type/${promptType}`,
    method: 'get',
    params
  })
}

// 批量导入模板
export function importTemplates(data) {
  return request({
    url: '/api/llm/prompts/templates/import',
    method: 'post',
    data
  })
}

// 导出全部模板
export function exportTemplates() {
  return request({
    url: '/api/llm/prompts/templates/export',
    method: 'get'
  })
}

// 获取模板文件信息
export function getTemplateFiles() {
  return request({
    url: '/api/llm/prompts/templates/files',
    method: 'get'
  })
}

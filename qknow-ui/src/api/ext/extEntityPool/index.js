import request from '@/utils/request'

// 查询实体池列表
export function listEntityPool(query) {
  return request({
    url: '/ext/entityPool/page',
    method: 'get',
    params: query
  })
}

// 查询实体池详细
export function getEntityPool(id) {
  return request({
    url: '/ext/entityPool/get',
    method: 'get',
    params: { id }
  })
}

// 新增实体池
export function addEntityPool(data) {
  return request({
    url: '/ext/entityPool/create',
    method: 'post',
    data: data
  })
}

// 修改实体池
export function updateEntityPool(data) {
  return request({
    url: '/ext/entityPool/update',
    method: 'put',
    data: data
  })
}

// 删除实体池
export function delEntityPool(id) {
  return request({
    url: '/ext/entityPool/delete',
    method: 'delete',
    params: { id }
  })
}

// 导出实体池
export function exportEntityPool(query) {
  return request({
    url: '/ext/entityPool/export',
    method: 'post',
    data: query
  })
}

// 处理实体（确认或拒绝）
export function processEntity(id, status, remark) {
  return request({
    url: '/ext/entityPool/process',
    method: 'post',
    params: { id, status, remark }
  })
}

// 实体消歧 - 获取候选实体
export function disambiguateEntity(entityPoolId, topK = 5) {
  return request({
    url: '/ext/entityPool/disambiguate',
    method: 'post',
    params: { entityPoolId, topK }
  })
}

// 实体消歧 - 确认消歧结果
export function confirmDisambiguation(entityPoolId, candidateId, remark) {
  return request({
    url: '/ext/entityPool/confirm-disambiguation',
    method: 'post',
    params: { entityPoolId, candidateId, remark }
  })
}

// 批量处理实体（确认或拒绝）
export function batchProcessEntities(idList, status, remark) {
  return request({
    url: '/ext/entityPool/batch-process',
    method: 'post',
    data: { idList, status, remark }
  })
}

// 获取候选实体详细信息
export function getCandidateEntityDetails(candidateId) {
  return request({
    url: '/ext/entityPool/candidate-details',
    method: 'get',
    params: { candidateId }
  })
}

// 合并实体信息
export function mergeEntityInfo(entityPoolId, candidateId, mergeFields, remark) {
  return request({
    url: '/ext/entityPool/merge-entity',
    method: 'post',
    params: { entityPoolId, candidateId, remark },
    data: mergeFields
  })
} 
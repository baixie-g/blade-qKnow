import request from '@/utils/request'

// 查询关系池列表
export function listRelationshipPool(query) {
  return request({
    url: '/ext/relationshipPool/page',
    method: 'get',
    params: query
  })
}

// 查询关系池详细
export function getRelationshipPool(id) {
  return request({
    url: '/ext/relationshipPool/get',
    method: 'get',
    params: { id }
  })
}

// 新增关系池
export function addRelationshipPool(data) {
  return request({
    url: '/ext/relationshipPool/create',
    method: 'post',
    data: data
  })
}

// 修改关系池
export function updateRelationshipPool(data) {
  return request({
    url: '/ext/relationshipPool/update',
    method: 'put',
    data: data
  })
}

// 删除关系池
export function delRelationshipPool(id) {
  return request({
    url: '/ext/relationshipPool/delete',
    method: 'delete',
    params: { id }
  })
}

// 导出关系池
export function exportRelationshipPool(query) {
  return request({
    url: '/ext/relationshipPool/export',
    method: 'post',
    data: query
  })
}

// 处理关系（确认或拒绝）
export function processRelationship(id, status, remark) {
  return request({
    url: '/ext/relationshipPool/process',
    method: 'post',
    params: { id, status, remark }
  })
}

// 批量处理关系（确认或拒绝）
export function batchProcessRelationships(idList, status, remark) {
  return request({
    url: '/ext/relationshipPool/batch-process',
    method: 'post',
    data: { idList, status, remark }
  })
} 
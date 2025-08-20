import request from '@/utils/request'

// 查询统一数据源列表
export function listUnifiedDatasource(query) {
  return request({
    url: '/dm/dmDatasource/unified/list',
    method: 'get',
    params: query
  })
}

// 获取统一数据源详情
export function getUnifiedDatasource(id) {
  return request({
    url: '/dm/dmDatasource/unified/' + id,
    method: 'get'
  })
}

// 新增统一数据源
export function addUnifiedDatasource(data) {
  return request({
    url: '/dm/dmDatasource/unified',
    method: 'post',
    data: data
  })
}

// 修改统一数据源
export function updateUnifiedDatasource(data) {
  return request({
    url: '/dm/dmDatasource/unified',
    method: 'put',
    data: data
  })
}

// 删除统一数据源
export function delUnifiedDatasource(id) {
  return request({
    url: '/dm/dmDatasource/unified/' + id,
    method: 'delete'
  })
}

// 测试统一数据源连接
export function testUnifiedDatasourceConnection(id) {
  return request({
    url: '/dm/dmDatasource/unified/testConnection/' + id,
    method: 'get'
  })
}

// 获取所有 Neo4j 数据源
export function getNeo4jDatasources() {
  return request({
    url: '/dm/dmDatasource/unified/neo4j',
    method: 'get'
  })
}

// 获取所有关系型数据源
export function getRelationalDatasources() {
  return request({
    url: '/dm/dmDatasource/unified/relational',
    method: 'get'
  })
}

// 从 EXT 同步 Neo4j 数据源到统一表
export function syncFromExt() {
  return request({
    url: '/dm/dmDatasource/unified/syncFromExt',
    method: 'post'
  })
}

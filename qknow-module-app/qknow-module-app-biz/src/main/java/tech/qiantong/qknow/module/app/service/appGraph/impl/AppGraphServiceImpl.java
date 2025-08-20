package tech.qiantong.qknow.module.app.service.appGraph.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.common.utils.StringUtils;
import tech.qiantong.qknow.module.app.controller.admin.appGraph.vo.AppGraphPageReqVO;
import tech.qiantong.qknow.module.app.controller.admin.appGraph.vo.AppGraphRelationshipSaveReqVO;
import tech.qiantong.qknow.module.app.controller.admin.appGraph.vo.AppGraphVO;
import tech.qiantong.qknow.module.app.controller.admin.appGraph.vo.DeleteNodeAttributeVO;
import tech.qiantong.qknow.module.app.enums.ReleaseStatus;
import tech.qiantong.qknow.module.app.service.appGraph.AppGraphService;
import tech.qiantong.qknow.neo4j.domain.DynamicEntity;
import tech.qiantong.qknow.neo4j.domain.relationship.DynamicEntityRelationship;
import tech.qiantong.qknow.neo4j.enums.Neo4jLabelEnum;
import tech.qiantong.qknow.neo4j.repository.DynamicRepository;
import tech.qiantong.qknow.neo4j.utils.Convert;
import tech.qiantong.qknow.neo4j.wrapper.Neo4jBuildWrapper;
import tech.qiantong.qknow.neo4j.wrapper.Neo4jQueryWrapper;

import javax.annotation.Resource;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import tech.qiantong.qknow.module.app.dal.dataobject.ext.ExtDatasourceLiteDO;
import tech.qiantong.qknow.module.app.dal.mapper.ext.ExtDatasourceLiteMapper;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tech.qiantong.qknow.neo4j.enums.Neo4jLabelEnum.get;

/**
 * 图谱
 */
@Slf4j
@Service
public class AppGraphServiceImpl implements AppGraphService {

    @Resource
    private DynamicRepository dynamicRepository;
    
    @Autowired
    private Neo4jClient neo4jClient;

    @Resource
    private ExtDatasourceLiteMapper extDatasourceLiteMapper;

    /**
     * 获取图谱数据
     *
     * @param appGraphVO
     * @return
     */
    @Override
    public Map<String, Object> getGraph(AppGraphVO appGraphVO) {
        // 如果传入了datasourceId，临时构建一个Neo4jClient用于该次查询
        Neo4jClient effectiveClient = this.neo4jClient;
        Driver tempDriver = null;
        try {
            if (appGraphVO.getDatasourceId() != null) {
                ExtDatasourceLiteDO ds = extDatasourceLiteMapper.selectById(appGraphVO.getDatasourceId());
                if (ds != null && ds.getHost() != null && ds.getPort() != null) {
                    String uri = String.format("bolt://%s:%d", ds.getHost(), ds.getPort());
                    String user = ds.getUsername() == null ? "neo4j" : ds.getUsername();
                    String pwd = ds.getPassword() == null ? "neo4j" : ds.getPassword();
                    tempDriver = GraphDatabase.driver(uri, AuthTokens.basic(user, pwd));
                    effectiveClient = Neo4jClient.create(tempDriver);
                }
            }
        } catch (Exception ignored) {}
        // 特殊处理：当entityType为0时，查询所有已发布的Entity标签实体（图谱探索模式）
        if (appGraphVO.getEntityType() != null && appGraphVO.getEntityType() == 0) {
            log.info("图谱探索模式：查询所有已发布的Entity标签实体");
            
            // 先测试查询所有Entity标签的节点
            String testQuery = "MATCH (n:Entity) RETURN n LIMIT 5";
            List<Map<String, Object>> testResults = new ArrayList<>(effectiveClient.query(testQuery)
                .fetch()
                .all());
            log.info("测试查询结果数量: {}", testResults.size());
            
            // 如果找到了Entity节点，再查询带条件的
            if (testResults.isEmpty()) {
                log.warn("未找到任何Entity标签的节点，尝试查询所有节点");
                String allNodesQuery = "MATCH (n) RETURN labels(n) as labels, n.name as name LIMIT 10";
                List<Map<String, Object>> allNodesResults = new ArrayList<>(effectiveClient.query(allNodesQuery)
                    .fetch()
                    .all());
                log.info("所有节点查询结果: {}", allNodesResults);
            }
            
            // 使用Neo4jClient直接执行Cypher查询
            String cypherQuery = "MATCH (n:Entity) WHERE n.release_status = $releaseStatus " +
                               "WITH collect(n) as nodes " +
                               "UNWIND nodes as n " +
                               "OPTIONAL MATCH (n)-[r]->(m:Entity) " +
                               "WHERE m.release_status = $releaseStatus " +
                               "RETURN n, collect(r) as relationships, collect(m) as relatedNodes";
            
            Map<String, Object> params = Maps.newHashMap();
            params.put("releaseStatus", ReleaseStatus.PUBLISHED.getValue());
            
            log.info("执行Cypher查询: {}", cypherQuery);
            log.info("查询参数: {}", params);
            
            // 执行查询并手动构建结果
            List<Map<String, Object>> results = new ArrayList<>(effectiveClient.query(cypherQuery)
                .bindAll(params)
                .fetch()
                .all());
            
            log.info("查询结果数量: {}", results.size());
            
            // 手动构建实体和关系数据
            List<Map<String, Object>> entities = Lists.newArrayList();
            List<Map<String, Object>> relationships = Lists.newArrayList();
            Set<String> processedEntities = new HashSet<>();
            Set<String> processedRelationships = new HashSet<>();
            
            for (Map<String, Object> result : results) {
                org.neo4j.driver.types.Node node = (org.neo4j.driver.types.Node) result.get("n");
                List<org.neo4j.driver.types.Relationship> rels = (List<org.neo4j.driver.types.Relationship>) result.get("relationships");
                List<org.neo4j.driver.types.Node> relatedNodes = (List<org.neo4j.driver.types.Node>) result.get("relatedNodes");
                
                if (node != null) {
                    String nodeId = String.valueOf(node.id());
                    
                    // 避免重复处理同一个实体
                    if (!processedEntities.contains(nodeId)) {
                        Map<String, Object> entity = Maps.newHashMap();
                        entity.put("id", String.valueOf(node.id())); // 确保ID是字符串格式
                        
                        // 安全地获取name属性
                        if (node.get("name") != null) {
                            entity.put("name", node.get("name").asString());
                        } else {
                            entity.put("name", "未知实体");
                        }
                        
                        // 安全地获取type属性
                        if (node.get("type") != null) {
                            entity.put("type", node.get("type").asString());
                        } else {
                            entity.put("type", "未知类型");
                        }
                        
                        // 添加前端期望的属性
                        // 根据type设置schemaId，用于颜色区分
                        String entityType = entity.get("type").toString();
                        int schemaId = 1; // 默认
                        if ("人物".equals(entityType) || "person".equals(entityType)) {
                            schemaId = 9; // 对应schema中的id: 9
                        } else if ("学校".equals(entityType) || "organization".equals(entityType)) {
                            schemaId = 10; // 对应schema中的id: 10
                        } else if ("公司".equals(entityType) || "company".equals(entityType)) {
                            schemaId = 11; // 对应schema中的id: 11
                        } else if ("地点".equals(entityType) || "location".equals(entityType)) {
                            schemaId = 13; // 对应schema中的id: 13
                        } else if ("技术".equals(entityType) || "technology".equals(entityType)) {
                            schemaId = 14; // 对应schema中的id: 14
                        }
                        entity.put("schemaId", schemaId);
                        entity.put("entityType", 2); // 默认entityType为2（非结构化）
                        entity.put("releaseStatus", 1); // 已发布状态
                        
                        // 添加所有节点属性（在设置schemaId之后）
                        entity.putAll(node.asMap());
                        
                        // 确保schemaId和id不被覆盖
                        entity.put("schemaId", schemaId);
                        entity.put("id", String.valueOf(node.id())); // 确保ID是字符串格式
                        
                        entities.add(entity);
                        processedEntities.add(nodeId);
                        
                        log.info("处理实体: id={}, name={}, type={}, schemaId={}", node.id(), entity.get("name"), entity.get("type"), entity.get("schemaId"));
                    }
                    
                    // 处理关系
                    if (rels != null && !rels.isEmpty()) {
                        for (int i = 0; i < rels.size(); i++) {
                            org.neo4j.driver.types.Relationship rel = rels.get(i);
                            org.neo4j.driver.types.Node relatedNode = relatedNodes.get(i);
                            
                            if (rel != null && relatedNode != null) {
                                String relId = String.valueOf(rel.id());
                                
                                // 避免重复处理同一个关系
                                if (!processedRelationships.contains(relId)) {
                                    Map<String, Object> relationship = Maps.newHashMap();
                                    relationship.put("id", String.valueOf(rel.id())); // 确保ID是字符串格式
                                    relationship.put("startId", String.valueOf(rel.startNodeId())); // 确保ID是字符串格式
                                    relationship.put("endId", String.valueOf(rel.endNodeId())); // 确保ID是字符串格式
                                    relationship.put("relationType", rel.type());
                                    
                                    // 获取源节点名称
                                    if (node.get("name") != null) {
                                        relationship.put("startName", node.get("name").asString());
                                    } else {
                                        relationship.put("startName", "未知实体");
                                    }
                                    
                                    // 安全地获取目标节点的name
                                    if (relatedNode.get("name") != null) {
                                        relationship.put("endName", relatedNode.get("name").asString());
                                    } else {
                                        relationship.put("endName", "未知实体");
                                    }
                                    
                                    relationships.add(relationship);
                                    processedRelationships.add(relId);
                                    log.info("处理关系: {} -> {} -> {}", relationship.get("startName"), rel.type(), relationship.get("endName"));
                                }
                            }
                        }
                    }
                }
            }
            
            Map<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("entities", entities);
            hashMap.put("relationships", relationships);
            
            log.info("返回实体数量: {}", entities.size());
            log.info("返回关系数量: {}", relationships.size());
            
            closeDriverQuietly(tempDriver);
            return hashMap;
        } else {
            Neo4jQueryWrapper<DynamicEntity> build = new Neo4jQueryWrapper<>(DynamicEntity.class);
            Neo4jLabelEnum neo4jLabelEnum = get(appGraphVO.getEntityType());
            
            // 添加空值检查，避免 NullPointerException
            if (neo4jLabelEnum == null) {
                log.warn("无效的 entityType: {}, 使用默认的 DYNAMICENTITY 查询", appGraphVO.getEntityType());
                neo4jLabelEnum = Neo4jLabelEnum.DYNAMICENTITY;
            }
            
            if (Neo4jLabelEnum.DYNAMICENTITY.eq(neo4jLabelEnum.getCode())) {
                build.eq("release_status", ReleaseStatus.PUBLISHED.getValue()); //发布状态
            } else {
                build.addLabels(neo4jLabelEnum.getLabel());
                if(appGraphVO.getEntityId() != null){
                    build.eq(neo4jLabelEnum.getEntityIdName(), appGraphVO.getEntityId());
                }
            }
            
            List<DynamicEntity> dynamicEntities = dynamicRepository.find(build);
            JSONObject dynamicEntityJSONObject = Convert.toDynamicEntityJSONObject(dynamicEntities);
            Map<String, Object> hashMap = Maps.newHashMap();
            hashMap.put("entities", dynamicEntityJSONObject.get("entities"));
            hashMap.put("relationships", dynamicEntityJSONObject.get("relationships"));
            closeDriverQuietly(tempDriver);
            return hashMap;
        }
    }

    /** 关闭临时driver */
    private void closeDriverQuietly(Driver driver) {
        try { if (driver != null) driver.close(); } catch (Exception ignored) {}
    }

    @Override
    public PageResult<JSONObject> getGraphPage(AppGraphPageReqVO appGraphVO) {
        Neo4jQueryWrapper<DynamicEntity> build = new Neo4jQueryWrapper<>(DynamicEntity.class);
        
        // 特殊处理：当entityType为0时，查询所有已发布的Entity标签实体（图谱探索模式）
        if (appGraphVO.getEntityType() != null && appGraphVO.getEntityType() == 0) {
            log.info("图谱探索模式：查询所有已发布的Entity标签实体");
            // 添加Entity标签
            build.addLabels("Entity");
            // 只查询已发布的实体
            build.eq("release_status", ReleaseStatus.PUBLISHED.getValue());
        } else {
            Neo4jLabelEnum neo4jLabelEnum = get(appGraphVO.getEntityType());
            
            // 添加空值检查，避免 NullPointerException
            if (neo4jLabelEnum == null) {
                log.warn("无效的 entityType: {}, 使用默认的 DYNAMICENTITY 查询", appGraphVO.getEntityType());
                build.eq("release_status", ReleaseStatus.PUBLISHED.getValue());
            } else {
                build.addLabels(neo4jLabelEnum.getLabel());
                if(appGraphVO.getEntityId() != null){
                    build.eq(neo4jLabelEnum.getEntityIdName(), appGraphVO.getEntityId());
                }
            }
        }
        
        if (StringUtils.isNotEmpty(appGraphVO.getName())) {
            build.like("name", appGraphVO.getName());
        }
        build.page(appGraphVO.getPageNum(), appGraphVO.getPageSize());
        PageResult<DynamicEntity> page = dynamicRepository.findPage(build);
        JSONObject dynamicEntityJSONObject = Convert.toDynamicEntityJSONObject(page.getRows());
        JSONArray jsonArray = dynamicEntityJSONObject.getJSONArray("entities");
        return new PageResult<>(jsonArray.toList(JSONObject.class), page.getTotal());
    }

    /**
     * 统计 (实体数量,关系类型数量,三元组数量)
     * @param appGraphVO
     * @return
     */
    public Map<String, Object> getGraphDataStatistics(AppGraphVO appGraphVO){
        List<DynamicEntity> dynamicEntities = dynamicRepository.findAll();
        JSONObject dynamicEntityJSONObject = Convert.toDynamicEntityJSONObject(dynamicEntities);
        JSONArray entities = dynamicEntityJSONObject.getJSONArray("entities");
        JSONArray relationships = dynamicEntityJSONObject.getJSONArray("relationships");
        long uniqueRelationTypeCount = relationships.stream()
                .map(relationship -> ((JSONObject) relationship).getString("relationType"))
                .distinct()
                .count();

        Map<String, Object> result = Maps.newHashMap();
        result.put("entityCount", entities.size());   // 实体数量
        result.put("relationshipCount", uniqueRelationTypeCount);  // 去重后的关系类型数量
        result.put("tripletCount", relationships.size());  // 三元组数量
        return result;
    }

    /**
     * 新增实体
     *
     * @return
     */
    public Boolean addNode(JSONArray jsonArray) {
        Set<Long> idSet = jsonArray.stream()
                .map(obj -> (JSONObject) obj)
                .filter(jsonObject -> jsonObject.containsKey("id") && jsonObject.get("id") != null)
                .map(jsonObject -> jsonObject.getLong("id"))
                .collect(Collectors.toSet());
        List<DynamicEntity> entityList = dynamicRepository.findAllById(idSet);
        Map<Long, DynamicEntity> entityMap = entityList.stream()
                .collect(Collectors.toMap(DynamicEntity::getNeo4jId, entity -> entity));
        List<DynamicEntity> dynamicEntityList = Lists.newArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Long id = jsonObject.getLong("id");
            DynamicEntity dynamicEntity;
            if (id != null) {
                dynamicEntity = entityMap.get(id);
            } else {
                dynamicEntity = new DynamicEntity();
                dynamicEntity.putDynamicProperties("release_status", ReleaseStatus.UNPUBLISHED.getValue());
            }
            // 标签
            dynamicEntity.addLabels(Neo4jLabelEnum.get(jsonObject.getInteger("entityType")).getLabel());
            // 动态属性
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                if (!"id".equals(entry.getKey())) {
                    dynamicEntity.putDynamicProperties(Convert.camelToSnake(entry.getKey()), entry.getValue());
                }
            }
            dynamicEntityList.add(dynamicEntity);
        }
        dynamicRepository.saveAll(dynamicEntityList);
        return true;
    }

    /**
     * 新增三元组 TODO
     * 根据起点id和结点id创建关系
     *
     * @param graphRelationshipSaveReqVOList
     * @return
     */
    public Boolean addTripletRel(List<AppGraphRelationshipSaveReqVO> graphRelationshipSaveReqVOList) {
        // 取出所有的实体id
        Set<Long> idSet = graphRelationshipSaveReqVOList.stream()
                .flatMap(user -> Stream.of(user.getEntityId1(), user.getEntityId2()))
                .collect(Collectors.toSet());
        // 查询出所有的实体
        List<DynamicEntity> graphEntityList = dynamicRepository.findAllById(idSet);
        Map<Long, DynamicEntity> graphEntityMap = graphEntityList.stream()
                .collect(Collectors.toMap(DynamicEntity::getNeo4jId, graphEntity -> graphEntity));
        List<Long> relationshipIds = graphRelationshipSaveReqVOList.stream()
                .map(AppGraphRelationshipSaveReqVO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 删除关系
        dynamicRepository.deleteRelationshipsByIds(relationshipIds);
        // 新增/更新关系
        graphRelationshipSaveReqVOList.forEach(appGraphRelationshipSaveReqVO -> {
            DynamicEntity graphEntity1 = graphEntityMap.get(appGraphRelationshipSaveReqVO.getEntityId1());
            DynamicEntity graphEntity2 = graphEntityMap.get(appGraphRelationshipSaveReqVO.getEntityId2());
            Map<String, List<DynamicEntityRelationship>> entityMap = graphEntity1.getRelationshipEntityMap();

            List<DynamicEntityRelationship> graphEntityRelationshipList;
            if(entityMap == null) {
                entityMap = Maps.newHashMap();
            }
            // 如果有关系则新增一个关系
            if (entityMap.containsKey(appGraphRelationshipSaveReqVO.getRelationshipType())) {
                graphEntityRelationshipList = entityMap.get(appGraphRelationshipSaveReqVO.getRelationshipType());
            } else {
                graphEntityRelationshipList = Lists.newArrayList();
            }
            DynamicEntityRelationship graphEntityRelationship = new DynamicEntityRelationship();
            graphEntityRelationship.setEndNode(graphEntity2);
            graphEntityRelationshipList.add(graphEntityRelationship);
            entityMap.put(appGraphRelationshipSaveReqVO.getRelationshipType (), graphEntityRelationshipList);
            graphEntity1.setRelationshipEntityMap(entityMap);
        });
        dynamicRepository.saveAll(graphEntityList);
        return true;
    }

    /**
     * 根据节点id和属性的id删除属性
     *
     * @param deleteNodeAttributeVO
     * @return
     */
    public AjaxResult deleteNodeAttributeById(DeleteNodeAttributeVO deleteNodeAttributeVO) {
        dynamicRepository.deleteNodeAttributeById(deleteNodeAttributeVO.getNodeId(), deleteNodeAttributeVO.getAttributeKey());
        return AjaxResult.success("操作成功");
    }

    /**
     * 发布 / 取消发布
     *
     * @return
     */
    public AjaxResult updateReleaseStatus(AppGraphVO appGraphVO) {
        Neo4jLabelEnum neo4jLabelEnum = get(appGraphVO.getEntityType());
        if (neo4jLabelEnum != null) {
            String label = neo4jLabelEnum.getLabel();
            //条件map
            HashMap<String, Object> paramMap = Maps.newHashMap();
            paramMap.put(neo4jLabelEnum.getEntityIdName(), appGraphVO.getEntityId());
            //修改属性内容
            HashMap<String, Object> updateMap = Maps.newHashMap();
            updateMap.put("release_status", appGraphVO.getReleaseStatus());//发布状态 0未发布 1已发布
            dynamicRepository.updateQuery(new Neo4jBuildWrapper<>(DynamicEntity.class), label, paramMap, updateMap);
        }
        return AjaxResult.success("操作成功");
    }

    /**
     * 根据节点id删除对应的节点
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult deleteNode(Long id) {
        dynamicRepository.deleteNodeById(id);
        return AjaxResult.success("删除成功");
    }

    @Override
    public Boolean deleteNodeByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        dynamicRepository.deleteNodeByIds(idList);
        return true;
    }


    /**
     * 根据关系id删除关系
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult deleteRelationshipById(Long id) {
        dynamicRepository.deleteRelationshipById(id);
        return AjaxResult.success("删除成功");
    }

    /**
     * 根据关系ids删除关系
     *
     * @param ids
     * @return
     */
    @Override
    public AjaxResult deleteRelationshipsByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        dynamicRepository.deleteRelationshipsByIds(idList);
        return AjaxResult.success("删除成功");
    }
}

package tech.qiantong.qknow.module.ext.service.extUnstructTask.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.common.exception.ServiceException;
import tech.qiantong.qknow.common.utils.StringUtils;
import tech.qiantong.qknow.common.utils.object.BeanUtils;
import tech.qiantong.qknow.module.app.enums.ReleaseStatus;
import tech.qiantong.qknow.module.ext.controller.admin.extUnstructTask.vo.ExtUnstructTaskPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extUnstructTask.vo.ExtUnstructTaskRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extUnstructTask.vo.ExtUnstructTaskSaveReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extUnstructTaskDocRel.vo.ExtUnstructTaskDocRelPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extUnstructTaskDocRel.vo.ExtUnstructTaskDocRelSaveReqVO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extUnstructTask.ExtUnstructTaskDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extUnstructTaskDocRel.ExtUnstructTaskDocRelDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extUnstructTaskText.ExtUnstructTaskTextDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extraction.ExtExtractionDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.unstructTaskRelation.ExtUnstructTaskRelationDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extRelationshipPool.ExtRelationshipPoolDO;
import tech.qiantong.qknow.module.ext.dal.mapper.extUnstructTask.ExtUnstructTaskMapper;
import tech.qiantong.qknow.module.ext.dal.mapper.extUnstructTaskText.ExtUnstructTaskTextMapper;
import tech.qiantong.qknow.module.ext.extEnum.ExtTaskStatus;
import tech.qiantong.qknow.module.ext.extEnum.ExtractType;
import tech.qiantong.qknow.module.ext.extEnum.UnstructTypeEnums;
import tech.qiantong.qknow.module.ext.service.deepke.DeepkeExtractionService;
import tech.qiantong.qknow.module.ext.service.extraction.ExtractionService;
import tech.qiantong.qknow.module.ext.service.extUnstructTask.IExtUnstructTaskService;
import tech.qiantong.qknow.module.ext.service.extUnstructTaskDocRel.IExtUnstructTaskDocRelService;
import tech.qiantong.qknow.module.ext.service.extEntityPool.IExtEntityPoolService;
import tech.qiantong.qknow.module.ext.service.extRelationshipPool.IExtRelationshipPoolService;
import tech.qiantong.qknow.module.ext.service.neo4j.service.ExtNeo4jService;
import tech.qiantong.qknow.module.ext.service.unstructTaskRelation.IExtUnstructTaskRelationService;
import tech.qiantong.qknow.module.kmc.api.service.IKmcApiService;
import tech.qiantong.qknow.module.kmc.dal.dataobject.document.KmcDocumentDO;
import tech.qiantong.qknow.module.kmc.service.kmcDocument.IKmcDocumentService;
import tech.qiantong.qknow.redis.service.IRedisService;
import tech.qiantong.qknow.module.ext.dal.dataobject.extSchema.ExtSchemaDO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extSchemaRelation.ExtSchemaRelationDO;
import tech.qiantong.qknow.module.ext.service.extSchema.IExtSchemaService;
import tech.qiantong.qknow.module.ext.service.extSchemaRelation.IExtSchemaRelationService;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 非结构化抽取任务Service业务层处理
 *
 * @author qknow
 * @date 2025-02-18
 */
@Slf4j
@Service
public class ExtUnstructTaskServiceImpl extends ServiceImpl<ExtUnstructTaskMapper, ExtUnstructTaskDO> implements IExtUnstructTaskService {
    @Resource
    private ExtUnstructTaskMapper extUnstructTaskMapper;
    @Resource
    private IKmcApiService kmcApiService;
    @Resource
    private IKmcDocumentService kmcDocumentService;
    @Resource
    private IExtUnstructTaskDocRelService extUnstructTaskDocRelService;
    @Resource
    private IExtUnstructTaskRelationService extUnstructTaskRelationService;
    @Resource
    private DeepkeExtractionService deepkeExtractionService;
    @Resource
    private ExtractionService extractionService;
    @Resource
    private IExtEntityPoolService extEntityPoolService;
    @Resource
    private IExtRelationshipPoolService extRelationshipPoolService;
    @Resource
    private ExtUnstructTaskTextMapper extUnstructTaskTextMapper;
    @Resource
    private ExtNeo4jService extNeo4jService;
    @Resource
    private IRedisService redisService;
    @Resource
    private IExtSchemaService extSchemaService;
    @Resource
    private IExtSchemaRelationService extSchemaRelationService;
    @Value("${unstruct.type}")
    private String unstructType;

    /**
     * 执行任务抽取，先放入redis队列中
     * @author shaun
     * @date 2025/05/26 15:48
     * @param createReqVO
     * @return tech.qiantong.qknow.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult executeExtraction(ExtUnstructTaskSaveReqVO createReqVO) {
        // 任务id
        Long taskId = createReqVO.getId();

        // 根据任务id获取非结构化抽取任务
        ExtUnstructTaskDO unstructTaskDO = extUnstructTaskMapper.selectById(taskId);

        // 校验任务状态
        if (ReleaseStatus.PUBLISHED.getValue().equals(unstructTaskDO.getPublishStatus())) {
            throw new RuntimeException("已发布状态不能重新执行抽取");
        }
        if (ExtTaskStatus.INPROGRESS.getValue().equals(unstructTaskDO.getStatus())) {
            throw new RuntimeException("执行中不能重新执行抽取");
        }

        // 校验任务是否关联文件
        ExtUnstructTaskDocRelPageReqVO docRelPageReqVO = new ExtUnstructTaskDocRelPageReqVO();
        docRelPageReqVO.setTaskId(taskId);
        docRelPageReqVO.setDelFlag(false);
        PageResult<ExtUnstructTaskDocRelDO> docRelPage = extUnstructTaskDocRelService.getExtUnstructTaskDocRelPage(docRelPageReqVO);
        if (docRelPage.getRows().size() == 0) {
            return AjaxResult.error("当前任务没有关联需要抽取的文件");
        }

        // 变更任务执行状态
        unstructTaskDO.setStatus(ExtTaskStatus.INPROGRESS.getValue());
        extUnstructTaskMapper.updateExtUnstructTask(unstructTaskDO);

        // 放入redis队列中, 按顺序 一个一个执行
        redisService.leftPush("ext_unstruck", String.valueOf(taskId));
        return AjaxResult.success("操作成功,执行中");
    }

    /**
     * 定时任务，消费队列任务, 执行任务抽取
     * @author shaun
     * @date 2025/05/23 11:21
     * @return void
     */
    @Override
    public void consumeQueue() {
        // 取出队列中的任务id
        String taskId = redisService.leftPop("ext_unstruck");

        // 判断队列是否为空
        if (StringUtils.isEmpty(taskId)) {
            log.info("队列中没有等待抽取的任务");
            return;
        }

        // 开始抽取
        log.info("----------------非结构化抽取任务开始-----------------------taskId:{}", taskId);

        // 获取抽取任务
        ExtUnstructTaskDO unstructTaskDO = extUnstructTaskMapper.selectById(taskId);

        // 获取任务关联的文件
        ExtUnstructTaskDocRelPageReqVO docRelPageReqVO = new ExtUnstructTaskDocRelPageReqVO();
        docRelPageReqVO.setTaskId(Long.valueOf(taskId));
        PageResult<ExtUnstructTaskDocRelDO> docRelPage = extUnstructTaskDocRelService.getExtUnstructTaskDocRelPage(docRelPageReqVO);
        List<ExtUnstructTaskDocRelDO> taskDocRelDOList = BeanUtils.toBean(docRelPage.getRows(), ExtUnstructTaskDocRelDO.class);

        try {
            // 根据配置文件判断使用哪种方式进行抽取
            if (UnstructTypeEnums.MODEL.eq(unstructType)) {
                // TODO 使用大模型抽取，开源版本暂未支持，如有需要请咨询相关管理人员
                log.warn("大模型抽取暂未支持～");
            } else {
                // 使用DeepKE进行抽取
                this.execExtTask(unstructTaskDO, taskDocRelDOList);
            }

            log.info("----------------非结构化抽取任务结束-----------------------taskId:{}", taskId);

            // 处理任务完成后的状态更新
            unstructTaskDO.setStatus(ExtTaskStatus.EXECUTED.getValue());
            extUnstructTaskMapper.updateExtUnstructTask(unstructTaskDO);
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常并打印日志
            log.error("非结构化任务抽取处理失败: {}", e.getMessage());
            unstructTaskDO.setStatus(ExtTaskStatus.ERROR.getValue());
            extUnstructTaskMapper.updateExtUnstructTask(unstructTaskDO);
        }
    }

    /**
     * 执行抽取任务
     * @author shaun
     * @date 2025/05/27 10:22
     * @param unstructTaskDO
     * @param taskDocRelDOList
     * @return void
     */
    private void execExtTask(ExtUnstructTaskDO unstructTaskDO, List<ExtUnstructTaskDocRelDO> taskDocRelDOList) throws Exception {
        // 获取所有的文件，存放至map中
        Map<Long, KmcDocumentDO> documentMap = kmcDocumentService.getKmcDocumentMap();

        // 删除池子中之前抽取相关的数据, 如果有的话
        // TODO: 添加删除池子数据的方法
        // extEntityPoolService.deleteByTaskId(unstructTaskDO.getId());
        // extRelationshipPoolService.deleteByTaskId(unstructTaskDO.getId());
        
        // 删除mysql中之前抽取的段落相关的数据, 如果有的话
        extUnstructTaskTextMapper.deleteByTaskId(unstructTaskDO.getId());

        // 遍历任务关联的文件
        for (ExtUnstructTaskDocRelDO extUnstructTaskDocRelDO : taskDocRelDOList) {
            KmcDocumentDO kmcDocument = documentMap.get(extUnstructTaskDocRelDO.getDocId());

            // 拼接文件地址
            String fileUrl = "http://127.0.0.1:8090/profile" + kmcDocument.getPath();

            // 创建 URL 对象
            URL url = new URL(fileUrl);
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法
            connection.setRequestMethod("GET");
            // 获取输入流
            InputStream inputStream = connection.getInputStream();
            // 使用 XWPFDocument 解析 docx 文件
            XWPFDocument document = new XWPFDocument(inputStream);
            // 获取文档中的段落
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            int i = 0;
            // 输出每个段落的文本内容
            for (XWPFParagraph paragraph : paragraphs) {
                i++;
                String text = paragraph.getText();
                if (StringUtils.isBlank(text)) {
                    continue;
                }
                log.info("============>抽取文本: {}", text);

                // 使用新的抽取API
                log.info("============调用新的抽取API开始抽取============");
                
                // 从任务配置中获取schema信息
                List<String> schemaList = getTaskSchemaList(unstructTaskDO.getId());
                if (schemaList == null || schemaList.isEmpty()) {
                    log.warn("任务未配置抽取schema，跳过抽取");
                    continue;
                }
                
                AjaxResult ajaxResult = extractionService.extractWithTaskSchema(text, schemaList);
                log.info("============调用新的抽取API完成============");

                if (ajaxResult.isSuccess()) {
                    log.info("============>抽取文本成功：{}", text);
                    
                    // 生成统一的时间戳后缀，用于所有实体和关系的ID
                    long timestamp = System.currentTimeMillis();
                    String timestampSuffix = "_" + timestamp;
                    log.info("============ 生成时间戳后缀: {} ============", timestampSuffix);
                    
                    // 解析返回结果 - 修复FastJSON版本兼容性问题
                    Object dataObj = ajaxResult.get("data");
                    JSONObject result;
                    if (dataObj instanceof com.alibaba.fastjson.JSONObject) {
                        // 如果是旧版本的JSONObject，转换为新版本
                        result = JSON.parseObject(dataObj.toString());
                    } else {
                        result = (JSONObject) dataObj;
                    }
                    
                    JSONArray nodes = result.getJSONArray("nodes");
                    JSONArray relationships = result.getJSONArray("relationships");
                    
                    log.info("============>抽取到的实体：{}", nodes);
                    log.info("============>抽取到的关系：{}", relationships);
                    
                    // 保存实体到池子
                    List<ExtEntityPoolDO> entityPoolList = new ArrayList<>();
                    log.info("============ 开始保存实体到实体池 ============");
                    log.info("任务ID: {}, 文档ID: {}, 段落索引: {}", unstructTaskDO.getId(), extUnstructTaskDocRelDO.getDocId(), i);
                    
                    for (int j = 0; j < nodes.size(); j++) {
                        JSONObject node = nodes.getJSONObject(j);
                        
                        // 为实体ID添加时间戳后缀
                        String originalEntityId = node.getString("id");
                        String newEntityId = originalEntityId + timestampSuffix;
                        
                        // 处理aliases字段 - 确保是JSON字符串格式
                        String aliasesStr = "";
                        if (node.containsKey("aliases")) {
                            Object aliasesObj = node.get("aliases");
                            if (aliasesObj instanceof JSONArray) {
                                aliasesStr = aliasesObj.toString();
                            } else if (aliasesObj instanceof String) {
                                aliasesStr = (String) aliasesObj;
                            }
                        }
                        
                        // 处理attributes字段 - 确保是JSON字符串格式
                        String attributesStr = "";
                        if (node.containsKey("attributes")) {
                            Object attributesObj = node.get("attributes");
                            if (attributesObj instanceof JSONObject) {
                                attributesStr = attributesObj.toString();
                            } else if (attributesObj instanceof String) {
                                attributesStr = (String) attributesObj;
                            }
                        }
                        
                        ExtEntityPoolDO entityPool = ExtEntityPoolDO.builder()
                            .workspaceId(unstructTaskDO.getWorkspaceId())
                            .taskId(unstructTaskDO.getId())
                            .docId(extUnstructTaskDocRelDO.getDocId())
                            .paragraphIndex(i)
                            .entityId(newEntityId) // 使用带时间戳后缀的ID
                            .entityName(node.getString("name"))
                            .entityType(node.getString("type"))
                            .aliases(aliasesStr)
                            .definition(node.getString("definition"))
                            .attributes(attributesStr)
                            .status(0) // 待处理
                            .validFlag(true)
                            .delFlag(false)
                            .build();
                        // 设置父类字段
                        entityPool.setCreateBy(unstructTaskDO.getUpdateBy());
                        entityPool.setCreatorId(unstructTaskDO.getUpdaterId());
                        entityPool.setUpdateBy(unstructTaskDO.getUpdateBy());
                        entityPool.setUpdaterId(unstructTaskDO.getUpdaterId());
                        entityPool.setCreateTime(new Date());
                        entityPool.setUpdateTime(new Date());
                        entityPoolList.add(entityPool);
                        
                        log.info("准备保存实体: 原始ID={}, 新ID={}, 名称={}, 类型={}", 
                            originalEntityId, newEntityId, entityPool.getEntityName(), entityPool.getEntityType());
                    }
                    
                    // 批量保存实体到池子
                    if (!entityPoolList.isEmpty()) {
                        log.info("批量保存实体到实体池，数量: {}", entityPoolList.size());
                        try {
                            extEntityPoolService.batchSaveEntities(entityPoolList);
                            log.info("============ 实体池保存成功 ============");
                        } catch (Exception e) {
                            log.error("============ 实体池保存失败 ============");
                            log.error("保存实体异常: ", e);
                        }
                    } else {
                        log.info("没有实体需要保存到实体池");
                    }
                    
                    // 保存关系到池子
                    List<ExtRelationshipPoolDO> relationshipPoolList = new ArrayList<>();
                    log.info("============ 开始保存关系到关系池 ============");
                    
                    for (int j = 0; j < relationships.size(); j++) {
                        JSONObject relationship = relationships.getJSONObject(j);
                        
                        // 为关系的源实体和目标实体ID添加时间戳后缀
                        String originalSourceId = relationship.getString("source");
                        String originalTargetId = relationship.getString("target");
                        String newSourceId = originalSourceId + timestampSuffix;
                        String newTargetId = originalTargetId + timestampSuffix;
                        
                        ExtRelationshipPoolDO relationshipPool = ExtRelationshipPoolDO.builder()
                            .workspaceId(unstructTaskDO.getWorkspaceId())
                            .taskId(unstructTaskDO.getId())
                            .docId(extUnstructTaskDocRelDO.getDocId())
                            .paragraphIndex(i)
                            .sourceEntityId(newSourceId) // 使用带时间戳后缀的源实体ID
                            .targetEntityId(newTargetId) // 使用带时间戳后缀的目标实体ID
                            .relationshipType(relationship.getString("type"))
                            .status(0) // 待处理
                            .validFlag(true)
                            .delFlag(false)
                            .build();
                        // 设置父类字段
                        relationshipPool.setCreateBy(unstructTaskDO.getUpdateBy());
                        relationshipPool.setCreatorId(unstructTaskDO.getUpdaterId());
                        relationshipPool.setUpdateBy(unstructTaskDO.getUpdateBy());
                        relationshipPool.setUpdaterId(unstructTaskDO.getUpdaterId());
                        relationshipPool.setCreateTime(new Date());
                        relationshipPool.setUpdateTime(new Date());
                        relationshipPoolList.add(relationshipPool);
                        
                        log.info("准备保存关系: 原始源实体={}, 新源实体={}, 原始目标实体={}, 新目标实体={}, 关系类型={}", 
                            originalSourceId, newSourceId, originalTargetId, newTargetId, relationshipPool.getRelationshipType());
                    }
                    
                    // 批量保存关系到池子
                    if (!relationshipPoolList.isEmpty()) {
                        log.info("批量保存关系到关系池，数量: {}", relationshipPoolList.size());
                        try {
                            extRelationshipPoolService.batchSaveRelationships(relationshipPoolList);
                            log.info("============ 关系池保存成功 ============");
                        } catch (Exception e) {
                            log.error("============ 关系池保存失败 ============");
                            log.error("保存关系异常: ", e);
                        }
                    } else {
                        log.info("没有关系需要保存到关系池");
                    }

                    //把文字信息存储到数据库
                    ExtUnstructTaskTextDO taskTextDO = new ExtUnstructTaskTextDO();
                    taskTextDO.setValidFlag(false);
                    taskTextDO.setDelFlag(false);
                    taskTextDO.setWorkspaceId(unstructTaskDO.getWorkspaceId());
                    taskTextDO.setDocId(extUnstructTaskDocRelDO.getDocId());
                    taskTextDO.setTaskId(unstructTaskDO.getId());
                    taskTextDO.setParagraphIndex(i);
                    taskTextDO.setText(text);
                    taskTextDO.setCreateBy(unstructTaskDO.getUpdateBy());
                    taskTextDO.setUpdateBy(unstructTaskDO.getUpdateBy());
                    taskTextDO.setCreatorId(unstructTaskDO.getUpdaterId());
                    taskTextDO.setUpdaterId(unstructTaskDO.getUpdaterId());
                    taskTextDO.setCreateTime(new Date());
                    taskTextDO.setUpdateTime(new Date());
                    log.info("============>把段落数据添加到数据库:{}", JSONObject.toJSONString(taskTextDO));
                    extUnstructTaskTextMapper.insert(taskTextDO);
                } else {
                    log.error("============>抽取任务失败: {}", ajaxResult);
                    unstructTaskDO.setStatus(ExtTaskStatus.ERROR.getValue());
                    extUnstructTaskMapper.updateExtUnstructTask(unstructTaskDO);
                }
            }
        }
    }

    @Override
    public AjaxResult getExtExtraction(ExtExtractionDO extExtractionDO) {
        ExtUnstructTaskDO unstructTaskDO = extUnstructTaskMapper.selectById(extExtractionDO.getTaskId());
        AjaxResult ajaxResult = extNeo4jService.getExtExtraction(extExtractionDO);
        if (ajaxResult.isSuccess()) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) ajaxResult.get("data");
            hashMap.put("releaseStatus", unstructTaskDO.getPublishStatus());
            return AjaxResult.success("", hashMap);
        }
        return ajaxResult;
    }

    @Override
    public PageResult<ExtUnstructTaskDO> getExtUnstructTaskPage(ExtUnstructTaskPageReqVO pageReqVO) {
        return extUnstructTaskMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createExtUnstructTask(ExtUnstructTaskSaveReqVO createReqVO) {
        ExtUnstructTaskDO dictType = BeanUtils.toBean(createReqVO, ExtUnstructTaskDO.class);
        extUnstructTaskMapper.insert(dictType);

        Map<String, Object> params = createReqVO.getParams();
        String docIds = (String) params.get("docIds");
        createReqVO.setId(dictType.getId());
        saveUnstructDocRel(createReqVO, docIds);

        String relationIds = (String) params.get("relationIds");
        this.saveExtUnstructTaskRelation(dictType, relationIds);
        return dictType.getId();
    }

    private void saveExtUnstructTaskRelation(ExtUnstructTaskDO createReqVO, String relationIds) {
        String[] split = StringUtils.split(relationIds, ",");
        for (String relationId : split) {
            ExtUnstructTaskRelationDO extUnstructTaskRelationDO = new ExtUnstructTaskRelationDO();
            extUnstructTaskRelationDO.setTaskId(createReqVO.getId());
            extUnstructTaskRelationDO.setRelationId(Long.parseLong(relationId));
            extUnstructTaskRelationDO.setWorkspaceId(createReqVO.getWorkspaceId());
            extUnstructTaskRelationService.save(extUnstructTaskRelationDO);
        }
    }

    private void saveUnstructDocRel(ExtUnstructTaskSaveReqVO createReqVO, String docIds) {
        String[] split = docIds.split(",");
        List<Long> ids = Arrays.stream(split)
                .map(Long::parseLong) // 将每个字符串转换为 Long
                .collect(Collectors.toList()); // 收集成一个 List<Long>
        if (ids.size() > 0) {
            List<KmcDocumentDO> documentListByIds = kmcDocumentService.getKmcDocumentListByIds(ids);
            
            // 直接使用KmcDocumentDO列表，无需类型转换
            documentListByIds.forEach(e -> {
                ExtUnstructTaskDocRelSaveReqVO docRelSaveReqVO = new ExtUnstructTaskDocRelSaveReqVO();
                docRelSaveReqVO.setWorkspaceId(createReqVO.getWorkspaceId());
                docRelSaveReqVO.setTaskId(createReqVO.getId());
                docRelSaveReqVO.setDocId(e.getId());
                docRelSaveReqVO.setDocName(e.getName());
                docRelSaveReqVO.setCreateBy(createReqVO.getCreateBy());
                docRelSaveReqVO.setCreatorId(createReqVO.getCreatorId());
                docRelSaveReqVO.setCreateTime(createReqVO.getCreateTime());
                docRelSaveReqVO.setUpdateBy(createReqVO.getUpdateBy());
                docRelSaveReqVO.setUpdaterId(createReqVO.getCreatorId());
                docRelSaveReqVO.setUpdateTime(createReqVO.getUpdateTime());
                extUnstructTaskDocRelService.createExtUnstructTaskDocRel(docRelSaveReqVO);
            });
        }
    }

    @Override
    public int updateExtUnstructTask(ExtUnstructTaskSaveReqVO updateReqVO) {
        // 更新非结构化抽取任务
        ExtUnstructTaskDO updateObj = BeanUtils.toBean(updateReqVO, ExtUnstructTaskDO.class);

        Map<String, Object> params = updateReqVO.getParams();
        //删除之前关联的任务文件信息
        String oldDocRelIds = (String) params.get("oldDocRelIds");
        if (StringUtils.isNotBlank(oldDocRelIds)) {
            String[] split = oldDocRelIds.split(",");
            // 转换为 long 类型的数组
            Long[] longArray = Arrays.stream(split)
                    .map(Long::parseLong)
                    .toArray(Long[]::new);
            List<Long> longs = Arrays.asList(longArray);
            extUnstructTaskDocRelService.removeByIds(longs);
        }

        String docIds = (String) params.get("docIds");
        //重新存关联文件
        saveUnstructDocRel(updateReqVO, docIds);
        List<ExtUnstructTaskRelationDO> relationDOList = extUnstructTaskRelationService.findByTaskId(updateObj.getId());
        extUnstructTaskRelationService.removeByIds(relationDOList);
        String relationIds = (String) params.get("relationIds");
        this.saveExtUnstructTaskRelation(updateObj, relationIds);
        return extUnstructTaskMapper.updateById(updateObj);
    }

    @Override
    public int removeExtUnstructTask(Collection<Long> idList) {
        // 批量删除非结构化抽取任务
        return extUnstructTaskMapper.deleteBatchIds(idList);
    }

    @Override
    public ExtUnstructTaskDO getExtUnstructTaskById(Long id) {
        return extUnstructTaskMapper.selectById(id);
    }

    /**
     * 发布
     *
     * @param unstructTaskDO
     * @return
     */
    @Override
    public AjaxResult releaseByTaskId(ExtUnstructTaskDO unstructTaskDO) {
        extUnstructTaskMapper.updateById(unstructTaskDO);
        extNeo4jService.updateByTaskIdAndExtractType(unstructTaskDO.getId(), ExtractType.UNSTRUCTURED.getValue(), ReleaseStatus.PUBLISHED.getValue());
        return AjaxResult.success("发布成功");
    }

    /**
     * 取消发布
     *
     * @param unstructTaskDO
     * @return
     */
    @Override
    public AjaxResult cancelReleaseByTaskId(ExtUnstructTaskDO unstructTaskDO) {
        extUnstructTaskMapper.updateById(unstructTaskDO);
        extNeo4jService.updateByTaskIdAndExtractType(unstructTaskDO.getId(), ExtractType.UNSTRUCTURED.getValue(), ReleaseStatus.UNPUBLISHED.getValue());
        return AjaxResult.success("取消发布成功");
    }


    /**
     * 导入非结构化抽取任务数据
     *
     * @param importExcelList 非结构化抽取任务数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importExtUnstructTask(List<ExtUnstructTaskRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (ExtUnstructTaskRespVO respVO : importExcelList) {
            try {
                ExtUnstructTaskDO extUnstructTaskDO = BeanUtils.toBean(respVO, ExtUnstructTaskDO.class);
                Long extUnstructTaskId = respVO.getId();
                if (isUpdateSupport) {
                    if (extUnstructTaskId != null) {
                        ExtUnstructTaskDO existingExtUnstructTask = extUnstructTaskMapper.selectById(extUnstructTaskId);
                        if (existingExtUnstructTask != null) {
                            extUnstructTaskMapper.updateById(extUnstructTaskDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + extUnstructTaskId + " 的非结构化抽取任务记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + extUnstructTaskId + " 的非结构化抽取任务记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<ExtUnstructTaskDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", extUnstructTaskId);
                    ExtUnstructTaskDO existingExtUnstructTask = extUnstructTaskMapper.selectOne(queryWrapper);
                    if (existingExtUnstructTask == null) {
                        extUnstructTaskMapper.insert(extUnstructTaskDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + extUnstructTaskId + " 的非结构化抽取任务记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + extUnstructTaskId + " 的非结构化抽取任务记录已存在。");
                    }
                }
            } catch (Exception e) {
                failureNum++;
                String errorMsg = "数据导入失败，错误信息：" + e.getMessage();
                failureMessages.add(errorMsg);
                log.error(errorMsg, e);
            }
        }
        StringBuilder resultMsg = new StringBuilder();
        if (failureNum > 0) {
            resultMsg.append("很抱歉，导入失败！共 ").append(failureNum).append(" 条数据格式不正确，错误如下：");
            resultMsg.append("<br/>").append(String.join("<br/>", failureMessages));
            throw new ServiceException(resultMsg.toString());
        } else {
            resultMsg.append("恭喜您，数据已全部导入成功！共 ").append(successNum).append(" 条。");
        }
        return resultMsg.toString();
    }

    /**
     * 获取任务的schema配置列表
     *
     * @param taskId 任务ID
     * @return schema列表
     */
    private List<String> getTaskSchemaList(Long taskId) {
        try {
            // 获取任务关联的关系配置
            List<ExtUnstructTaskRelationDO> relationList = extUnstructTaskRelationService.findByTaskId(taskId);
            if (relationList == null || relationList.isEmpty()) {
                return null;
            }

            List<String> schemaList = new ArrayList<>();
            for (ExtUnstructTaskRelationDO taskRelation : relationList) {
                // 通过relationId获取关系配置详情
                ExtSchemaRelationDO schemaRelation = extSchemaRelationService.getExtSchemaRelationById(taskRelation.getRelationId());
                if (schemaRelation != null) {
                    // 获取起点和终点的概念名称
                    ExtSchemaDO startSchema = extSchemaService.getExtSchemaById(schemaRelation.getStartSchemaId());
                    ExtSchemaDO endSchema = extSchemaService.getExtSchemaById(schemaRelation.getEndSchemaId());
                    
                    if (startSchema != null && endSchema != null) {
                        // 构建schema格式：起点-关系->终点
                        String schema = startSchema.getName() + "-" + schemaRelation.getRelation() + "->" + endSchema.getName();
                        schemaList.add(schema);
                    }
                }
            }
            
            return schemaList;
        } catch (Exception e) {
            log.error("获取任务schema配置失败，taskId: {}", taskId, e);
            return null;
        }
    }
}

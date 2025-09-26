package tech.qiantong.qknow.module.ext.controller.admin.extEntityPool;

import cn.hutool.core.date.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qknow.common.annotation.Log;
import tech.qiantong.qknow.common.core.controller.BaseController;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.domain.CommonResult;
import tech.qiantong.qknow.common.core.page.PageParam;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.common.enums.BusinessType;
import tech.qiantong.qknow.common.utils.object.BeanUtils;
import tech.qiantong.qknow.common.utils.poi.ExcelUtil;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolPageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo.ExtEntityPoolSaveReqVO;
import tech.qiantong.qknow.module.ext.convert.extEntityPool.ExtEntityPoolConvert;
import tech.qiantong.qknow.module.ext.dal.dataobject.extEntityPool.ExtEntityPoolDO;
import tech.qiantong.qknow.module.ext.service.extEntityPool.IExtEntityPoolService;
import tech.qiantong.qknow.module.ext.dal.dataobject.extDatasource.ExtDatasourceDO;
import tech.qiantong.qknow.module.ext.service.extDatasource.IExtDatasourceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 实体池 Controller
 *
 * @author qknow
 * @date 2025-01-20
 */
@Tag(name = "管理后台 - 实体池")
@RestController
@RequestMapping("/ext/entityPool")
@Validated
@Slf4j
public class ExtEntityPoolController extends BaseController {

    @Resource
    private IExtEntityPoolService extEntityPoolService;

    @Resource
    private IExtDatasourceService extDatasourceService;

    /**
     * 查询实体池列表
     *
     * @param pageReqVO 查询条件
     * @return 实体池分页
     */
    @GetMapping("/list")
    @Operation(summary = "查询实体池列表")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:list')")
    public CommonResult<PageResult<ExtEntityPoolRespVO>> list(ExtEntityPoolPageReqVO pageReqVO) {
        PageResult<ExtEntityPoolDO> pageResult = extEntityPoolService.getExtEntityPoolPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, ExtEntityPoolRespVO.class));
    }

    /**
     * 获得实体池分页
     *
     * @param pageReqVO 查询条件
     * @return 实体池分页
     */
    @GetMapping("/page")
    @Operation(summary = "获得实体池分页")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:query')")
    public CommonResult<PageResult<ExtEntityPoolRespVO>> getExtEntityPoolPage(ExtEntityPoolPageReqVO pageReqVO) {
        PageResult<ExtEntityPoolDO> pageResult = extEntityPoolService.getExtEntityPoolPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, ExtEntityPoolRespVO.class));
    }

    /**
     * 获得实体池
     *
     * @param id 编号
     * @return 实体池
     */
    @GetMapping("/get")
    @Operation(summary = "获得实体池")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:query')")
    public CommonResult<ExtEntityPoolRespVO> getExtEntityPool(@RequestParam("id") Long id) {
        ExtEntityPoolRespVO extEntityPool = extEntityPoolService.getExtEntityPool(id);
        return CommonResult.success(extEntityPool);
    }

    /**
     * 创建实体池
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @PostMapping("/create")
    @Operation(summary = "创建实体池")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:create')")
    @Log(title = "实体池", businessType = BusinessType.INSERT)
    public CommonResult<Long> createExtEntityPool(@Valid @RequestBody ExtEntityPoolSaveReqVO createReqVO) {
        createReqVO.setCreatorId(getUserId());
        createReqVO.setCreateBy(getNickName());
        createReqVO.setCreateTime(DateUtil.date());
        createReqVO.setWorkspaceId(super.getWorkSpaceId());
        return CommonResult.success(extEntityPoolService.createExtEntityPool(createReqVO));
    }

    /**
     * 更新实体池
     *
     * @param updateReqVO 更新信息
     */
    @PutMapping("/update")
    @Operation(summary = "更新实体池")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:update')")
    @Log(title = "实体池", businessType = BusinessType.UPDATE)
    public CommonResult<Integer> updateExtEntityPool(@Valid @RequestBody ExtEntityPoolSaveReqVO updateReqVO) {
        updateReqVO.setUpdaterId(getUserId());
        updateReqVO.setUpdateBy(getNickName());
        updateReqVO.setUpdateTime(DateUtil.date());
        extEntityPoolService.updateExtEntityPool(updateReqVO);
        return CommonResult.success(1);
    }

    /**
     * 删除实体池
     *
     * @param id 编号
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除实体池")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:delete')")
    @Log(title = "实体池", businessType = BusinessType.DELETE)
    public CommonResult<Integer> deleteExtEntityPool(@RequestParam("id") Long id) {
        extEntityPoolService.deleteExtEntityPool(id);
        return CommonResult.success(1);
    }

    /**
     * 批量删除实体池
     *
     * @param ids 编号数组
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "批量删除实体池")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:delete')")
    @Log(title = "实体池", businessType = BusinessType.DELETE)
    public CommonResult<Integer> removeExtEntityPool(@PathVariable Long[] ids) {
        return CommonResult.success(extEntityPoolService.removeExtEntityPool(Arrays.asList(ids)));
    }

    /**
     * 导出实体池 Excel
     *
     * @param exportReqVO 导出条件
     * @param response    响应对象
     */
    @Log(title = "实体池", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @Operation(summary = "导出实体池 Excel")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:export')")
    public void exportExtEntityPool(@Valid @RequestBody ExtEntityPoolPageReqVO exportReqVO,
                                   HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExtEntityPoolDO> list = extEntityPoolService.getExtEntityPoolList(exportReqVO);
        // 转换为RespVO
        List<ExtEntityPoolRespVO> respList = ExtEntityPoolConvert.INSTANCE.convertList(list);
        // 导出 Excel
        ExcelUtil<ExtEntityPoolRespVO> util = new ExcelUtil<>(ExtEntityPoolRespVO.class);
        util.exportExcel(response, respList, "实体池");
    }

    /**
     * 导入实体池数据
     *
     * @param file 文件
     * @param updateSupport 是否更新支持
     * @return 导入结果
     */
    @Operation(summary = "导入实体池数据")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:import')")
    @Log(title = "实体池", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public CommonResult<String> importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<ExtEntityPoolRespVO> util = new ExcelUtil<>(ExtEntityPoolRespVO.class);
        List<ExtEntityPoolRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = extEntityPoolService.importExtEntityPool(importExcelList, updateSupport, operName);
        return CommonResult.success(message);
    }

    /**
     * 处理实体（确认或拒绝）
     *
     * @param id 实体ID
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    @PostMapping("/process")
    @Operation(summary = "处理实体")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:process')")
    @Log(title = "实体池", businessType = BusinessType.UPDATE)
    public CommonResult<Object> processEntity(@RequestParam("id") Long id,
                                             @RequestParam("status") Integer status,
                                             @RequestParam(value = "remark", required = false) String remark) {
        return CommonResult.success(extEntityPoolService.processEntity(id, status, remark));
    }

    /**
     * 批量处理实体（确认或拒绝）
     *
     * @param idList 实体ID列表
     * @param status 处理状态 1：已确认，2：已拒绝
     * @param remark 处理备注
     * @return 处理结果
     */
    @PostMapping("/batch-process")
    @Operation(summary = "批量处理实体")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:process')")
    @Log(title = "实体池", businessType = BusinessType.UPDATE)
    public CommonResult<Object> batchProcessEntities(@RequestBody Map<String, Object> requestBody) {
        // 正确处理ID列表的类型转换
        List<?> idListObj = (List<?>) requestBody.get("idList");
        List<Long> idList = new ArrayList<>();
        for (Object id : idListObj) {
            if (id instanceof Integer) {
                idList.add(((Integer) id).longValue());
            } else if (id instanceof Long) {
                idList.add((Long) id);
            } else {
                idList.add(Long.valueOf(id.toString()));
            }
        }
        
        Integer status = (Integer) requestBody.get("status");
        String remark = (String) requestBody.get("remark");
        
        AjaxResult result = extEntityPoolService.batchProcessEntities(idList, status, remark);
        if (result.isSuccess()) {
            return CommonResult.success(result.get("data"));
        } else {
            return CommonResult.error(500, result.get("msg").toString());
        }
    }

    /**
     * 实体消歧 - 获取候选实体
     *
     * @param entityPoolId 实体池ID
     * @param topK 返回候选数量
     * @return 消歧结果
     */
    @PostMapping("/disambiguate")
    @Operation(summary = "实体消歧")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:disambiguate')")
    public CommonResult<Object> disambiguateEntity(@RequestParam("entityPoolId") Long entityPoolId,
                                                  @RequestParam(value = "topK", defaultValue = "5") Integer topK) {
        return CommonResult.success(extEntityPoolService.disambiguateEntity(entityPoolId, topK));
    }

    /**
     * 实体消歧 - 确认消歧结果
     *
     * @param entityPoolId 实体池ID
     * @param candidateId 候选实体ID
     * @param remark 备注
     * @return 处理结果
     */
    @PostMapping("/confirm-disambiguation")
    @Operation(summary = "确认消歧结果")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:confirm')")
    @Log(title = "实体池", businessType = BusinessType.UPDATE)
    public CommonResult<Object> confirmDisambiguation(@RequestParam("entityPoolId") Long entityPoolId,
                                                      @RequestParam("candidateId") String candidateId,
                                                      @RequestParam(value = "remark", required = false) String remark) {
        return CommonResult.success(extEntityPoolService.confirmDisambiguation(entityPoolId, candidateId, remark));
    }

    /**
     * 获取候选实体的详细信息
     *
     * @param candidateId 候选实体ID
     * @return 候选实体详细信息
     */
    @GetMapping("/candidate-details")
    @Operation(summary = "获取候选实体详细信息")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:query')")
    public CommonResult<Object> getCandidateEntityDetails(@RequestParam("candidateId") String candidateId) {
        AjaxResult result = extEntityPoolService.getCandidateEntityDetails(candidateId);
        if (result.isSuccess()) {
            return CommonResult.success(result.get("data"));
        } else {
            return CommonResult.error(500, result.get("msg").toString());
        }
    }

    /**
     * 合并实体信息
     *
     * @param entityPoolId 实体池ID
     * @param candidateId 候选实体ID
     * @param mergeFields 要合并的字段配置列表
     * @param remark 备注
     * @return 合并结果
     */
    @PostMapping("/merge-entity")
    @Operation(summary = "合并实体信息")
    // @PreAuthorize("@ss.hasPermi('ext:extEntityPool:process')")
    @Log(title = "实体池", businessType = BusinessType.UPDATE)
    public CommonResult<Object> mergeEntityInfo(
            @RequestParam("entityPoolId") Long entityPoolId,
            @RequestParam("candidateId") String candidateId,
            @RequestBody List<Map<String, Object>> mergeFields,
            @RequestParam(value = "remark", required = false) String remark) {
        AjaxResult result = extEntityPoolService.mergeEntityInfo(entityPoolId, candidateId, mergeFields, remark);
        if (result.isSuccess()) {
            return CommonResult.success(result.get("data"));
        } else {
            return CommonResult.error(500, result.get("msg").toString());
        }
    }

    /**
     * 测试Neo4j连接
     *
     * @param datasourceId 数据源ID
     * @return 连接测试结果
     */
    @PostMapping("/testNeo4jConnection")
    @Operation(summary = "测试Neo4j连接")
    public AjaxResult testNeo4jConnection(@RequestParam("datasourceId") Long datasourceId) {
        try {
            log.info("开始测试数据源ID {} 的Neo4j连接...", datasourceId);
            
            // 获取数据源信息
            ExtDatasourceDO datasource = extDatasourceService.getExtDatasourceById(datasourceId);
            if (datasource == null) {
                return AjaxResult.error("数据源不存在，ID: " + datasourceId);
            }
            
            // 测试连接
            boolean isConnected = extEntityPoolService.testNeo4jConnection(datasourceId);
            
            if (isConnected) {
                return AjaxResult.success("Neo4j连接测试成功", datasource);
            } else {
                return AjaxResult.error("Neo4j连接测试失败");
            }
            
        } catch (Exception e) {
            log.error("测试Neo4j连接时出错: {}", e.getMessage(), e);
            return AjaxResult.error("测试Neo4j连接时出错: " + e.getMessage());
        }
    }
} 
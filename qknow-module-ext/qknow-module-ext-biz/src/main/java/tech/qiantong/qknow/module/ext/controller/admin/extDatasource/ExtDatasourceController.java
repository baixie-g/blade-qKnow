package tech.qiantong.qknow.module.ext.controller.admin.extDatasource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.qiantong.qknow.common.core.controller.BaseController;
import tech.qiantong.qknow.common.core.domain.AjaxResult;
import tech.qiantong.qknow.common.core.domain.CommonResult;
import tech.qiantong.qknow.common.core.page.PageResult;
import tech.qiantong.qknow.common.utils.object.BeanUtils;
import tech.qiantong.qknow.common.utils.http.HttpUtils;
import tech.qiantong.qknow.module.ext.controller.admin.extDatasource.vo.ExtDatasourcePageReqVO;
import tech.qiantong.qknow.module.ext.controller.admin.extDatasource.vo.ExtDatasourceRespVO;
import tech.qiantong.qknow.module.ext.controller.admin.extDatasource.vo.ExtDatasourceSaveReqVO;
import tech.qiantong.qknow.module.ext.dal.dataobject.extDatasource.ExtDataSourceTable;
import tech.qiantong.qknow.module.ext.service.extDatasource.IExtDatasourceService;

import javax.annotation.Resource;

/**
 * 数据源Controller
 *
 * @author qknow
 * @date 2025-02-25
 */
@Tag(name = "数据源")
@RestController
@RequestMapping("/ext/datasource")
@Validated
public class ExtDatasourceController extends BaseController {
    @Resource
    private IExtDatasourceService extDatasourceService;

    /**
     * 根据数据源id, 数据id和表名获取行数据
     *
     * @param sourceTable
     * @return
     */
    @GetMapping("getTableDataByDataId")
    public AjaxResult getTableDataByDataId(ExtDataSourceTable sourceTable) {
        return extDatasourceService.getTableDataByDataId(sourceTable);
    }

    /**
     * 测试连接（Neo4j等）
     */
    @Operation(summary = "测试连接")
    @GetMapping("/testConnection")
    public AjaxResult testConnection(Long id) {
        return extDatasourceService.testConnection(id);
    }

    /**
     * 查询数据源列表
     */
    @Operation(summary = "查询数据源列表")
    @GetMapping("/list")
    public CommonResult<PageResult<ExtDatasourceRespVO>> list(ExtDatasourcePageReqVO extDatasource) {
        PageResult<?> page = extDatasourceService.getExtDatasourcePage(extDatasource);
        return CommonResult.success(BeanUtils.toBean(page, ExtDatasourceRespVO.class));
    }

    /**
     * 获取数据源详情
     */
    @Operation(summary = "获取数据源详细信息")
    @GetMapping(value = "/{id}")
    public CommonResult<ExtDatasourceRespVO> getInfo(@PathVariable("id") Long id) {
        return CommonResult.success(BeanUtils.toBean(extDatasourceService.getExtDatasourceById(id), ExtDatasourceRespVO.class));
    }

    /**
     * 新增数据源
     */
    @Operation(summary = "新增数据源")
    @PostMapping
    public CommonResult<Long> add(@RequestBody ExtDatasourceSaveReqVO extDatasource) {
        extDatasource.setStatus(0);
        return CommonResult.toAjax(extDatasourceService.createExtDatasource(extDatasource));
    }

    /**
     * 修改数据源
     */
    @Operation(summary = "修改数据源")
    @PutMapping
    public CommonResult<Integer> edit(@RequestBody ExtDatasourceSaveReqVO extDatasource) {
        return CommonResult.toAjax(extDatasourceService.updateExtDatasource(extDatasource));
    }

    /**
     * 删除数据源
     */
    @Operation(summary = "删除数据源")
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(extDatasourceService.removeExtDatasource(java.util.Arrays.asList(ids)));
    }

    /**
     * 上传数据源配置到 Nacos
     */
    @Operation(summary = "上传数据源配置到 Nacos")
    @PostMapping("/uploadToNacos")
    public AjaxResult uploadToNacos() {
        try {
            // 获取所有数据源
            java.util.List<tech.qiantong.qknow.module.ext.dal.dataobject.extDatasource.ExtDatasourceDO> datasources = 
                extDatasourceService.getExtDatasourceList();
            
            // 构建 Nacos 配置
            java.util.Map<String, Object> nacosConfig = new java.util.HashMap<>();
            nacosConfig.put("datasources", datasources);
            nacosConfig.put("timestamp", System.currentTimeMillis());
            
            // 发送到 Nacos
            String nacosUrl = "http://localhost:8848/nacos/v1/cs/configs";
            String dataId = "qknow-datasources";
            String group = "DEFAULT_GROUP";
            String content = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(nacosConfig);
            
            // 尝试不同的Nacos API调用方式
            String response = null;
            String errorMsg = "";
            
            // 方式1: 先尝试获取访问令牌，然后使用认证调用
            try {
                // 先尝试登录获取token
                String loginUrl = "http://localhost:8848/nacos/v1/auth/users/login";
                String loginParams = "username=nacos&password=nacos";
                String loginResponse = HttpUtils.sendPost(loginUrl, loginParams);
                
                if (loginResponse != null && loginResponse.contains("accessToken")) {
                    // 解析token
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<String, Object> loginResult = mapper.readValue(loginResponse, java.util.Map.class);
                    String accessToken = (String) loginResult.get("accessToken");
                    
                    // 使用token调用配置API
                    String authUrl = nacosUrl + "?accessToken=" + accessToken;
                    String putParams = "dataId=" + dataId + "&group=" + group + "&content=" + 
                        java.net.URLEncoder.encode(content, "UTF-8") + "&type=json";
                    
                    response = HttpUtils.sendPost(authUrl, putParams);
                    if (response != null && response.contains("true")) {
                        return AjaxResult.success("数据源配置已成功上传到 Nacos (使用认证)");
                    }
                }
            } catch (Exception e1) {
                errorMsg += "认证方式失败: " + e1.getMessage() + "; ";
            }
            
            // 方式2: 直接使用查询参数（不需要认证的简单方式）
            try {
                String urlWithParams = nacosUrl + "?dataId=" + dataId + "&group=" + group + "&content=" + 
                    java.net.URLEncoder.encode(content, "UTF-8") + "&type=json";
                response = HttpUtils.sendPost(urlWithParams, "");
                if (response != null && response.contains("true")) {
                    return AjaxResult.success("数据源配置已成功上传到 Nacos (简单方式)");
                }
            } catch (Exception e2) {
                errorMsg += "简单方式失败: " + e2.getMessage() + "; ";
            }
            
            // 方式3: 尝试PUT方法
            try {
                String putUrl = nacosUrl;
                String putParams = "dataId=" + dataId + "&group=" + group + "&content=" + 
                    java.net.URLEncoder.encode(content, "UTF-8") + "&type=json";
                
                response = HttpUtils.sendPost(putUrl, putParams);
                if (response != null && response.contains("true")) {
                    return AjaxResult.success("数据源配置已成功上传到 Nacos (PUT方式)");
                }
            } catch (Exception e3) {
                errorMsg += "PUT方式失败: " + e3.getMessage() + "; ";
            }
            
            return AjaxResult.error("上传到 Nacos 失败: " + errorMsg);
            
        } catch (Exception e) {
            return AjaxResult.error("上传到 Nacos 失败: " + e.getMessage());
        }
    }

}

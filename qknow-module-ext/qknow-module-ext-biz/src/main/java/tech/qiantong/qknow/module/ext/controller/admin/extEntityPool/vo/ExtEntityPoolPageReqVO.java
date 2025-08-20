package tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import tech.qiantong.qknow.common.core.page.PageParam;

@Schema(description = "管理后台 - 实体池分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExtEntityPoolPageReqVO extends PageParam {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工作区id", example = "1024")
    private Long workspaceId;

    @Schema(description = "任务id", example = "1024")
    private Long taskId;

    @Schema(description = "文档id", example = "1024")
    private Long docId;

    @Schema(description = "Neo4j数据源ID", example = "1")
    private Long datasourceId;

    @Schema(description = "实体名称", example = "小明")
    private String entityName;

    @Schema(description = "实体类型", example = "人物")
    private String entityType;

    @Schema(description = "处理状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间开始")
    private Date createTimeStart;

    @Schema(description = "创建时间结束")
    private Date createTimeEnd;

} 
package tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import tech.qiantong.qknow.common.core.page.PageParam;

@Schema(description = "管理后台 - 关系池分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExtRelationshipPoolPageReqVO extends PageParam {

    @Schema(description = "工作区id", example = "1024")
    private Long workspaceId;

    @Schema(description = "任务id", example = "1024")
    private Long taskId;

    @Schema(description = "文档id", example = "1024")
    private Long docId;

    @Schema(description = "Neo4j数据源ID", example = "1")
    private Long datasourceId;

    @Schema(description = "源实体ID", example = "person_001")
    private String sourceEntityId;

    @Schema(description = "目标实体ID", example = "school_001")
    private String targetEntityId;

    @Schema(description = "关系类型", example = "毕业院校")
    private String relationshipType;

    @Schema(description = "处理状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间开始")
    private Date createTimeStart;

    @Schema(description = "创建时间结束")
    private Date createTimeEnd;

} 
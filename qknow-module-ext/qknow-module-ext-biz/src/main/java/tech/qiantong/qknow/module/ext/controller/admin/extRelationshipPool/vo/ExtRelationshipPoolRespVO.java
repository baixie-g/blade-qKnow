package tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理后台 - 关系池 Response VO")
@Data
public class ExtRelationshipPoolRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "工作区id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long workspaceId;

    @Schema(description = "任务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long taskId;

    @Schema(description = "文档id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long docId;

    @Schema(description = "段落索引", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer paragraphIndex;

    @Schema(description = "Neo4j数据源ID", example = "1")
    private Long datasourceId;

    @Schema(description = "源实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "person_001")
    private String sourceEntityId;

    @Schema(description = "目标实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "school_001")
    private String targetEntityId;

    @Schema(description = "关系类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "毕业院校")
    private String relationshipType;

    @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "处理时间")
    private Date processTime;

    @Schema(description = "处理人", example = "小桐")
    private String processBy;

    @Schema(description = "处理备注", example = "确认通过")
    private String processRemark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

} 
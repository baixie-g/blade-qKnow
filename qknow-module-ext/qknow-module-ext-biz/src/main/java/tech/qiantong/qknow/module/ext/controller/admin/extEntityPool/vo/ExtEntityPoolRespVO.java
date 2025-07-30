package tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理后台 - 实体池 Response VO")
@Data
public class ExtEntityPoolRespVO {

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

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "person_001")
    private String entityId;

    @Schema(description = "实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小明")
    private String entityName;

    @Schema(description = "实体类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "人物")
    private String entityType;

    @Schema(description = "实体别名，JSON格式", example = "[]")
    private String aliases;

    @Schema(description = "实体定义", example = "小明毕业于清华大学，目前在字节跳动工作。")
    private String definition;

    @Schema(description = "实体属性，JSON格式", example = "{}")
    private String attributes;

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
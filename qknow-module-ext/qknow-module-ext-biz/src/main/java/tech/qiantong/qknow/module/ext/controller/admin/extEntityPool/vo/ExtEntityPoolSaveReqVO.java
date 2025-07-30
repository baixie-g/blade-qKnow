package tech.qiantong.qknow.module.ext.controller.admin.extEntityPool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 实体池新增/修改 Request VO")
@Data
public class ExtEntityPoolSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "工作区id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "工作区id不能为空")
    private Long workspaceId;

    @Schema(description = "任务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "任务id不能为空")
    private Long taskId;

    @Schema(description = "文档id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "文档id不能为空")
    private Long docId;

    @Schema(description = "段落索引", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "段落索引不能为空")
    private Integer paragraphIndex;

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "person_001")
    @NotBlank(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小明")
    @NotBlank(message = "实体名称不能为空")
    private String entityName;

    @Schema(description = "实体类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "人物")
    @NotBlank(message = "实体类型不能为空")
    private String entityType;

    @Schema(description = "实体别名，JSON格式", example = "[]")
    private String aliases;

    @Schema(description = "实体定义", example = "小明毕业于清华大学，目前在字节跳动工作。")
    private String definition;

    @Schema(description = "实体属性，JSON格式", example = "{}")
    private String attributes;

    @Schema(description = "处理状态", example = "0")
    private Integer status;

    @Schema(description = "处理备注", example = "确认通过")
    private String processRemark;

    // 审计字段
    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

} 
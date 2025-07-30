package tech.qiantong.qknow.module.ext.controller.admin.extRelationshipPool.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 关系池新增/修改 Request VO")
@Data
public class ExtRelationshipPoolSaveReqVO {

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

    @Schema(description = "源实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "person_001")
    @NotBlank(message = "源实体ID不能为空")
    private String sourceEntityId;

    @Schema(description = "目标实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "school_001")
    @NotBlank(message = "目标实体ID不能为空")
    private String targetEntityId;

    @Schema(description = "关系类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "毕业院校")
    @NotBlank(message = "关系类型不能为空")
    private String relationshipType;

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
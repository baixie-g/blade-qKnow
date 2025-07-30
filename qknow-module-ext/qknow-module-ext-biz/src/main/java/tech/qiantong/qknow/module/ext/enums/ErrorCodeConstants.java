package tech.qiantong.qknow.module.ext.enums;

import tech.qiantong.qknow.common.exception.ErrorCode;

/**
 * ext 模块错误码枚举类
 *
 * ext 模块，使用 1-000-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 实体池相关 1-000-000-000 ==========
    ErrorCode EXT_ENTITY_POOL_NOT_EXISTS = new ErrorCode(1_000_000_000, "实体池不存在");
    ErrorCode EXT_ENTITY_POOL_NAME_DUPLICATE = new ErrorCode(1_000_000_001, "实体池名称已存在");

    // ========== 关系池相关 1-000-000-100 ==========
    ErrorCode EXT_RELATIONSHIP_POOL_NOT_EXISTS = new ErrorCode(1_000_000_100, "关系池不存在");
    ErrorCode EXT_RELATIONSHIP_POOL_DUPLICATE = new ErrorCode(1_000_000_101, "关系池记录已存在");

} 
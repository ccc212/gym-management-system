package com.gymsys.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 团队成员状态枚举
 */
@Getter
@AllArgsConstructor
public enum TeamMemberStatusEnum {
    
    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝");
    
    private final Integer code;
    private final String desc;
} 
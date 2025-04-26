package com.gymsys.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    // 通用状态码
    SUCCESS(0, "操作成功"),
    FAIL(-10001, "操作失败"),
    SERVER_ERROR(-10002, "服务器内部错误"),
    PARAM_ERROR(-10003, "参数校验失败"),
    NOT_FOUND(-10004, "未找到"),
    NO_PERMISSION(-10005, "权限不足"),

    // 登录相关
    NO_LOGIN(-10101, "用户未登录"),
    PASSWORD_ERROR(-10102, "密码错误"), 
    USERNAME_EXIST(-10103, "用户名已存在"),
    TOKEN_EXPIRED(-10104, "Token已过期"),
    TOKEN_INVALID(-10105, "Token验证失败"),
    ACCOUNT_NOT_FOUND(-10106, "账号不存在"),
    LOGIN_FAILED(-10107, "登录失败"),
    USER_NOT_FOUND(-10108, "用户不存在"),
    USER_EXPIRED(-10109, "用户已过期"),

    // 用户管理相关

    // 场地管理相关

    // 赛事管理相关
    COMPETITION_NOT_EXIST(-10401, "赛事不存在"),
    COMPETITION_EQUIPMENT_RELATION_NOT_EXIST(-10402, "赛事与器材关联不存在"),
    COMPETITION_VENUE_RELATION_NOT_EXIST(-10403, "赛事与场地关联不存在"),
    COMPETITION_SIGN_UP_USER_NOT_EXIST(-10404, "个人报名不存在"),
    COMPETITION_SIGN_UP_TEAM_NOT_EXIST(-10405, "团队报名不存在"),
    TEAM_NOT_EXIST(-10406, "团队不存在"),
    TEAM_MEMBER_RELATION_NOT_EXIST(-10407, "团队成员关联不存在"),


    // 器材管理相关

    ;

    private final Integer code;

    private final String desc;
}

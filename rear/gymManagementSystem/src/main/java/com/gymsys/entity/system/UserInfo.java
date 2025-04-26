package com.gymsys.entity.system;

import lombok.Data;

/**
 * 返回用户的信息
 */
@Data
public class UserInfo {
    private Integer id;
    private String userNumber;
    private Object[] permissions;
}

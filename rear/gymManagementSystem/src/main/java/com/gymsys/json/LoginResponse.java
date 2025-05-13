package com.gymsys.json;

import com.gymsys.entity.venue.UserEntity;
import lombok.Data;

/**
 * 登录响应类
 */
@Data
public class LoginResponse {
    
    /**
     * 认证令牌
     */
    private String token;
    
    /**
     * 用户信息
     */
    private UserEntity user;
} 
package com.gymsys.controller;

import com.gymsys.entity.venue.UserEntity;
import com.gymsys.json.LoginRequest;
import com.gymsys.json.LoginResponse;
import com.gymsys.repository.venue.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        // 从数据库中查询用户
        UserEntity user = userRepository.findByUsernameAndPassword(username, password);
        
        // 如果用户存在，返回登录成功
        if (user != null) {
            // 生成token
            String token = "token-" + System.currentTimeMillis();
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUser(user);
            
            return ResponseEntity.ok(response);
        }
        
        // 用户不存在或密码错误，返回401未授权
        return ResponseEntity.status(401).build();
    }
} 
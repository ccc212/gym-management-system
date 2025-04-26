package com.gymsys.entity.system;

import lombok.Data;

@Data
public class LoginVo {
    private Integer id;
    private String userNumber;
    private String token;
}

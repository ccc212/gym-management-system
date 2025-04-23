package com.gymsys.entity.system;

import lombok.Data;

@Data
public class UpdatePasswordParm {
    private Integer id;
    private String oldPassword;
    private String password;
}

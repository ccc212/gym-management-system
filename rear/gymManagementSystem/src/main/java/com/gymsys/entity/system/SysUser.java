package com.gymsys.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userNumber;
    private String password;
    private String name;
    private String sex;
    private String age;
    private String phone;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String userType;
} 
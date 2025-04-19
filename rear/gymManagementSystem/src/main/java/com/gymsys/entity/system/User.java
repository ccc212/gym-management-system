package com.gymsys.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userNumber;

    private String password;

    private String name;

    private String sex;

    private String age;

    private String phone;

    private String email;

    private String userType;

    @TableField(exist = false)
    private Integer roleId;

    @TableField(exist = false)
    private Integer departId;

    @TableField(exist = false)
    private Integer sectionId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;

}

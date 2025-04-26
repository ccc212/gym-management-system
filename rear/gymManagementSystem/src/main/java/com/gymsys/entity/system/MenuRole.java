package com.gymsys.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_menu_role")
public class MenuRole {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer menuId;
    private Integer roleId;

}

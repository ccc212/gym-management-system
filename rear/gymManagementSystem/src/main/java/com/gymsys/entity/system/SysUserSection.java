package com.gymsys.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_use_section")
public class SysUserSection {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer sectionId;
}

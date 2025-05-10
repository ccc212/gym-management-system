package com.gymsys.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@TableName("sys_menu")
public class Menu {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer parentId;

    private String parentName;

    private String title;

    private String name;

    private String code;

    private String path;

    private String type;

    private String url;

    private String icon;

    private String orderNum;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;

    //下级菜单
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();

    //上级树
    @TableField(exist = false)
    private Integer value;

    @TableField(exist = false)
    private String label;
}

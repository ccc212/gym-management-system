package com.gymsys.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_equipment")
public class Equipment {
    private Integer id;
    private String equipmentName;
    private Integer status; // 0-可用，1-租用中，2-报修中
    private String repairReason;
    private Date createTime;
    private Date updateTime;
}
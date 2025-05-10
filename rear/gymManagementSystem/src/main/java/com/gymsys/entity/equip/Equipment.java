package com.gymsys.entity.equip;


import lombok.Data;

import java.util.Date;

@Data
public class Equipment {
    private Integer id; // 器材编号
    private String EquipmentName; // 器材名称
    private String type; // 器材类型
    private Integer remainingQuantity; // 仓库剩余数量
    private String administrator; // 负责的器材管理员
    private Integer status; // 器材状态，如正常、报修中、借出等
    private String repairReason; // 报修原因
    private Date setCreateTime; // 更新时间
    private Date setUpdateTime;
    private Date createTime;
    private Date updateTime;

    public void setCreateTime(Date createTime) {
        this.setCreateTime = createTime;
        this.createTime = createTime;
    }



    public void setUpdateTime(Date updateTime) {
        this.setUpdateTime = updateTime;
        this.updateTime = updateTime;
    }


    public void setRepairTime(Date repairTime) {
    }

    public Integer getEquipmentId() {
        return this.id;
    }
}
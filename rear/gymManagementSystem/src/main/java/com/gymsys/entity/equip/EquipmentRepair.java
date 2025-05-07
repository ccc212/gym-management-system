package com.gymsys.entity.equip;

import lombok.Data;
import java.util.Date;

@Data
public class EquipmentRepair {
    private Integer id; // 报修记录编号
    private Integer equipmentId; // 报修的器材编号
    private String reason; // 报修原因
    private Date applyTime; // 报修申请时间
}
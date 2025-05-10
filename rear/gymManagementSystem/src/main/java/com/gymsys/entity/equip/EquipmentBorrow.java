package com.gymsys.entity.equip;

import lombok.Data;
import java.util.Date;

@Data
public class EquipmentBorrow {
    private Integer id; // 借出记录编号
    private Integer equipmentId; // 借出的器材编号
    private String borrower; // 借用人
    private Date borrowTime; // 借出时间
    private Date returnTime; // 归还时间
}
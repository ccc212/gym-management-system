package com.gymsys.service.equipment.impl;



import com.gymsys.entity.equip.Equipment;
import com.gymsys.service.equipment.EquipmentService;
import com.gymsys.service.equipment.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private EquipmentService equipmentService;

    @Override
    public boolean reportRepair(Equipment equipment, String reason) {
        try {
            // 记录报修时间
            Date repairTime = new Date();
            // 调用EquipmentService更新器材状态为报修中
            equipmentService.updateEquipmentStatus(equipment.getEquipmentId().longValue(), "报修中");
            // 记录报修原因到数据库
            equipmentService.logRepairReason(equipment.getEquipmentId().longValue(), reason, repairTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean reportRepair(Integer equipmentId, String reason, Date repairTime) {
        try {
            // 调用EquipmentService更新器材状态为报修中
            equipmentService.updateEquipmentStatus(equipmentId.longValue(), "报修中");
            // 记录报修原因到数据库
            equipmentService.logRepairReason(equipmentId.longValue(), reason, repairTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

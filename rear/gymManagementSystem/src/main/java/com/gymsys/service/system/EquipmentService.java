package com.gymsys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.system.Equipment;

public interface EquipmentService extends IService<Equipment> {

    /**
     * 报修器材
     */
    boolean reportRepair(Integer id, String reason);
}
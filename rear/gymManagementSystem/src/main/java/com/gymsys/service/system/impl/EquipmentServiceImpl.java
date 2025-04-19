package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.Equipment;
import com.gymsys.mapper.system.EquipmentMapper;
import com.gymsys.service.system.EquipmentService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public abstract class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Override
    public boolean reportRepair(Integer id, String reason) {
        Equipment equipment = baseMapper.selectById(id);
        if (equipment == null) {
            return false; // 器材不存在
        }
        equipment.setStatus(2); // 标记为报修中
        equipment.setRepairReason(reason);
        equipment.setUpdateTime(new Date());
        return this.updateById(equipment);
    }
}
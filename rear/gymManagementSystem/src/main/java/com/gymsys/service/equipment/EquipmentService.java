package com.gymsys.service.equipment;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.entity.equip.Equipment;

import java.util.Date;
import java.util.List;


public interface EquipmentService {
    boolean save(Equipment equipment);
    boolean removeById(Integer id);
    boolean updateById(Equipment equipment);
    Equipment getById(Integer id);
    List<Equipment> list();
    List<Equipment> list(QueryWrapper<Equipment> queryWrapper);
    IPage<Equipment> page(IPage<Equipment> page, QueryWrapper<Equipment> queryWrapper);
    
    boolean updateEquipmentStatus(Long equipmentId, String status);

    boolean logRepairReason(Long equipmentId, String reason, Date repairTime);
}

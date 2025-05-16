package com.gymsys.service.equipment.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gymsys.entity.equip.EquipmentRepair;

import com.gymsys.mapper.equipment.EquipmentRepairMapper;
import com.gymsys.service.equipment.EquipmentRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报修记录服务实现类
 */
@Service
public class RepairServiceImpl implements EquipmentRepairService {

    @Autowired
    private EquipmentRepairMapper equipmentRepairMapper;

    @Override
    public boolean addRepairRecord(EquipmentRepair repair) {
        return equipmentRepairMapper.insert(repair) > 0;
    }

    @Override
    public boolean updateRepairRecord(EquipmentRepair repair) {
        return equipmentRepairMapper.updateById(repair) > 0;
    }

    @Override
    public boolean deleteRepairRecord(Integer id) {
        return equipmentRepairMapper.deleteById(id) > 0;
    }

    @Override
    public EquipmentRepair getById(Integer id) {
        return equipmentRepairMapper.selectById(id);
    }

    @Override
    public List<EquipmentRepair> listByEquipmentId(Integer equipmentId) {
        QueryWrapper<EquipmentRepair> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EquipmentRepair::getEquipmentId, equipmentId);
        return equipmentRepairMapper.selectList(queryWrapper);
    }

    @Override
    public List<EquipmentRepair> listAll() {
        return equipmentRepairMapper.selectList(null);
    }
}
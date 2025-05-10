package com.gymsys.service.equipment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.equip.Equipment;
import com.gymsys.mapper.equipment.EquipmentMapper;
import com.gymsys.service.equipment.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public boolean save(Equipment equipment) {
        equipment.setCreateTime(new Date());
        equipment.setStatus(0);
        return equipmentMapper.insert(equipment) > 0;
    }

    @Override
    public boolean removeById(Integer id) {
        return equipmentMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateById(Equipment equipment) {
        equipment.setUpdateTime(new Date());
        return equipmentMapper.updateById(equipment) > 0;
    }

    @Override
    public Equipment getById(Integer id) {
        return equipmentMapper.selectById(id);
    }

    @Override
    public List<Equipment> list() {
        return equipmentMapper.selectList(null);
    }

    @Override
    public List<Equipment> list(QueryWrapper<Equipment> queryWrapper) {
        return equipmentMapper.selectList(queryWrapper);
    }

    @Override
    public boolean updateEquipmentStatus(Long equipmentId, String status) {
        Equipment equipment = getById(equipmentId.intValue());
        if (equipment != null) {
            equipment.setStatus(status.equals("报修中") ? 2 : 0); // 2表示报修中状态
            return updateById(equipment);
        }
        return false;
    }

    @Override
    public boolean logRepairReason(Long equipmentId, String reason, Date repairTime) {
        Equipment equipment = getById(equipmentId.intValue());
        if (equipment != null) {
            // 假设通过Mapper将报修原因和时间存入数据库（需根据实际Mapper方法调整）
            equipment.setRepairReason(reason);
            equipment.setRepairTime(repairTime);
            return updateById(equipment);
        }
        return false;
    }

    @Override
    public IPage<Equipment> page(IPage<Equipment> page, QueryWrapper<Equipment> queryWrapper) {
        return equipmentMapper.selectPage(page, queryWrapper);
    }
}
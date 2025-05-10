package com.gymsys.service.equipment.impl;

import com.gymsys.entity.Result;
import com.gymsys.entity.equip.Equipment;
import com.gymsys.service.equipment.AdminService;
import com.gymsys.service.equipment.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private EquipmentService equipmentService;

    @Override
    public Result approveRepair(Integer equipmentId, boolean approved) {
        if (approved) {
            // 处理审核通过逻辑，例如更新器材状态为维修中
            Equipment equipment = equipmentService.getById(equipmentId);
            if (equipment != null) {
                equipment.setStatus(3); // 假设 3 为维修中状态
                if (equipmentService.updateById(equipment)) {
                    return Result.success("报修审核通过");
                }
            }
            return Result.error("报修审核处理失败");
        } else {
            // 处理审核不通过逻辑，例如恢复器材状态为可用
            Equipment equipment = equipmentService.getById(equipmentId);
            if (equipment != null) {
                equipment.setStatus(0); // 恢复为可用状态
                if (equipmentService.updateById(equipment)) {
                    return Result.success("报修审核未通过");
                }
            }
            return Result.error("报修审核处理失败");
        }
    }

    @Override
    public Result approveRent(Integer equipmentId, boolean approved) {
        if (approved) {
            // 处理审核通过逻辑，例如更新器材状态为租用中
            Equipment equipment = equipmentService.getById(equipmentId);
            if (equipment != null) {
                equipment.setStatus(1); // 标记为租用中
                if (equipmentService.updateById(equipment)) {
                    return Result.success("借出审核通过");
                }
            }
            return Result.error("借出审核处理失败");
        } else {
            // 处理审核不通过逻辑，例如保持器材状态为可用
            return Result.success("借出审核未通过");
        }
    }
}
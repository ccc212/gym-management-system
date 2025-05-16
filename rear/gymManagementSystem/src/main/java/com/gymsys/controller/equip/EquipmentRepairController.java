package com.gymsys.controller.equip;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gymsys.entity.Result;
import com.gymsys.entity.equip.Equipment;

import com.gymsys.entity.equip.EquipmentRepair;
import com.gymsys.service.equipment.EquipmentRepairService;
import com.gymsys.service.equipment.EquipmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;
@RestController
@RequestMapping("/equipment/repair")
public class EquipmentRepairController {

    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private EquipmentRepairService repairService;


    /**
     * 管理员查看所有报修记录（仅显示报修中状态）
     * @return 报修记录列表
     */
    @GetMapping("/admin/getRepairRecords")
    public Result getRepairRecords() {
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Equipment::getStatus, 2);

        List<Equipment> repairRecords = equipmentService.list(queryWrapper);
        return Result.success(repairRecords);
    }

    // 下面开始新增的 CRUD 接口实现

    @Autowired
    private EquipmentRepairService equipmentRepairService;

    /**
     * 新增报修记录
     * @param repair 报修实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result addRepair(@RequestBody EquipmentRepair repair) {
        if (repair.getEquipmentId() == null) {
            return Result.error("缺少器材ID");
        }
        repair.setApplyTime(new Date());
        boolean saved = equipmentRepairService.addRepairRecord(repair);
        if (saved) {
            return Result.success("添加报修记录成功");
        } else {
            return Result.error("添加报修记录失败");
        }
    }

    /**
     * 修改报修记录
     * @param repair 报修实体
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result updateRepair(@RequestBody EquipmentRepair repair) {
        if (repair.getId() == null) {
            return Result.error("缺少报修记录ID");
        }
        boolean updated = equipmentRepairService.updateRepairRecord(repair);
        if (updated) {
            return Result.success("修改报修记录成功");
        } else {
            return Result.error("修改报修记录失败");
        }
    }

    /**
     * 删除报修记录
     * @param id 报修记录ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteRepair(@PathVariable Integer id) {
        boolean deleted = equipmentRepairService.deleteRepairRecord(id);
        if (deleted) {
            return Result.success("删除报修记录成功");
        } else {
            return Result.error("删除报修记录失败");
        }
    }

    /**
     * 根据ID查询报修记录
     * @param id 报修记录ID
     * @return 报修记录详情
     */
    @GetMapping("/get/{id}")
    public Result getRepairById(@PathVariable Integer id) {
        EquipmentRepair repair = equipmentRepairService.getById(id);
        if (repair != null) {
            return Result.success(repair);
        } else {
            return Result.error("报修记录不存在");
        }
    }

    /**
     * 根据器材ID查询该器材所有报修记录
     * @param equipmentId 器材ID
     * @return 报修记录列表
     */
    @GetMapping("/listByEquipment/{equipmentId}")
    public Result listRepairsByEquipment(@PathVariable Integer equipmentId) {
        List<EquipmentRepair> repairs = equipmentRepairService.listByEquipmentId(equipmentId);
        return Result.success(repairs);
    }

    /**
     * 查询所有报修记录
     * @return 报修记录列表
     */
    @GetMapping("/listAll")
    public Result listAllRepairs() {
        List<EquipmentRepair> repairs = equipmentRepairService.listAll();
        return Result.success(repairs);
    }
}

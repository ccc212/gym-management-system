package com.gymsys.controller.equip;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gymsys.entity.Result;
import com.gymsys.entity.equip.Equipment;

import com.gymsys.service.equipment.EquipmentService;
import com.gymsys.service.equipment.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 器材报修管理控制器
 */
@RestController
@RequestMapping("/equipment/repair") // 统一请求路径前缀
public class EquipmentRepairController {

    // 注入服务层对象（需确保 Service 已实现并被 Spring 管理）
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private RepairService repairService;

    /**
     * 提交器材报修申请
     * @param id 器材 ID
     * @param reason 报修原因
     * @return 操作结果
     */
    @PostMapping("/reportRepair/{id}") // POST 请求路径
    public Result reportRepair(
            @PathVariable("id") Integer id, // 路径参数
            @RequestParam String reason // 请求参数（可改为 @RequestBody 接收对象）
    ) {
        // 校验器材是否存在
        Equipment equipment = equipmentService.getById(id);
        if (equipment == null) {
            return Result.error("器材不存在");
        }
        // 调用报修服务处理报修逻辑
        boolean reportResult = repairService.reportRepair(equipment, reason);
        if (reportResult) {
            return Result.success("报修成功");
        } else {
            return Result.error("报修失败，请联系管理员");
        }
    }

    /**
     * 管理员查看所有报修记录（仅显示报修中状态）
     * @return 报修记录列表
     */
    @GetMapping("/admin/getRepairRecords") // GET 请求路径
    public Result getRepairRecords() {
        // 构建查询条件：状态为报修中（2）
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Equipment::getStatus, 2);

        // 查询报修记录
        List<Equipment> repairRecords = equipmentService.list(queryWrapper);
        return Result.success(repairRecords);
    }
}
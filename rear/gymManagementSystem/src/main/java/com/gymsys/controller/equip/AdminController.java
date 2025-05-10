package com.gymsys.controller.equip;

import com.gymsys.entity.Result;
import com.gymsys.entity.equip.Equipment;
import com.gymsys.service.equipment.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/approval")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 审核器材报修
     */
    @PostMapping("/repair")
    public Result approveRepair(@RequestParam Integer equipmentId, @RequestParam boolean approved) {
        return adminService.approveRepair(equipmentId, approved);
    }

    /**
     * 审核器材借出
     */
    @PostMapping("/rent")
    public Result approveRent(@RequestParam Integer equipmentId, @RequestParam boolean approved) {
        return adminService.approveRent(equipmentId, approved);
    }
}

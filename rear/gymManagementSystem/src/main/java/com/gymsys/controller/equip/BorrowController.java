package com.gymsys.controller.equip;

import com.gymsys.entity.Result;
import com.gymsys.service.equipment.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/borrow")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    /**
     * 借出器材
     */
    @PostMapping("/lend")
    public Result lendEquipment(@RequestParam Integer equipmentId) {
        if (borrowService.lendEquipment(equipmentId)) {
            return Result.success("借出成功");
        }
        return Result.error("借出失败");
    }

    /**
     * 归还器材
     */
    @PostMapping("/return")
    public Result returnEquipment(@RequestParam Integer equipmentId) {
        if (borrowService.returnEquipment(equipmentId)) {
            return Result.success("归还成功");
        }
        return Result.error("归还失败");
    }
}

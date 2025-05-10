package com.gymsys.controller.equip;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Result;
import com.gymsys.entity.equip.Equipment;
import com.gymsys.entity.equip.EquipmentParm;
import com.gymsys.entity.system.SelectItme;


import com.gymsys.service.equipment.EquipmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//import org.springframework.security.access.prepost.PreAuthorize;


@RequestMapping("/api/system/equipment")
@RestController
public class EquipBasicsController {
    @Autowired
    private EquipmentService equipmentService;

    /**
     * 添加器材
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result add(@RequestBody Equipment equipment) {
        equipment.setCreateTime(new Date());
        equipment.setStatus(0); // 默认状态：0-可用，1-租用中，2-报修中
        if (equipmentService.save(equipment)) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 修改器材信息
     */
    @PutMapping
    public Result update(@RequestBody Equipment equipment) {
        equipment.setUpdateTime(new Date());
        if (equipmentService.updateById(equipment)) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 删除器材
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        if (equipmentService.removeById(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取器材列表（分页+条件查询）
     */
    @GetMapping("/getList")
    public Result getList(EquipmentParm parm) {
        // 分页构造
        IPage<Equipment> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        // 查询条件构造
        QueryWrapper<Equipment> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getEquipmentName())) {
            query.lambda().like(Equipment::getEquipmentName, parm.getEquipmentName());
        }
        if (parm.getStatus() != null) {
            query.lambda().eq(Equipment::getStatus, parm.getStatus());
        }
        IPage<Equipment> list = equipmentService.page(page, query);
        return Result.success(list);
    }



    /**
     * 获取器材下拉列表（用于前端选择）
     */
    @GetMapping("/selectList")
    public Result selectList() {
        QueryWrapper<Equipment> query = new QueryWrapper<>();
        query.lambda().eq(Equipment::getStatus, 0); // 只返回可用的器材
        List<Equipment> list = equipmentService.list(query);

        List<SelectItme> selectList = Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .map(item -> {
                    SelectItme vo = new SelectItme();
                    vo.setLabel(item.getEquipmentName());
                    vo.setValue(item.getId());
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(selectList);
    }
}



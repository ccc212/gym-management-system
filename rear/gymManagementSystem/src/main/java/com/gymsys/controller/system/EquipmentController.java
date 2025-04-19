package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.domain.entity.Result;
import com.gymsys.entity.system.Equipment;
import com.gymsys.entity.system.EquipmentParm;
import com.gymsys.entity.system.SelectItme;
import com.gymsys.service.system.EquipmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

    @RequestMapping("/api/system/equipment")
    @RestController
    public class EquipmentController {
        @Autowired
        private EquipmentService equipmentService;

        /**
         * 添加器材
         */
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
         * 器材租用
         */
        @PostMapping("/rent/{id}")
        public Result rent(@PathVariable("id") Integer id) {
            Equipment equipment = equipmentService.getById(id);
            if (equipment == null) {
                return Result.error("器材不存在");
            }
            if (equipment.getStatus() != 0) {
                return Result.error("器材当前不可租用");
            }
            equipment.setStatus(1); // 标记为租用中
            equipment.setUpdateTime(new Date());
            if (equipmentService.updateById(equipment)) {
                return Result.success("租用成功");
            }
            return Result.error("租用失败");
        }

        /**
         * 器材归还
         */
        @PostMapping("/return/{id}")
        public Result returnEquipment(@PathVariable("id") Integer id) {
            Equipment equipment = equipmentService.getById(id);
            if (equipment == null) {
                return Result.error("器材不存在");
            }
            if (equipment.getStatus() != 1) {
                return Result.error("器材未处于租用状态");
            }
            equipment.setStatus(0); // 标记为可用
            equipment.setUpdateTime(new Date());
            if (equipmentService.updateById(equipment)) {
                return Result.success("归还成功");
            }
            return Result.error("归还失败");
        }

        /**
         * 器材报修
         */
        @PostMapping("/reportRepair/{id}")
        public Result reportRepair(@PathVariable("id") Integer id, @RequestParam String reason) {
            Equipment equipment = equipmentService.getById(id);
            if (equipment == null) {
                return Result.error("器材不存在");
            }
            equipment.setStatus(2); // 标记为报修中
            equipment.setRepairReason(reason);
            equipment.setUpdateTime(new Date());
            if (equipmentService.updateById(equipment)) {
                return Result.success("报修成功");
            }
            return Result.error("报修失败");
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



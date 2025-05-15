package com.gymsys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.common.Result;
import com.gymsys.dto.SpecialArrangementDTO;
import com.gymsys.entity.specialarrangement.SpecialArrangement;
import com.gymsys.service.SpecialArrangementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/special-arrangements")
public class SpecialArrangementController {

    @Resource
    private SpecialArrangementService specialArrangementService;

    @GetMapping
    public Result<Page<SpecialArrangement>> getSpecialArrangements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Page<SpecialArrangement> arrangements = specialArrangementService.getSpecialArrangements(
            page, size, venueType, venueId, startDate, endDate);
        return Result.success(arrangements);
    }

    @PostMapping
    public Result<SpecialArrangement> createSpecialArrangement(@RequestBody SpecialArrangementDTO dto) {
        SpecialArrangement arrangement = specialArrangementService.createSpecialArrangement(dto);
        return Result.success(arrangement);
    }

    @PutMapping("/{id}")
    public Result<SpecialArrangement> updateSpecialArrangement(
            @PathVariable Long id,
            @RequestBody SpecialArrangementDTO dto) {
        SpecialArrangement arrangement = specialArrangementService.updateSpecialArrangement(id, dto);
        return Result.success(arrangement);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSpecialArrangement(@PathVariable Long id) {
        specialArrangementService.deleteSpecialArrangement(id);
        return Result.success(null);
    }
} 
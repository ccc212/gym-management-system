package com.gymsys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.dto.SpecialArrangementDTO;
import com.gymsys.entity.SpecialArrangement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpecialArrangementService {
    // 分页查询特殊场地安排
    Page<SpecialArrangement> getSpecialArrangements(Integer page, Integer size, String venueType, Long venueId, LocalDate startDate, LocalDate endDate);
    
    // 获取可用时间段
    List<Map<String, Object>> getAvailableTimeSlots(Long venueId, LocalDate date);
    
    // 创建特殊场地安排
    SpecialArrangement createSpecialArrangement(SpecialArrangementDTO dto);
    
    // 更新特殊场地安排
    SpecialArrangement updateSpecialArrangement(Long id, SpecialArrangementDTO dto);
    
    // 删除特殊场地安排
    void deleteSpecialArrangement(Long id);
} 
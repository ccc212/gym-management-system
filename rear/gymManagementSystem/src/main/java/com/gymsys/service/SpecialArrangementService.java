package com.gymsys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.dto.SpecialArrangementDTO;
import com.gymsys.entity.specialarrangement.SpecialArrangement;
import java.time.LocalDate;

public interface SpecialArrangementService {
    // 分页查询特殊场地安排
    Page<SpecialArrangement> getSpecialArrangements(Integer page, Integer size, String venueType, Long venueId, LocalDate startDate, LocalDate endDate);
    
    // 创建特殊场地安排
    SpecialArrangement createSpecialArrangement(SpecialArrangementDTO dto);
    
    // 更新特殊场地安排
    SpecialArrangement updateSpecialArrangement(Long id, SpecialArrangementDTO dto);
    
    // 删除特殊场地安排
    void deleteSpecialArrangement(Long id);
} 
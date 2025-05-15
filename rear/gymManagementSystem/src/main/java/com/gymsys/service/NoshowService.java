package com.gymsys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.dto.NoshowDTO;
import com.gymsys.entity.noshow.NoshowEntity;

import java.time.LocalDate;

public interface NoshowService {
    Page<NoshowEntity> getNoshows(Integer page, Integer size, String venueType, Long venueId, LocalDate startDate, LocalDate endDate);
    
    NoshowEntity handleNoshow(Long id, NoshowDTO dto);
    
    NoshowEntity getNoshowDetail(Long id);
    
    boolean save(NoshowEntity noshow);
} 
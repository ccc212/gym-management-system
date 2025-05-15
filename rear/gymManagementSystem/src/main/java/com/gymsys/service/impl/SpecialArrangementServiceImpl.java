package com.gymsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.dto.SpecialArrangementDTO;
import com.gymsys.entity.specialarrangement.SpecialArrangement;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.mapper.specialarrangement.SpecialArrangementMapper;
import com.gymsys.repository.venue.VenueRepository;
import com.gymsys.service.SpecialArrangementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecialArrangementServiceImpl implements SpecialArrangementService {

    @Resource
    private SpecialArrangementMapper specialArrangementMapper;

    @Resource
    private VenueRepository venueRepository;

    @Override
    public Page<SpecialArrangement> getSpecialArrangements(Integer page, Integer size, String venueType, Long venueId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SpecialArrangement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(venueType)) {
            wrapper.eq(SpecialArrangement::getVenueType, venueType);
        }
        if (venueId != null) {
            wrapper.eq(SpecialArrangement::getVenueId, venueId);
        }
        if (startDate != null) {
            wrapper.ge(SpecialArrangement::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(SpecialArrangement::getDate, endDate);
        }
        
        wrapper.orderByDesc(SpecialArrangement::getCreatedAt);
        
        return specialArrangementMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public SpecialArrangement createSpecialArrangement(SpecialArrangementDTO dto) {
        VenueEntity venue = venueRepository.selectById(dto.getVenueId());
        if (venue == null) {
            throw new RuntimeException("场地不存在");
        }

        SpecialArrangement arrangement = new SpecialArrangement();
        BeanUtils.copyProperties(dto, arrangement);
     
        // 设置场地信息
        arrangement.setVenueName(venue.getName());
        arrangement.setVenueType(venue.getType());
        
        // 设置创建和更新时间
        arrangement.setCreatedAt(LocalDateTime.now());
        arrangement.setUpdatedAt(LocalDateTime.now());
        
        specialArrangementMapper.insert(arrangement);
        return arrangement;
    }

    @Override
    @Transactional
    public SpecialArrangement updateSpecialArrangement(Long id, SpecialArrangementDTO dto) {
        SpecialArrangement existing = specialArrangementMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("特殊安排不存在");
        }

        VenueEntity venue = venueRepository.selectById(dto.getVenueId());
        if (venue == null) {
            throw new RuntimeException("场地不存在");
        }

        // 更新基本信息
        BeanUtils.copyProperties(dto, existing);
        
        // 更新场地信息
        existing.setVenueName(venue.getName());
        existing.setVenueType(venue.getType());
        
        // 更新时间戳
        existing.setUpdatedAt(LocalDateTime.now());
        
        specialArrangementMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteSpecialArrangement(Long id) {
        specialArrangementMapper.deleteById(id);
    }

    private boolean checkTimeSlotSpecial(Long venueId, LocalDate date, String timeRange) {
        LambdaQueryWrapper<SpecialArrangement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialArrangement::getVenueId, venueId)
               .eq(SpecialArrangement::getDate, date)
               .and(w -> w.le(SpecialArrangement::getStartTime, timeRange.split(" - ")[0])
                         .ge(SpecialArrangement::getEndTime, timeRange.split(" - ")[1]));
        
        return specialArrangementMapper.selectCount(wrapper) > 0;
    }

    private String mergeTimeSlots(List<String> timeSlots) {
        if (timeSlots.isEmpty()) {
            return "";
        }
        
        // 解析时间段
        List<LocalTime> times = new ArrayList<>();
        for (String slot : timeSlots) {
            String[] parts = slot.split("-");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            times.add(LocalTime.of(hour, minute));
        }
        
        // 排序
        times.sort(LocalTime::compareTo);
        
        // 获取开始和结束时间
        LocalTime startTime = times.get(0);
        LocalTime endTime = times.get(times.size() - 1).plusMinutes(30);
        
        return String.format("%02d:%02d - %02d:%02d",
            startTime.getHour(), startTime.getMinute(),
            endTime.getHour(), endTime.getMinute());
    }
} 
package com.gymsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.dto.SpecialArrangementDTO;
import com.gymsys.entity.SpecialArrangement;
import com.gymsys.entity.Venue;
import com.gymsys.mapper.SpecialArrangementMapper;
import com.gymsys.mapper.VenueMapper;
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
    private VenueMapper venueMapper;

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
        
        wrapper.orderByDesc(SpecialArrangement::getCreatedTime);
        
        return specialArrangementMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Map<String, Object>> getAvailableTimeSlots(Long venueId, LocalDate date) {
        List<Map<String, Object>> timeSlots = new ArrayList<>();
        
        // 生成时间段（8:00-22:00，每30分钟一个时间段）
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        
        while (startTime.isBefore(endTime)) {
            LocalTime slotEndTime = startTime.plusMinutes(30);
            String timeRange = String.format("%02d:%02d - %02d:%02d", 
                startTime.getHour(), startTime.getMinute(),
                slotEndTime.getHour(), slotEndTime.getMinute());
            
            Map<String, Object> slot = new HashMap<>();
            slot.put("id", String.format("%d-%d", startTime.getHour(), startTime.getMinute()));
            slot.put("timeRange", timeRange);
            
            // 检查该时间段是否已被预约或设为特殊安排
            boolean isBooked = checkTimeSlotBooked(venueId, date, timeRange);
            boolean isSpecial = checkTimeSlotSpecial(venueId, date, timeRange);
            
            slot.put("isBooked", isBooked);
            slot.put("isSpecial", isSpecial);
            
            timeSlots.add(slot);
            startTime = slotEndTime;
        }
        
        // 添加全天选项
        Map<String, Object> allDaySlot = new HashMap<>();
        allDaySlot.put("id", "all-day");
        allDaySlot.put("timeRange", "全天");
        allDaySlot.put("isBooked", false);
        allDaySlot.put("isSpecial", false);
        timeSlots.add(0, allDaySlot);
        
        return timeSlots;
    }

    @Override
    @Transactional
    public SpecialArrangement createSpecialArrangement(SpecialArrangementDTO dto) {
        Venue venue = venueMapper.selectById(dto.getVenueId());
        if (venue == null) {
            throw new RuntimeException("场地不存在");
        }

        SpecialArrangement arrangement = new SpecialArrangement();
        BeanUtils.copyProperties(dto, arrangement);
        
        // 设置场地信息
        arrangement.setVenueName(venue.getName());
        arrangement.setVenueType(venue.getType());
        
        // 设置时间段
        if (dto.getTimeSlots().contains("all-day")) {
            arrangement.setTimeRange("全天");
        } else {
            // 合并连续的时间段
            arrangement.setTimeRange(mergeTimeSlots(dto.getTimeSlots()));
        }
        
        // 设置创建信息
        arrangement.setCreatedBy("管理员"); // 实际应该从当前登录用户获取
        arrangement.setCreatedTime(LocalDateTime.now());
        arrangement.setStatus("ACTIVE");
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

        Venue venue = venueMapper.selectById(dto.getVenueId());
        if (venue == null) {
            throw new RuntimeException("场地不存在");
        }

        BeanUtils.copyProperties(dto, existing);
        
        // 更新场地信息
        existing.setVenueName(venue.getName());
        existing.setVenueType(venue.getType());
        
        // 更新时间段
        if (dto.getTimeSlots().contains("all-day")) {
            existing.setTimeRange("全天");
        } else {
            existing.setTimeRange(mergeTimeSlots(dto.getTimeSlots()));
        }
        
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

    private boolean checkTimeSlotBooked(Long venueId, LocalDate date, String timeRange) {
        // TODO: 实现检查时间段是否已被预约的逻辑
        return false;
    }

    private boolean checkTimeSlotSpecial(Long venueId, LocalDate date, String timeRange) {
        LambdaQueryWrapper<SpecialArrangement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialArrangement::getVenueId, venueId)
               .eq(SpecialArrangement::getDate, date)
               .eq(SpecialArrangement::getTimeRange, timeRange);
        
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
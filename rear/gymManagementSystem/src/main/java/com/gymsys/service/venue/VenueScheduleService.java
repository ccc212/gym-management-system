package com.gymsys.service.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gymsys.dto.VenueScheduleDTO;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.reservation.ReservationRepository;
import com.gymsys.repository.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VenueScheduleService {
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public List<VenueScheduleDTO> getWeeklySchedule(LocalDate startDate, LocalDate endDate, String venueType, Long venueId) {
        List<VenueScheduleDTO> schedules = new ArrayList<>();
        
        // 获取所有场地或指定类型的场地
        LambdaQueryWrapper<VenueEntity> venueWrapper = new LambdaQueryWrapper<>();
        if (venueType != null && !venueType.isEmpty()) {
            venueWrapper.eq(VenueEntity::getType, venueType);
        }
        if (venueId != null) {
            venueWrapper.eq(VenueEntity::getId, venueId);
        }
        List<VenueEntity> venues = venueRepository.selectList(venueWrapper);
        
        // 获取日期范围内的所有预约
        LambdaQueryWrapper<ReservationEntity> reservationWrapper = new LambdaQueryWrapper<>();
        reservationWrapper.between(ReservationEntity::getDate, startDate, endDate)
                        .in(ReservationEntity::getStatus, Arrays.asList("CONFIRMED", "IN_USE", "PENDING"));
        List<ReservationEntity> reservations = reservationRepository.selectList(reservationWrapper);
        
        // 按场地ID分组预约
        Map<Long, List<ReservationEntity>> reservationsByVenue = reservations.stream()
                .collect(Collectors.groupingBy(ReservationEntity::getVenueId));
        
        // 生成时间段
        LocalTime startTime = LocalTime.parse("08:00", TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse("22:00", TIME_FORMATTER);
        
        // 为每个场地生成每天的时间段安排
        for (VenueEntity venue : venues) {
            List<ReservationEntity> venueReservations = reservationsByVenue.getOrDefault(venue.getId(), new ArrayList<>());
            
            // 遍历每一天
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // 遍历每个时间段
                LocalTime currentTime = startTime;
                while (currentTime.isBefore(endTime)) {
                    VenueScheduleDTO schedule = new VenueScheduleDTO();
                    schedule.setVenueId(venue.getId());
                    schedule.setVenueName(venue.getName());
                    schedule.setType(venue.getType());
                    schedule.setDate(date);
                    schedule.setTimeSlot(currentTime.format(TIME_FORMATTER) + " - " + 
                                      currentTime.plusMinutes(30).format(TIME_FORMATTER));
                    
                    // 确定该时间段的状态
                    String status = determineTimeSlotStatus(venue, date, currentTime, venueReservations);
                    schedule.setStatus(status);
                    
                    schedules.add(schedule);
                    currentTime = currentTime.plusMinutes(30);
                }
            }
        }
        
        return schedules;
    }
    
    private String determineTimeSlotStatus(VenueEntity venue, LocalDate date, LocalTime time,
                                         List<ReservationEntity> venueReservations) {
        // 检查场地状态
        if ("MAINTENANCE".equals(venue.getStatus())) {
            return "MAINTENANCE";
        }
        
        // 检查是否是维护时间（周一 8:00-10:00）
        if (date.getDayOfWeek().getValue() == 1 && 
            time.getHour() >= 8 && time.getHour() < 10) {
            return "MAINTENANCE";
        }
        
        // 检查是否是特殊场地时间（周六 14:00-18:00）
        if (date.getDayOfWeek().getValue() == 6 && 
            time.getHour() >= 14 && time.getHour() < 18) {
            return "SPECIAL";
        }
        
        // 检查是否是过去的时间
        LocalDateTime slotDateTime = date.atTime(time);
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            return "PAST";
        }
        
        // 检查是否已被预约
        String timeStr = time.format(TIME_FORMATTER);
        for (ReservationEntity reservation : venueReservations) {
            if (reservation.getDate().equals(date)) {
                LocalTime reservedStart = LocalTime.parse(reservation.getStartTime(), TIME_FORMATTER);
                LocalTime reservedEnd = LocalTime.parse(reservation.getEndTime(), TIME_FORMATTER);
                
                if (!(time.plusMinutes(30).isBefore(reservedStart) || time.isAfter(reservedEnd))) {
                    switch (reservation.getStatus()) {
                        case "IN_USE":
                            return "IN_USE";
                        case "CONFIRMED":
                            return "BOOKED";
                        case "PENDING":
                            return "PENDING";
                    }
                }
            }
        }
        
        return "AVAILABLE";
    }
    
    public List<String> getVenueTypes() {
        // 返回所有支持的场地类型
        return Arrays.asList(
            "basketball",
            "football",
            "badminton",
            "tennis",
            "swimming",
            "table_tennis"
        );
    }
    
    public List<VenueScheduleDTO> getVenuesByType(String type) {
        LambdaQueryWrapper<VenueEntity> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            // 将前端传来的英文类型转换为数据库中的类型
            String dbType = type.toLowerCase();
            wrapper.eq(VenueEntity::getType, dbType);
        }
        
        return venueRepository.selectList(wrapper).stream()
                .map(venue -> {
                    VenueScheduleDTO dto = new VenueScheduleDTO();
                    dto.setVenueId(venue.getId());
                    dto.setVenueName(venue.getName());
                    dto.setType(venue.getType());
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 

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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        LambdaQueryWrapper<ReservationEntity> reservationWrapper = new LambdaQueryWrapper<>();
        reservationWrapper.between(ReservationEntity::getStartTime, startDateTime, endDateTime)
                        .in(ReservationEntity::getStatus, "CONFIRMED", "IN_USE", "PENDING");
        List<ReservationEntity> reservations = reservationRepository.selectList(reservationWrapper);
        
        // 按场地ID分组预约
        Map<Long, List<ReservationEntity>> reservationsByVenue = reservations.stream()
                .collect(Collectors.groupingBy(ReservationEntity::getVenueId));
        
        // 生成时间段
        LocalTime startTime = LocalTime.parse("08:00", TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse("22:00", TIME_FORMATTER);
        
        // 为每个时间段生成每个场地的安排
        for (LocalTime currentTime = startTime; currentTime.isBefore(endTime); currentTime = currentTime.plusMinutes(30)) {
            final LocalTime timeSlot = currentTime;
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                final LocalDate currentDate = date;
                for (VenueEntity venue : venues) {
                    VenueScheduleDTO schedule = new VenueScheduleDTO();
                    schedule.setVenueId(venue.getId());
                    schedule.setVenueName(venue.getName());
                    schedule.setType(venue.getType());
                    schedule.setTimeSlot(String.format("%s - %s",
                            timeSlot.format(TIME_FORMATTER),
                            timeSlot.plusMinutes(30).format(TIME_FORMATTER)));
                    schedule.setDate(currentDate.atTime(timeSlot));
                    
                    // 检查该时间段的状态
                    String status = determineTimeSlotStatus(venue, currentDate, timeSlot,
                            reservationsByVenue.getOrDefault(venue.getId(), new ArrayList<>()));
                    schedule.setStatus(status);
                    
                    // 如果场地不可用，标记为维护中
                    if (!venue.getIsAvailable()) {
                        schedule.setStatus("MAINTENANCE");
                    }
                    
                    schedules.add(schedule);
                }
            }
        }
        
        return schedules;
    }
    
    private String determineTimeSlotStatus(VenueEntity venue, LocalDate date, LocalTime time,
                                         List<ReservationEntity> venueReservations) {
        LocalDateTime slotStartTime = date.atTime(time);
        LocalDateTime slotEndTime = date.atTime(time.plusMinutes(30));
        LocalDateTime now = LocalDateTime.now();
        
        // 检查是否在维护时间
        if (isMaintenanceTime(venue, slotStartTime)) {
            return "MAINTENANCE";
        }
        
        // 检查是否是特殊场地时间
        if (isSpecialTime(venue, slotStartTime)) {
            return "SPECIAL";
        }
        
        // 检查是否已被预约
        for (ReservationEntity reservation : venueReservations) {
            if (!(slotEndTime.isBefore(reservation.getStartTime()) ||
                    slotStartTime.isAfter(reservation.getEndTime()))) {
                if ("IN_USE".equals(reservation.getStatus())) {
                    return "IN_USE";
                } else if ("CONFIRMED".equals(reservation.getStatus())) {
                    return "BOOKED";
                } else if ("PENDING".equals(reservation.getStatus())) {
                    return "PENDING";
                }
            }
        }
        
        // 检查是否是过去的时间段
        if (slotEndTime.isBefore(now)) {
            return "PAST";
        }
        
        return "AVAILABLE";
    }
    
    private boolean isMaintenanceTime(VenueEntity venue, LocalDateTime dateTime) {
        // 每周一早上8点到10点是维护时间
        return dateTime.getDayOfWeek().getValue() == 1 &&
                dateTime.getHour() >= 8 && dateTime.getHour() < 10;
    }
    
    private boolean isSpecialTime(VenueEntity venue, LocalDateTime dateTime) {
        // 每周六下午2点到6点是特殊场地时间
        return dateTime.getDayOfWeek().getValue() == 6 &&
                dateTime.getHour() >= 14 && dateTime.getHour() < 18;
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

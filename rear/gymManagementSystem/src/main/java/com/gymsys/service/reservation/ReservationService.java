package com.gymsys.service.reservation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.entity.venue.TimeSlot;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.reservation.ReservationRepository;
import com.gymsys.repository.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService extends ServiceImpl<ReservationRepository, ReservationEntity> {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private VenueRepository venueRepository;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * 获取可用时间段
     */
    public List<TimeSlot> getAvailableTimeSlots(Long venueId, String date) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        VenueEntity venue = venueRepository.selectById(venueId);
        
        if (venue != null) {
            // 设置营业时间
            LocalTime startTime = LocalTime.parse("08:00", TIME_FORMATTER);
            LocalTime endTime = LocalTime.parse("22:00", TIME_FORMATTER);
            
            // 解析日期
            LocalDate localDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDate = LocalDate.parse(date, formatter);
            } catch (Exception ex) {
                throw new RuntimeException("无效的日期格式: " + date);
            }
            
            LocalDate today = LocalDate.now();
            LocalTime nowTime = LocalTime.now();
            
            // 获取该日期的所有预约
            LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ReservationEntity::getVenueId, venueId)
                  .eq(ReservationEntity::getStatus, "CONFIRMED")
                  .eq(ReservationEntity::getDate, localDate);
            List<ReservationEntity> reservations = reservationRepository.selectList(wrapper);
            
            // 生成所有时间段（半小时一段）
            while (startTime.isBefore(endTime)) {
                LocalTime slotStartTime = startTime;
                LocalTime slotEndTime = startTime.plusMinutes(30);
                
                // 检查是否是过期时间段（只有当天的过去时间才标记为已过期）
                if (localDate.equals(today) && slotEndTime.isBefore(nowTime)) {
                    startTime = startTime.plusMinutes(30);
                    continue;
                }
                
                // 检查是否是维护时间（周一 8:00-10:00）
                if (localDate.getDayOfWeek().getValue() == 1 && 
                    startTime.getHour() >= 8 && startTime.getHour() < 10) {
                    TimeSlot timeSlot = new TimeSlot();
                    timeSlot.setStartTime(startTime.format(TIME_FORMATTER));
                    timeSlot.setEndTime(startTime.plusMinutes(30).format(TIME_FORMATTER));
                    timeSlot.setStatus("MAINTENANCE");
                    timeSlot.setPrice(venue.getPricePerHour().divide(BigDecimal.valueOf(2)));
                    timeSlots.add(timeSlot);
                    startTime = startTime.plusMinutes(30);
                    continue;
                }
                
                // 检查是否是特殊场地时间（周六 14:00-18:00）
                if (localDate.getDayOfWeek().getValue() == 6 && 
                    startTime.getHour() >= 14 && startTime.getHour() < 18) {
                    TimeSlot timeSlot = new TimeSlot();
                    timeSlot.setStartTime(startTime.format(TIME_FORMATTER));
                    timeSlot.setEndTime(startTime.plusMinutes(30).format(TIME_FORMATTER));
                    timeSlot.setStatus("SPECIAL");
                    timeSlot.setPrice(venue.getPricePerHour().divide(BigDecimal.valueOf(2)));
                    timeSlots.add(timeSlot);
                    startTime = startTime.plusMinutes(30);
                    continue;
                }
                
                // 检查该时间段是否已被预约
                boolean isAvailable = true;
                for (ReservationEntity reservation : reservations) {
                    LocalTime reservedStart = LocalTime.parse(reservation.getStartTime(), TIME_FORMATTER);
                    LocalTime reservedEnd = LocalTime.parse(reservation.getEndTime(), TIME_FORMATTER);
                    if (!(slotEndTime.isBefore(reservedStart) || slotStartTime.isAfter(reservedEnd))) {
                        isAvailable = false;
                        break;
                    }
                }
                
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setStartTime(startTime.format(TIME_FORMATTER));
                timeSlot.setEndTime(startTime.plusMinutes(30).format(TIME_FORMATTER));
                timeSlot.setStatus(isAvailable ? "AVAILABLE" : "BOOKED");
                timeSlot.setPrice(venue.getPricePerHour().divide(BigDecimal.valueOf(2)));
                timeSlots.add(timeSlot);
                
                startTime = startTime.plusMinutes(30);
            }
        }
        
        return timeSlots;
    }
    
    /**
     * 创建预约
     */
    @Transactional
    public ReservationEntity createReservation(Long venueId, Long userId,
                                             String startTime, String endTime, Integer numberOfPeople, String remarks) {
        System.out.println("准备写入预约: " + venueId + "," + userId + "," + startTime + "," + endTime);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setVenueId(venueId);
        reservation.setUserId(userId);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setRemarks(remarks);
        reservation.setStatus("PENDING");
        reservation.setCreatedTime(LocalDateTime.now());
        reservation.setUpdatedTime(LocalDateTime.now());

        // 计算预约日期
        LocalDate reservationDate = LocalDate.now();
        reservation.setDate(reservationDate);

        // 获取场馆信息计算费用
        VenueEntity venue = venueRepository.selectById(venueId);
        if (venue != null) {
            LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
            LocalTime end = LocalTime.parse(endTime, TIME_FORMATTER);
            double hours = (end.toSecondOfDay() - start.toSecondOfDay()) / 3600.0;
            reservation.setCost(venue.getPricePerHour().multiply(BigDecimal.valueOf(hours)));
        }

        // 保存预约
        save(reservation);
        
        // 设置场馆信息
        reservation.setVenueInfo(venue);
        
        return reservation;
    }
    
    private BigDecimal calculateTotalPrice(BigDecimal pricePerHour, LocalDateTime startTime, LocalDateTime endTime) {
        long hours = java.time.Duration.between(startTime, endTime).toHours();
        return pricePerHour.multiply(BigDecimal.valueOf(hours));
    }
    
    /**
     * 确认预约
     */
    @Transactional
    public void confirmReservation(Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation != null) {
            reservation.setStatus("CONFIRMED");
            reservationRepository.updateById(reservation);
        }
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public void cancelReservation(Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            reservationRepository.updateById(reservation);
        }
    }
    
    /**
     * 完成预约
     */
    @Transactional
    public void completeReservation(Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation != null) {
            reservation.setStatus("COMPLETED");
            reservationRepository.updateById(reservation);
        }
    }
    
    /**
     * 修改预约类型
     */
    @Transactional
    public ReservationEntity updateReservationType(Long id, String type) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        reservation.setReservationType(type);
        reservation.setUpdatedTime(LocalDateTime.now());
        reservationRepository.updateById(reservation);
        return reservation;
    }
    
    /**
     * 获取场馆的预约列表
     */
    public Page<ReservationEntity> getVenueReservations(Long venueId, int page, int size) {
        return reservationRepository.findByVenueId(venueId, new Page<>(page, size));
    }
    
    /**
     * 获取用户的预约列表
     */
    public Page<ReservationEntity> getUserReservations(Long userId, int page, int size) {
        return reservationRepository.findByUserId(userId, new Page<>(page, size));
    }
    
    /**
     * 获取指定状态的预约列表
     */
    public Page<ReservationEntity> getReservationsByStatus(String status, int page, int size) {
        return reservationRepository.findByStatus(status, new Page<>(page, size));
    }
    
    /**
     * 获取场馆的每周预约统计
     */
    public List<ReservationEntity> getVenueWeeklyReservations(Long venueId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationEntity::getVenueId, venueId)
                .between(ReservationEntity::getStartTime, startTime, endTime);
        return reservationRepository.selectList(wrapper);
    }
    
    /**
     * 获取预约详情
     */
    public ReservationEntity getReservationById(Long id) {
        return reservationRepository.selectById(id);
    }
    
    public Page<ReservationEntity> page(Page<ReservationEntity> page, LambdaQueryWrapper<ReservationEntity> wrapper) {
        Page<ReservationEntity> result = super.page(page, wrapper);
        
        // 填充场馆信息
        result.getRecords().forEach(reservation -> {
            VenueEntity venue = venueRepository.selectById(reservation.getVenueId());
            reservation.setVenueInfo(venue);
        });
        
        return result;
    }
} 
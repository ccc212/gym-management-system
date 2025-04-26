package com.gymsys.service.reservation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class ReservationService {
    
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
            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(date + "T00:00:00");
            } catch (Exception e) {
                // 如果解析失败，尝试其他格式
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(date, formatter);
                    dateTime = localDate.atStartOfDay();
                } catch (Exception ex) {
                    throw new RuntimeException("无效的日期格式: " + date);
                }
            }
            
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            
            // 获取该日期的所有预约
            LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ReservationEntity::getVenueId, venueId)
                  .eq(ReservationEntity::getStatus, "CONFIRMED")
                  .ge(ReservationEntity::getStartTime, dateTime)
                  .lt(ReservationEntity::getStartTime, dateTime.plusDays(1));
            List<ReservationEntity> reservations = reservationRepository.selectList(wrapper);
            
            // 生成所有时间段（半小时一段）
            while (startTime.isBefore(endTime)) {
                LocalDateTime slotStartTime = dateTime.with(startTime);
                LocalDateTime slotEndTime = dateTime.with(startTime.plusMinutes(30));
                
                // 检查是否是过期时间段（只有当天的过去时间才标记为已过期）
                if (dateTime.toLocalDate().equals(today) && slotEndTime.isBefore(now)) {
                    startTime = startTime.plusMinutes(30);
                    continue;
                }
                
                // 检查是否是维护时间（周一 8:00-10:00）
                if (dateTime.getDayOfWeek().getValue() == 1 && 
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
                if (dateTime.getDayOfWeek().getValue() == 6 && 
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
                    if (!(slotEndTime.isBefore(reservation.getStartTime()) || 
                          slotStartTime.isAfter(reservation.getEndTime()))) {
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
    public ReservationEntity createReservation(Long venueId, Long userId, String cardNumber, 
                                             LocalDateTime startTime, LocalDateTime endTime, 
                                             Integer numberOfPeople, String remarks) {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setVenueId(venueId);
        reservation.setUserId(userId);
        reservation.setCardNumber(cardNumber);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setRemark(remarks);
        reservation.setStatus("PENDING");
        reservation.setReservationType("NORMAL");
        
        reservationRepository.insert(reservation);
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
        reservation.setUpdatedAt(LocalDateTime.now());
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
    
    /**
     * 根据卡号和状态查询预约
     */
    public List<ReservationEntity> getReservationsByCardNumberAndStatus(String cardNumber, String status) {
        LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationEntity::getCardNumber, cardNumber)
                .eq(ReservationEntity::getStatus, status);
        return reservationRepository.selectList(wrapper);
    }
    
    public Page<ReservationEntity> getReservationsByCardNumber(String cardNumber, int page, int size) {
        LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationEntity::getCardNumber, cardNumber)
              .orderByDesc(ReservationEntity::getStartTime);
        
        return reservationRepository.selectPage(new Page<>(page, size), wrapper);
    }
    
    public Page<ReservationEntity> getReservationsByVenueId(Long venueId, int page, int size) {
        LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationEntity::getVenueId, venueId)
              .orderByDesc(ReservationEntity::getStartTime);
        
        return reservationRepository.selectPage(new Page<>(page, size), wrapper);
    }
    
    public Page<ReservationEntity> getReservationsByUserId(Long userId, int page, int size) {
        LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationEntity::getUserId, userId)
              .orderByDesc(ReservationEntity::getStartTime);
        
        return reservationRepository.selectPage(new Page<>(page, size), wrapper);
    }
} 
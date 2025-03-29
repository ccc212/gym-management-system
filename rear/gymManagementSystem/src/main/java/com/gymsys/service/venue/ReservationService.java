package com.gymsys.service.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.ReservationRepository;
import com.gymsys.repository.venue.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final VenueRepository venueRepository;
    
    /**
     * 场地预约（用一卡通号码预约，一周内预约）
     */
    @Transactional
    public ReservationEntity reserveVenue(Long venueId, String cardNumber, 
                                        LocalDateTime startTime, LocalDateTime endTime) {
        // 检查预约时间是否在一周内
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekLater = now.plusWeeks(1);
        
        if (startTime.isBefore(now) || startTime.isAfter(oneWeekLater)) {
            throw new IllegalArgumentException("预约时间必须在一周内");
        }
        
        // 检查场地是否存在
        Optional<VenueEntity> venueOpt = venueRepository.findById(venueId);
        if (venueOpt.isEmpty()) {
            throw new IllegalArgumentException("场地不存在");
        }
        
        VenueEntity venue = venueOpt.get();
        
        // 检查场地是否可用
        if (!venue.isAvailable()) {
            throw new IllegalArgumentException("场地不可用");
        }
        
        // 检查时间段是否已被预约
        List<ReservationEntity> existingReservations = reservationRepository
                .findByVenueAndStartTimeBetween(venue, startTime.minusMinutes(1), endTime.plusMinutes(1));
        
        if (!existingReservations.isEmpty()) {
            throw new IllegalArgumentException("该时段已被预约");
        }
        
        // 创建预约
        ReservationEntity reservation = new ReservationEntity();
        reservation.setVenue(venue);
        reservation.setCardNumber(cardNumber);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setReservationType("REGULAR");
        reservation.setStatus("BOOKED");
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * 场地预约退订
     */
    @Transactional
    public void cancelReservation(Long reservationId, String cardNumber) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
        
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("预约不存在");
        }
        
        ReservationEntity reservation = reservationOpt.get();
        
        // 验证一卡通号码
        if (!reservation.getCardNumber().equals(cardNumber)) {
            throw new IllegalArgumentException("无权取消此预约");
        }
        
        // 检查是否已经开始或已经取消
        if (reservation.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("预约已开始，无法取消");
        }
        
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new IllegalArgumentException("预约已经取消");
        }
        
        // 更新状态
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }
    
    /**
     * 场地预约修改
     */
    @Transactional
    public ReservationEntity modifyReservation(Long reservationId, String cardNumber,
                                             LocalDateTime newStartTime, LocalDateTime newEndTime) {
        // 验证预约是否存在
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
        
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("预约不存在");
        }
        
        ReservationEntity reservation = reservationOpt.get();
        
        // 验证一卡通号码
        if (!reservation.getCardNumber().equals(cardNumber)) {
            throw new IllegalArgumentException("无权修改此预约");
        }
        
        // 检查是否已经开始或已经取消
        if (reservation.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("预约已开始，无法修改");
        }
        
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new IllegalArgumentException("预约已经取消，无法修改");
        }
        
        // 检查新的时间是否在一周内
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekLater = now.plusWeeks(1);
        
        if (newStartTime.isBefore(now) || newStartTime.isAfter(oneWeekLater)) {
            throw new IllegalArgumentException("预约时间必须在一周内");
        }
        
        // 检查新时段是否可用（排除当前预约）
        List<ReservationEntity> existingReservations = reservationRepository
                .findByVenueAndStartTimeBetween(reservation.getVenue(), 
                                              newStartTime.minusMinutes(1), 
                                              newEndTime.plusMinutes(1));
        
        boolean timeSlotAvailable = existingReservations.isEmpty() || 
                (existingReservations.size() == 1 && existingReservations.get(0).getId().equals(reservationId));
        
        if (!timeSlotAvailable) {
            throw new IllegalArgumentException("该时段已被预约");
        }
        
        // 更新预约
        reservation.setStartTime(newStartTime);
        reservation.setEndTime(newEndTime);
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * 场地预约失约处理
     */
    @Transactional
    public void handleNoShow(Long reservationId) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
        
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("预约不存在");
        }
        
        ReservationEntity reservation = reservationOpt.get();
        
        // 只有已预约的才能标记为失约
        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new IllegalArgumentException("只有已预约的才能标记为失约");
        }
        
        reservation.setStatus("NO_SHOW");
        reservationRepository.save(reservation);
    }
    
    /**
     * 校队预留（使用）场地
     */
    @Transactional
    public ReservationEntity reserveVenueForTeam(Long venueId, 
                                               LocalDateTime startTime, 
                                               LocalDateTime endTime) {
        // 通过调用普通预约方法，但使用特殊的"TEAM"标识
        ReservationEntity reservation = reserveVenue(venueId, "TEAM", startTime, endTime);
        reservation.setReservationType("TEAM");
        return reservationRepository.save(reservation);
    }
    
    /**
     * 上课使用场地
     */
    @Transactional
    public ReservationEntity reserveVenueForClass(Long venueId, 
                                                LocalDateTime startTime, 
                                                LocalDateTime endTime) {
        // 通过调用普通预约方法，但使用特殊的"CLASS"标识
        ReservationEntity reservation = reserveVenue(venueId, "CLASS", startTime, endTime);
        reservation.setReservationType("CLASS");
        return reservationRepository.save(reservation);
    }
    
    /**
     * 场地一周信息查询
     */
    public List<ReservationEntity> getVenueWeeklyReservations(Long venueId) {
        LocalDate today = LocalDate.now();
        LocalDateTime weekStart = today.atStartOfDay();
        LocalDateTime weekEnd = today.plusDays(7).atStartOfDay();
        
        return reservationRepository.findVenueWeeklyReservations(venueId, weekStart, weekEnd);
    }
    
    /**
     * 根据ID查询预约
     */
    public Optional<ReservationEntity> findReservationById(Long id) {
        return reservationRepository.findById(id);
    }
    
    /**
     * 查询用户所有预约
     */
    public List<ReservationEntity> findUserReservations(String cardNumber) {
        return reservationRepository.findByCardNumberAndStatus(cardNumber, "BOOKED");
    }
}
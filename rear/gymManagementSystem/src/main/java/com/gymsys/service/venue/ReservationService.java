package com.gymsys.service.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.ReservationRepository;
import com.gymsys.repository.venue.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final VenueRepository venueRepository;
    
    /**
     * 创建预约
     */
    @Transactional
    public ReservationEntity createReservation(ReservationEntity reservation) {
        // 检查场馆是否存在且可用
        VenueEntity venue = venueRepository.selectById(reservation.getVenueId());
        if (venue == null) {
            throw new RuntimeException("场馆不存在");
        }
        
        if (!venue.getIsAvailable()) {
            throw new RuntimeException("场馆不可用");
        }
        
        // 检查时间冲突
        List<ReservationEntity> overlappingReservations = reservationRepository.findOverlappingReservations(
                reservation.getVenueId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
        
        if (!overlappingReservations.isEmpty()) {
            throw new RuntimeException("该时间段已被预约");
        }
        
        // 设置预约状态和时间
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        // 保存预约
        reservationRepository.insert(reservation);
        return reservation;
    }
    
    /**
     * 确认预约
     */
    @Transactional
    public ReservationEntity confirmReservation(Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        if (!reservation.getCardNumber().equals(reservation.getCardNumber())) {
            throw new RuntimeException("卡号不匹配");
        }
        
        reservation.setStatus("CONFIRMED");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.updateById(reservation);
        
        return reservation;
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public ReservationEntity cancelReservation(Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        if (!reservation.getCardNumber().equals(reservation.getCardNumber())) {
            throw new RuntimeException("卡号不匹配");
        }
        
        reservation.setStatus("CANCELLED");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.updateById(reservation);
        
        return reservation;
    }
    
    /**
     * 完成预约
     */
    @Transactional
    public ReservationEntity completeReservation(Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        if (!reservation.getCardNumber().equals(reservation.getCardNumber())) {
            throw new RuntimeException("卡号不匹配");
        }
        
        reservation.setStatus("COMPLETED");
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.updateById(reservation);
        
        return reservation;
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
}
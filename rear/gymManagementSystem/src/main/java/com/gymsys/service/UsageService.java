package com.gymsys.service;

import com.gymsys.entity.ReservationEntity;
import com.gymsys.entity.UsageEntity;
import com.gymsys.entity.VenueEntity;
import com.gymsys.repository.ReservationRepository;
import com.gymsys.repository.UsageRepository;
import com.gymsys.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsageService {
    
    private final UsageRepository usageRepository;
    private final VenueRepository venueRepository;
    private final ReservationRepository reservationRepository;
    
    /**
     * 场地使用开始
     */
    @Transactional
    public UsageEntity startVenueUsage(Long venueId, String cardNumber, Long reservationId) {
        // 检查场地是否存在
        Optional<VenueEntity> venueOpt = venueRepository.findById(venueId);
        if (venueOpt.isEmpty()) {
            throw new IllegalArgumentException("场地不存在");
        }
        
        VenueEntity venue = venueOpt.get();
        
        // 创建使用记录
        UsageEntity usage = new UsageEntity();
        usage.setVenue(venue);
        usage.setCardNumber(cardNumber);
        usage.setStartTime(LocalDateTime.now());
        
        // 如果有预约ID，关联预约
        if (reservationId != null) {
            Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
            if (reservationOpt.isPresent()) {
                ReservationEntity reservation = reservationOpt.get();
                
                // 验证一卡通号码
                if (!reservation.getCardNumber().equals(cardNumber) && 
                    !reservation.getCardNumber().equals("TEAM") && 
                    !reservation.getCardNumber().equals("CLASS")) {
                    throw new IllegalArgumentException("无权使用此预约");
                }
                
                usage.setReservation(reservation);
                
                // 更新预约状态
                reservation.setStatus("COMPLETED");
                reservationRepository.save(reservation);
            }
        }
        
        return usageRepository.save(usage);
    }
    
    /**
     * 场地使用结束，并计算费用
     */
    @Transactional
    public UsageEntity endVenueUsage(Long usageId) {
        // 检查使用记录是否存在
        Optional<UsageEntity> usageOpt = usageRepository.findById(usageId);
        if (usageOpt.isEmpty()) {
            throw new IllegalArgumentException("使用记录不存在");
        }
        
        UsageEntity usage = usageOpt.get();
        
        // 检查是否已经结束
        if (usage.getEndTime() != null) {
            throw new IllegalArgumentException("场地使用已经结束");
        }
        
        // 设置结束时间
        LocalDateTime endTime = LocalDateTime.now();
        usage.setEndTime(endTime);
        
        // 计算使用时长（小时）
        double hours = Duration.between(usage.getStartTime(), endTime).toMinutes() / 60.0;
        
        // 计算费用
        BigDecimal pricePerHour = usage.getVenue().getPricePerHour();
        BigDecimal cost = pricePerHour.multiply(BigDecimal.valueOf(hours));
        
        usage.setCost(cost);
        
        return usageRepository.save(usage);
    }
    
    /**
     * 一卡通付费
     */
    @Transactional
    public UsageEntity payUsage(Long usageId) {
        // 检查使用记录是否存在
        Optional<UsageEntity> usageOpt = usageRepository.findById(usageId);
        if (usageOpt.isEmpty()) {
            throw new IllegalArgumentException("使用记录不存在");
        }
        
        UsageEntity usage = usageOpt.get();
        
        // 检查是否已经付费
        if (usage.getPaid()) {
            throw new IllegalArgumentException("已经付费");
        }
        
        // 检查是否已经结束
        if (usage.getEndTime() == null) {
            throw new IllegalArgumentException("场地使用尚未结束，无法付费");
        }
        
        // 更新付费状态
        usage.setPaid(true);
        
        return usageRepository.save(usage);
    }
    
    /**
     * 场地收费标准查询
     */
    public BigDecimal getVenuePrice(Long venueId) {
        Optional<VenueEntity> venueOpt = venueRepository.findById(venueId);
        return venueOpt.map(VenueEntity::getPricePerHour)
                .orElseThrow(() -> new IllegalArgumentException("场地不存在"));
    }
    
    /**
     * 查询正在使用的场地
     */
    public List<UsageEntity> getActiveUsages() {
        return usageRepository.findByEndTimeIsNull();
    }
    
    /**
     * 查询用户未付费的记录
     */
    public List<UsageEntity> getUnpaidUsages(String cardNumber) {
        return usageRepository.findByCardNumberAndPaid(cardNumber, false);
    }
}
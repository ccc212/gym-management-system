package com.gymsys.service.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.UsageEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.UsageRepository;
import com.gymsys.repository.venue.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageService {
    
    private final UsageRepository usageRepository;
    private final VenueRepository venueRepository;
    
    /**
     * 开始使用场馆
     */
    @Transactional
    public UsageEntity startUsage(UsageEntity usage) {
        // 检查场馆是否存在且可用
        VenueEntity venue = venueRepository.selectById(usage.getVenueId());
        if (venue == null) {
            throw new RuntimeException("场馆不存在");
        }
        
        if (!venue.getIsAvailable()) {
            throw new RuntimeException("场馆不可用");
        }
        
        // 设置使用记录
        usage.setStartTime(LocalDateTime.now());
        usage.setPaid(false);
        usage.setCreatedAt(LocalDateTime.now());
        usage.setUpdatedAt(LocalDateTime.now());
        
        // 保存使用记录
        usageRepository.insert(usage);
        return usage;
    }
    
    /**
     * 结束使用场馆
     */
    @Transactional
    public UsageEntity endUsage(Long id) {
        UsageEntity usage = usageRepository.selectById(id);
        if (usage == null) {
            throw new RuntimeException("使用记录不存在");
        }
        
        if (usage.getEndTime() != null) {
            throw new RuntimeException("该使用记录已结束");
        }
        
        // 设置结束时间
        usage.setEndTime(LocalDateTime.now());
        usage.setUpdatedAt(LocalDateTime.now());
        
        // 计算费用
        VenueEntity venue = venueRepository.selectById(usage.getVenueId());
        long hours = ChronoUnit.HOURS.between(usage.getStartTime(), usage.getEndTime());
        usage.setCost(venue.getPricePerHour().multiply(BigDecimal.valueOf(hours)));
        
        // 更新使用记录
        usageRepository.updateById(usage);
        return usage;
    }
    
    /**
     * 支付使用费用
     */
    @Transactional
    public UsageEntity payUsage(Long id) {
        UsageEntity usage = usageRepository.selectById(id);
        if (usage == null) {
            throw new RuntimeException("使用记录不存在");
        }
        
        if (usage.getPaid()) {
            throw new RuntimeException("该使用记录已支付");
        }
        
        if (usage.getEndTime() == null) {
            throw new RuntimeException("使用尚未结束，无法支付");
        }
        
        // 设置支付状态
        usage.setPaid(true);
        usage.setUpdatedAt(LocalDateTime.now());
        
        // 更新使用记录
        usageRepository.updateById(usage);
        return usage;
    }
    
    /**
     * 获取场馆的使用记录
     */
    public Page<UsageEntity> getVenueUsages(Long venueId, int page, int size) {
        return usageRepository.findByVenueId(venueId, new Page<>(page, size));
    }
    
    /**
     * 获取用户的使用记录
     */
    public Page<UsageEntity> getUserUsages(Long userId, int page, int size) {
        return usageRepository.findByUserId(userId, new Page<>(page, size));
    }
    
    /**
     * 获取指定时间范围内的使用记录
     */
    public Page<UsageEntity> getUsagesByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        LambdaQueryWrapper<UsageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(UsageEntity::getStartTime, startTime, endTime);
        return usageRepository.selectPage(new Page<>(page, size), wrapper);
    }
    
    /**
     * 获取进行中的使用记录
     */
    public Page<UsageEntity> getActiveUsages(int page, int size) {
        LambdaQueryWrapper<UsageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(UsageEntity::getEndTime);
        return usageRepository.selectPage(new Page<>(page, size), wrapper);
    }
    
    /**
     * 获取指定卡号的使用记录
     */
    public Page<UsageEntity> getUsagesByCardNumber(String cardNumber, int page, int size) {
        LambdaQueryWrapper<UsageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsageEntity::getCardNumber, cardNumber);
        return usageRepository.selectPage(new Page<>(page, size), wrapper);
    }
}
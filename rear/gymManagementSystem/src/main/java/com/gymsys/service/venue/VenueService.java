package com.gymsys.service.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    
    private final VenueRepository venueRepository;
    
    /**
     * 创建场馆
     */
    @Transactional
    public VenueEntity createVenue(VenueEntity venue) {
        venue.setCreatedAt(LocalDateTime.now());
        venue.setUpdatedAt(LocalDateTime.now());
        venue.setIsAvailable(true);
        venue.setStatus("ACTIVE");
        
        venueRepository.insert(venue);
        return venue;
    }
    
    /**
     * 更新场馆信息
     */
    @Transactional
    public VenueEntity updateVenue(VenueEntity venue) {
        VenueEntity existingVenue = venueRepository.selectById(venue.getId());
        if (existingVenue == null) {
            throw new RuntimeException("场馆不存在");
        }
        
        venue.setUpdatedAt(LocalDateTime.now());
        venueRepository.updateById(venue);
        return venue;
    }
    
    /**
     * 获取场馆详情
     */
    public VenueEntity getVenueById(Long id) {
        return venueRepository.selectById(id);
    }
    
    /**
     * 获取所有场馆
     */
    public List<VenueEntity> getAllVenues() {
        return venueRepository.selectList(new LambdaQueryWrapper<>());
    }
    
    /**
     * 获取可用场馆
     */
    public List<VenueEntity> getAvailableVenues() {
        LambdaQueryWrapper<VenueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueEntity::getIsAvailable, true);
        return venueRepository.selectList(wrapper);
    }
    
    /**
     * 获取指定类型的场馆
     */
    public List<VenueEntity> getVenuesByType(String type) {
        LambdaQueryWrapper<VenueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VenueEntity::getType, type);
        return venueRepository.selectList(wrapper);
    }
    
    /**
     * 获取指定状态的场馆
     */
    public Page<VenueEntity> getVenuesByStatus(String status, int page, int size) {
        return venueRepository.findByStatus(status, new Page<>(page, size));
    }
    
    /**
     * 更新场馆状态
     */
    @Transactional
    public VenueEntity updateVenueStatus(Long id, String status) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            throw new RuntimeException("场馆不存在");
        }
        
        venue.setStatus(status);
        venue.setUpdatedAt(LocalDateTime.now());
        venueRepository.updateById(venue);
        return venue;
    }
    
    /**
     * 更新场馆可用性
     */
    @Transactional
    public VenueEntity updateVenueAvailability(Long id, boolean isAvailable) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            throw new RuntimeException("场馆不存在");
        }
        
        venue.setIsAvailable(isAvailable);
        venue.setUpdatedAt(LocalDateTime.now());
        venueRepository.updateById(venue);
        return venue;
    }
}
package com.gymsys.service;

import gym.entity.VenueEntity;
import gym.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueService {
    
    private final VenueRepository venueRepository;
    
    /**
     * 添加场地
     */
    @Transactional
    public VenueEntity addVenue(VenueEntity venue) {
        return venueRepository.save(venue);
    }
    
    /**
     * 删除场地
     */
    @Transactional
    public void removeVenue(Long venueId) {
        venueRepository.deleteById(venueId);
    }
    
    /**
     * 更新场地信息
     */
    @Transactional
    public VenueEntity updateVenue(VenueEntity venue) {
        return venueRepository.save(venue);
    }
    
    /**
     * 根据ID查找场地
     */
    public Optional<VenueEntity> findVenueById(Long venueId) {
        return venueRepository.findById(venueId);
    }
    
    /**
     * 查询所有场地
     */
    public List<VenueEntity> findAllVenues() {
        return venueRepository.findAll();
    }
    
    /**
     * 查询可用场地
     */
    public List<VenueEntity> findAvailableVenues() {
        return venueRepository.findByIsAvailable(true);
    }
    
    /**
     * 查询特定类型的场地
     */
    public List<VenueEntity> findVenuesByType(String type) {
        return venueRepository.findByType(type);
    }
}
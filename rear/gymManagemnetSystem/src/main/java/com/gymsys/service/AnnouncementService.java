package com.gymsys.service;

import gym.entity.AnnouncementEntity;
import gym.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    
    /**
     * 创建公告
     */
    @Transactional
    public AnnouncementEntity createAnnouncement(AnnouncementEntity announcement) {
        announcement.setPublishTime(LocalDateTime.now());
        return announcementRepository.save(announcement);
    }
    
    /**
     * 获取所有有效公告
     */
    public List<AnnouncementEntity> getActiveAnnouncements() {
        LocalDateTime now = LocalDateTime.now();
        return announcementRepository.findByActiveTrueAndPublishTimeBeforeAndExpireTimeAfter(now, now);
    }
    
    /**
     * 停用公告
     */
    @Transactional
    public void deactivateAnnouncement(Long id) {
        Optional<AnnouncementEntity> announcementOpt = announcementRepository.findById(id);
        announcementOpt.ifPresent(announcement -> {
            announcement.setActive(false);
            announcementRepository.save(announcement);
        });
    }
    
    /**
     * 获取最新公告
     */
    public String getLatestAnnouncementContent() {
        List<AnnouncementEntity> activeAnnouncements = getActiveAnnouncements();
        if (activeAnnouncements.isEmpty()) {
            return "欢迎使用场馆，请遵守相关规定。";
        }
        
        // 按发布时间排序，取最新的
        return activeAnnouncements.stream()
                .sorted((a1, a2) -> a2.getPublishTime().compareTo(a1.getPublishTime()))
                .findFirst()
                .map(AnnouncementEntity::getContent)
                .orElse("欢迎使用场馆，请遵守相关规定。");
    }
}
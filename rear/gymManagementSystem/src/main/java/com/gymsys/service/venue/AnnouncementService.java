package com.gymsys.service.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gymsys.entity.venue.AnnouncementEntity;
import com.gymsys.repository.venue.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        announcement.setActive(true);
        announcementRepository.insert(announcement);
        return announcement;
    }
    
    /**
     * 获取所有有效公告
     */
    public List<AnnouncementEntity> getActiveAnnouncements() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementEntity::getActive, true)
                .lt(AnnouncementEntity::getPublishTime, now)
                .gt(AnnouncementEntity::getExpireTime, now);
        return announcementRepository.selectList(wrapper);
    }
    
    /**
     * 停用公告
     */
    @Transactional
    public void deactivateAnnouncement(Long id) {
        AnnouncementEntity announcement = announcementRepository.selectById(id);
        if (announcement != null) {
            announcement.setActive(false);
            announcementRepository.updateById(announcement);
        }
    }
    
    /**
     * 获取最新公告
     */
    public List<AnnouncementEntity> getLatestAnnouncements() {
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementEntity::getActive, true)
                .orderByDesc(AnnouncementEntity::getPublishTime)
                .last("LIMIT 5");
        return announcementRepository.selectList(wrapper);
    }
}
package com.gymsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Announcement;
import com.gymsys.mapper.AnnouncementMapper;
import com.gymsys.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public Page<Announcement> getAnnouncements(
            Integer page, 
            Integer size, 
            String type, 
            String status, 
            LocalDate startDate, 
            LocalDate endDate, 
            String keyword) {
        
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(type)) {
            wrapper.eq(Announcement::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Announcement::getStatus, status);
        }
        if (startDate != null) {
            wrapper.ge(Announcement::getPublishDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Announcement::getEndDate, endDate);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword)
                  .or()
                  .like(Announcement::getContent, keyword);
        }
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(Announcement::getCreatedAt);
        
        return announcementMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    public Announcement updateAnnouncement(Announcement announcement) {
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.updateById(announcement);
        return announcement;
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    @Override
    public Announcement getAnnouncementDetail(Long id) {
        return announcementMapper.selectById(id);
    }
} 
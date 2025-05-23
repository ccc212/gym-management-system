package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Announcement;
import com.gymsys.mapper.AnnouncementMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDate;

@Repository
public class AnnouncementRepository {

    @Resource
    private AnnouncementMapper announcementMapper;

    public Page<Announcement> getAnnouncements(
            Integer page,
            Integer size,
            String type,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            String keyword) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        if (startDate != null) {
            wrapper.ge(Announcement::getPublishDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Announcement::getEndDate, endDate);
        }
        if (keyword != null) {
            wrapper.like(Announcement::getTitle, keyword)
                  .or()
                  .like(Announcement::getContent, keyword);
        }
        
        wrapper.orderByDesc(Announcement::getCreatedAt);
        
        return announcementMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Announcement createAnnouncement(Announcement announcement) {
        announcementMapper.insert(announcement);
        return announcement;
    }

    public Announcement updateAnnouncement(Announcement announcement) {
        announcementMapper.updateById(announcement);
        return announcement;
    }

    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    public Announcement getAnnouncementDetail(Long id) {
        return announcementMapper.selectById(id);
    }
}
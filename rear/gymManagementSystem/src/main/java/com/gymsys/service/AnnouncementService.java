package com.gymsys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Announcement;

import java.time.LocalDate;

public interface AnnouncementService {
    
    Page<Announcement> getAnnouncements(
        Integer page, 
        Integer size, 
        String type, 
        String status, 
        LocalDate startDate, 
        LocalDate endDate, 
        String keyword
    );
    
    Announcement createAnnouncement(Announcement announcement);
    
    Announcement updateAnnouncement(Announcement announcement);
    
    void deleteAnnouncement(Long id);
    
    Announcement getAnnouncementDetail(Long id);
} 
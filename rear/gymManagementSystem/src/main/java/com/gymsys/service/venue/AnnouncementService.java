package com.gymsys.service.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Announcement;
import com.gymsys.repository.venue.AnnouncementRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    @Resource
    private AnnouncementRepository announcementRepository;

    public Page<Announcement> getAnnouncements(
            Integer page,
            Integer size,
            String type,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            String keyword) {
        return announcementRepository.getAnnouncements(page, size, type, status, startDate, endDate, keyword);
    }

    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        return announcementRepository.createAnnouncement(announcement);
    }

    public Announcement updateAnnouncement(Announcement announcement) {
        announcement.setUpdatedAt(LocalDateTime.now());
        return announcementRepository.updateAnnouncement(announcement);
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteAnnouncement(id);
    }

    public Announcement getAnnouncementDetail(Long id) {
        return announcementRepository.getAnnouncementDetail(id);
    }
}
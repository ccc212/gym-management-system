package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.common.Result;
import com.gymsys.entity.Announcement;
import com.gymsys.service.venue.AnnouncementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping({"/api/announcements", "/announcements"})
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @GetMapping
    public Result<Page<Announcement>> getAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword) {
        Page<Announcement> announcements = announcementService.getAnnouncements(
            page, size, type, status, startDate, endDate, keyword);
        return Result.success(announcements);
    }

    @PostMapping
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        Announcement created = announcementService.createAnnouncement(announcement);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Announcement> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody Announcement announcement) {
        announcement.setId(id);
        Announcement updated = announcementService.updateAnnouncement(announcement);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementDetail(id);
        return Result.success(announcement);
    }
}
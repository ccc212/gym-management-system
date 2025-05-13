package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.AnnouncementEntity;
import com.gymsys.repository.venue.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @GetMapping
    public ResponseEntity<Page<AnnouncementEntity>> getAllAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<AnnouncementEntity> announcementPage = announcementRepository.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<AnnouncementEntity>()
        );
        return ResponseEntity.ok(announcementPage);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementEntity>> getActiveAnnouncements() {
        List<AnnouncementEntity> announcements = announcementRepository.findByStatus("ACTIVE");
        return ResponseEntity.ok(announcements);
    }
    
    @GetMapping("/time-range")
    public ResponseEntity<List<AnnouncementEntity>> getAnnouncementsByTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        List<AnnouncementEntity> announcements = announcementRepository.findByPublishTimeBetween(startTime, endTime);
        return ResponseEntity.ok(announcements);
    }
    
    @PostMapping
    public ResponseEntity<AnnouncementEntity> createAnnouncement(@RequestBody AnnouncementEntity announcement) {
        announcementRepository.insert(announcement);
        return ResponseEntity.ok(announcement);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementEntity> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementEntity announcement) {
        announcement.setId(id);
        announcementRepository.updateById(announcement);
        return ResponseEntity.ok(announcement);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
package com.gymsys.controller;

import com.gymsys.entity.AnnouncementEntity;
import com.gymsys.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementController {// 代码行数：30
    
    private final AnnouncementService announcementService;
    
    /**
     * 创建公告
     */
    @PostMapping
    public ResponseEntity<AnnouncementEntity> createAnnouncement(@RequestBody AnnouncementEntity announcement) {
        AnnouncementEntity savedAnnouncement = announcementService.createAnnouncement(announcement);
        return new ResponseEntity<>(savedAnnouncement, HttpStatus.CREATED);
    }
    
    /**
     * 获取所有有效公告
     */
    @GetMapping
    public ResponseEntity<List<AnnouncementEntity>> getActiveAnnouncements() {
        List<AnnouncementEntity> announcements = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(announcements);
    }
    
    /**
     * 停用公告
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateAnnouncement(@PathVariable Long id) {
        announcementService.deactivateAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 获取最新公告内容
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, String>> getLatestAnnouncement() {
        String content = announcementService.getLatestAnnouncementContent();
        return ResponseEntity.ok(Map.of("content", content));
    }
}
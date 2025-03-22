package com.gymsys.service;

import com.gymsys.entity.AnnouncementEntity;
import com.gymsys.repository.AnnouncementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    private AnnouncementEntity testAnnouncement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置测试公告
        testAnnouncement = new AnnouncementEntity();
        testAnnouncement.setId(1L);
        testAnnouncement.setTitle("测试公告");
        testAnnouncement.setContent("这是一条测试公告内容");
        testAnnouncement.setPublishTime(LocalDateTime.now().minusDays(1));
        testAnnouncement.setExpireTime(LocalDateTime.now().plusDays(7));
        testAnnouncement.setActive(true);
    }

    @Test
    void testCreateAnnouncement() {
        when(announcementRepository.save(any(AnnouncementEntity.class))).thenReturn(testAnnouncement);
        
        AnnouncementEntity announcement = announcementService.createAnnouncement(testAnnouncement);
        
        assertNotNull(announcement);
        assertEquals("测试公告", announcement.getTitle());
        assertNotNull(announcement.getPublishTime());
        
        verify(announcementRepository, times(1)).save(any(AnnouncementEntity.class));
    }

    @Test
    void testGetActiveAnnouncements() {
        LocalDateTime now = LocalDateTime.now();
        
        when(announcementRepository.findByActiveTrueAndPublishTimeBeforeAndExpireTimeAfter(
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testAnnouncement));
        
        List<AnnouncementEntity> announcements = announcementService.getActiveAnnouncements();
        
        assertEquals(1, announcements.size());
        assertEquals("测试公告", announcements.get(0).getTitle());
        
        verify(announcementRepository, times(1))
                .findByActiveTrueAndPublishTimeBeforeAndExpireTimeAfter(
                        any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testDeactivateAnnouncement() {
        when(announcementRepository.findById(1L)).thenReturn(Optional.of(testAnnouncement));
        when(announcementRepository.save(any(AnnouncementEntity.class))).thenReturn(testAnnouncement);
        
        announcementService.deactivateAnnouncement(1L);
        
        assertFalse(testAnnouncement.getActive());
        
        verify(announcementRepository, times(1)).findById(1L);
        verify(announcementRepository, times(1)).save(testAnnouncement);
    }

    @Test
    void testGetLatestAnnouncementContent_WithAnnouncements() {
        List<AnnouncementEntity> announcements = new ArrayList<>();
        announcements.add(testAnnouncement);
        
        AnnouncementEntity olderAnnouncement = new AnnouncementEntity();
        olderAnnouncement.setId(2L);
        olderAnnouncement.setTitle("旧公告");
        olderAnnouncement.setContent("这是一条较旧的公告");
        olderAnnouncement.setPublishTime(LocalDateTime.now().minusDays(7));
        olderAnnouncement.setExpireTime(LocalDateTime.now().plusDays(1));
        olderAnnouncement.setActive(true);
        
        announcements.add(olderAnnouncement);
        
        when(announcementService.getActiveAnnouncements()).thenReturn(announcements);
        
        String content = announcementService.getLatestAnnouncementContent();
        
        assertEquals("这是一条测试公告内容", content);
    }

    @Test
    void testGetLatestAnnouncementContent_NoAnnouncements() {
        when(announcementService.getActiveAnnouncements()).thenReturn(new ArrayList<>());
        
        String content = announcementService.getLatestAnnouncementContent();
        
        assertEquals("欢迎使用场馆，请遵守相关规定。", content);
    }
}
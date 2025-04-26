package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VenueControllerTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueController venueController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private VenueEntity createTestVenue(Long id, String name, String type, String description, BigDecimal price, boolean isAvailable) {
        VenueEntity venue = new VenueEntity();
        venue.setId(id);
        venue.setName(name);
        venue.setType(type);
        venue.setDescription(description);
        venue.setPrice(price);
        venue.setIsAvailable(isAvailable);
        venue.setStatus("AVAILABLE");
        return venue;
    }

    @Test
    void testGetAllVenues() {
        // 准备测试数据
        List<VenueEntity> venues = Arrays.asList(
                createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true),
                createTestVenue(2L, "足球场1", "football", "标准足球场", new BigDecimal("200"), true)
        );
        Page<VenueEntity> page = new Page<>(1, 10, venues.size());
        page.setRecords(venues);

        // 模拟repository行为
        when(venueRepository.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试
        ResponseEntity<Page<VenueEntity>> response = venueController.getAllVenues(1, 10);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(venues.size(), response.getBody().getRecords().size());
    }

    @Test
    void testGetVenueById() {
        // 准备测试数据
        VenueEntity venue = createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true);

        // 模拟repository行为
        when(venueRepository.selectById(1L)).thenReturn(venue);

        // 执行测试
        ResponseEntity<VenueEntity> response = venueController.getVenueById(1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(venue, response.getBody());
    }

    @Test
    void testGetVenuesByType() {
        // 准备测试数据
        List<VenueEntity> venues = Arrays.asList(
                createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true),
                createTestVenue(2L, "篮球场2", "basketball", "标准篮球场", new BigDecimal("100"), true)
        );
        Page<VenueEntity> page = new Page<>(1, 10, venues.size());
        page.setRecords(venues);

        // 模拟repository行为
        when(venueRepository.findByTypeWithPage(eq("basketball"), any(Page.class))).thenReturn(page);

        // 执行测试
        ResponseEntity<Page<VenueEntity>> response = venueController.getVenuesByType("basketball", 1, 10);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(venues.size(), response.getBody().getRecords().size());
    }

    @Test
    void testGetAvailableVenues() {
        // 准备测试数据
        List<VenueEntity> venues = Arrays.asList(
                createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true),
                createTestVenue(2L, "足球场1", "football", "标准足球场", new BigDecimal("200"), true)
        );

        // 模拟repository行为
        when(venueRepository.selectList(any())).thenReturn(venues);

        // 执行测试
        ResponseEntity<List<VenueEntity>> response = venueController.getAvailableVenues();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(venues.size(), response.getBody().size());
    }

    @Test
    void testCreateVenue() {
        // 准备测试数据
        VenueEntity venue = createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true);

        // 模拟repository行为
        when(venueRepository.insert(venue)).thenReturn(1);

        // 执行测试
        ResponseEntity<VenueEntity> response = venueController.createVenue(venue);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(venue, response.getBody());
    }

    @Test
    void testUpdateVenue() {
        // 准备测试数据
        VenueEntity existingVenue = createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true);
        VenueEntity updatedVenue = createTestVenue(1L, "篮球场2", "basketball", "标准篮球场", new BigDecimal("150"), true);

        // 模拟repository行为
        when(venueRepository.selectById(1L)).thenReturn(existingVenue);
        when(venueRepository.updateById(updatedVenue)).thenReturn(1);

        // 执行测试
        ResponseEntity<VenueEntity> response = venueController.updateVenue(1L, updatedVenue);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedVenue, response.getBody());
    }

    @Test
    void testDeleteVenue() {
        // 准备测试数据
        VenueEntity venue = createTestVenue(1L, "篮球场1", "basketball", "标准篮球场", new BigDecimal("100"), true);

        // 模拟repository行为
        when(venueRepository.selectById(1L)).thenReturn(venue);
        when(venueRepository.deleteById(1L)).thenReturn(1);

        // 执行测试
        ResponseEntity<Void> response = venueController.deleteVenue(1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(venueRepository, times(1)).deleteById(1L);
    }
}
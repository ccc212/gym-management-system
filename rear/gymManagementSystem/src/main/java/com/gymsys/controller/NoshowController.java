package com.gymsys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.dto.NoshowDTO;
import com.gymsys.entity.noshow.NoshowEntity;
import com.gymsys.service.NoshowService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/noshows")
public class NoshowController {

    @Resource
    private NoshowService noshowService;

    @GetMapping
    public Page<NoshowEntity> getNoshows(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return noshowService.getNoshows(page, size, venueType, venueId, startDate, endDate);
    }

    @PostMapping("/{id}/handle")
    public NoshowEntity handleNoshow(@PathVariable Long id, @RequestBody NoshowDTO dto) {
        return noshowService.handleNoshow(id, dto);
    }

    @GetMapping("/{id}")
    public NoshowEntity getNoshowDetail(@PathVariable Long id) {
        return noshowService.getNoshowDetail(id);
    }

    // 测试接口：创建测试失约记录
    @PostMapping("/test/create")
    public List<NoshowEntity> createTestNoshows() {
        List<NoshowEntity> testNoshows = new ArrayList<>();
        
        // 创建一些测试数据
        String[] venueTypes = {"篮球场", "足球场", "羽毛球场", "网球场", "游泳池", "乒乓球室"};
        String[] venueNames = {"A", "B", "C"};
        String[] timeRanges = {"08:00 - 10:00", "10:00 - 12:00", "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00"};
        String[] userNames = {"张三", "李四", "王五", "赵六", "钱七"};
        String[] userPhones = {"13800138001", "13800138002", "13800138003", "13800138004", "13800138005"};
        
        LocalDate today = LocalDate.now();
        
        // 创建10条测试记录
        for (int i = 0; i < 10; i++) {
            NoshowEntity noshow = new NoshowEntity();
            noshow.setReservationId(10000L + i);
            noshow.setUserId(1000L + i);
            noshow.setUserName(userNames[i % userNames.length]);
            noshow.setUserPhone(userPhones[i % userPhones.length]);
            noshow.setVenueId(100L + i);
            noshow.setVenueName(venueTypes[i % venueTypes.length] + venueNames[i % venueNames.length]);
            noshow.setVenueType(venueTypes[i % venueTypes.length]);
            noshow.setDate(today.minusDays(i % 3)); // 最近3天的日期
            noshow.setTimeRange(timeRanges[i % timeRanges.length]);
            noshow.setNumberOfPeople((i % 5 + 1) * 2); // 2-10人
            noshow.setCost(new java.math.BigDecimal((i % 5 + 1) * 50)); // 50-250元
            noshow.setStatus("PENDING");
            noshow.setCreatedAt(LocalDateTime.now().minusHours(i));
            noshow.setUpdatedAt(LocalDateTime.now().minusHours(i));
            
            noshowService.save(noshow);
            testNoshows.add(noshow);
        }
        
        return testNoshows;
    }
} 
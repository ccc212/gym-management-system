package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.UsageEntity;
import com.gymsys.service.venue.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/usages")
@RequiredArgsConstructor
public class UsageController {
    
    private final UsageService usageService;
    
    /**
     * 开始使用场馆
     */
    @PostMapping
    public ResponseEntity<UsageEntity> startUsage(@RequestBody UsageEntity usage) {
        return ResponseEntity.ok(usageService.startUsage(usage));
    }
    
    /**
     * 结束使用场馆
     */
    @PutMapping("/{id}/end")
    public ResponseEntity<UsageEntity> endUsage(@PathVariable Long id) {
        return ResponseEntity.ok(usageService.endUsage(id));
    }
    
    /**
     * 支付使用费用
     */
    @PutMapping("/{id}/pay")
    public ResponseEntity<UsageEntity> payUsage(@PathVariable Long id) {
        return ResponseEntity.ok(usageService.payUsage(id));
    }
    
    /**
     * 获取场馆的使用记录
     */
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<Page<UsageEntity>> getVenueUsages(
            @PathVariable Long venueId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(usageService.getVenueUsages(venueId, page, size));
    }
    
    /**
     * 获取用户的使用记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<UsageEntity>> getUserUsages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(usageService.getUserUsages(userId, page, size));
    }
    
    /**
     * 获取指定时间范围内的使用记录
     */
    @GetMapping("/time-range")
    public ResponseEntity<Page<UsageEntity>> getUsagesByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(usageService.getUsagesByTimeRange(startTime, endTime, page, size));
    }
    
    /**
     * 获取进行中的使用记录
     */
    @GetMapping("/active")
    public ResponseEntity<Page<UsageEntity>> getActiveUsages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(usageService.getActiveUsages(page, size));
    }
    
    /**
     * 获取指定卡号的使用记录
     */
    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<Page<UsageEntity>> getUsagesByCardNumber(
            @PathVariable String cardNumber,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(usageService.getUsagesByCardNumber(cardNumber, page, size));
    }
}
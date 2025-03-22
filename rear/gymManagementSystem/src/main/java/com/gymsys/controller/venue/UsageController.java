package com.gymsys.controller.venue;

import com.gymsys.entity.venue.UsageEntity;
import com.gymsys.service.venue.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usages")
public class UsageController {
    
    private final UsageService usageService;
    
    /**
     * 场地使用开始
     */
    @PostMapping("/start")
    public ResponseEntity<UsageEntity> startVenueUsage(
            @RequestParam Long venueId,
            @RequestParam String cardNumber,
            @RequestParam(required = false) Long reservationId) {
        
        UsageEntity usage = usageService.startVenueUsage(venueId, cardNumber, reservationId);
        return new ResponseEntity<>(usage, HttpStatus.CREATED);
    }
    
    /**
     * 场地使用结束
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<UsageEntity> endVenueUsage(@PathVariable Long id) {
        UsageEntity usage = usageService.endVenueUsage(id);
        return ResponseEntity.ok(usage);
    }
    
    /**
     * 一卡通付费
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<UsageEntity> payUsage(@PathVariable Long id) {
        UsageEntity usage = usageService.payUsage(id);
        return ResponseEntity.ok(usage);
    }
    
    /**
     * 场地收费标准查询
     */
    @GetMapping("/price/{venueId}")
    public ResponseEntity<Map<String, BigDecimal>> getVenuePrice(@PathVariable Long venueId) {
        BigDecimal price = usageService.getVenuePrice(venueId);
        return ResponseEntity.ok(Map.of("pricePerHour", price));
    }
    
    /**
     * 获取当前使用记录
     */
    @GetMapping("/active")
    public ResponseEntity<List<UsageEntity>> getActiveUsages() {
        List<UsageEntity> usages = usageService.getActiveUsages();
        return ResponseEntity.ok(usages);
    }
    
    /**
     * 获取用户未付费记录
     */
    @GetMapping("/unpaid/{cardNumber}")
    public ResponseEntity<List<UsageEntity>> getUnpaidUsages(@PathVariable String cardNumber) {
        List<UsageEntity> usages = usageService.getUnpaidUsages(cardNumber);
        return ResponseEntity.ok(usages);
    }
}
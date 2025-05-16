package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.common.Result;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.entity.venue.TimeSlot;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.dto.ReservationRequest;
import com.gymsys.repository.venue.VenueRepository;
import com.gymsys.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/venues", "/venues"})
public class VenueController {
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("/types")
    public Result<List<String>> getVenueTypes() {
        List<String> types = Arrays.asList(
            "篮球场",
            "足球场",
            "羽毛球场",
            "网球场",
            "游泳池",
            "乒乓球室"
        );
        return Result.success(types);
    }
    
    @GetMapping
    public Result<Page<VenueEntity>> getAllVenues(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {
        Page<VenueEntity> venuePage = new Page<>(page, size);
        LambdaQueryWrapper<VenueEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (type != null && !type.isEmpty()) {
            // 将中文类型转换为英文类型
            String englishType = convertToEnglishType(type);
            wrapper.eq(VenueEntity::getType, englishType);
        }
        
        Page<VenueEntity> result = venueRepository.selectPage(venuePage, wrapper);
        
        // 转换场地类型为中文
        result.getRecords().forEach(venue -> {
            if (venue.getType() != null) {
                venue.setType(convertToChineseType(venue.getType()));
            }
        });
        
        return Result.success(result);
    }
    
    private String convertToEnglishType(String chineseType) {
        switch (chineseType) {
            case "篮球场":
                return "basketball";
            case "足球场":
                return "football";
            case "羽毛球场":
                return "badminton";
            case "网球场":
                return "tennis";
            case "游泳池":
                return "swimming";
            case "乒乓球室":
                return "table_tennis";
            default:
                return chineseType;
        }
    }
    
    private String convertToChineseType(String englishType) {
        switch (englishType) {
            case "basketball":
                return "篮球场";
            case "football":
                return "足球场";
            case "badminton":
                return "羽毛球场";
            case "tennis":
                return "网球场";
            case "swimming":
                return "游泳池";
            case "table_tennis":
                return "乒乓球室";
            default:
                return englishType;
        }
    }
    
    @GetMapping("/{id}")
    public Result<VenueEntity> getVenueById(@PathVariable Long id) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            return Result.error("场地不存在");
        }
        return Result.success(venue);
    }
    
    @GetMapping("/type/{type}")
    public Result<List<VenueEntity>> getVenuesByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VenueEntity> venuePage = new Page<>(page, size);
        List<VenueEntity> venues = venueRepository.findByTypeWithPage(type, venuePage).getRecords();
        return Result.success(venues);
    }
    
    @GetMapping("/available")
    public Result<List<VenueEntity>> getAvailableVenues() {
        List<VenueEntity> venues = venueRepository.selectList(null);
        return Result.success(venues);
    }
    
    @GetMapping("/{venueId}/time-slots")
    public Result<List<TimeSlot>> getTimeSlots(
            @PathVariable Long venueId,
            @RequestParam String date) {
        String formattedDate = date.replace("年", "-")
                                 .replace("月", "-")
                                 .replace("日", "");
        List<TimeSlot> timeSlots = reservationService.getAvailableTimeSlots(venueId, formattedDate);
        return Result.success(timeSlots);
    }
    
    @PostMapping("/{venueId}/reserve")
    public Result<ReservationEntity> createReservation(
            @PathVariable Long venueId,
            @RequestBody ReservationRequest request) {
        ReservationEntity reservation = reservationService.createReservation(
                venueId,
                request.getUserId(),
                request.getStartTime().toString(),
                request.getEndTime().toString(),
                request.getNumberOfPeople(),
                request.getRemarks()
        );
        return Result.success(reservation);
    }
    
    @PostMapping
    public Result<VenueEntity> createVenue(@RequestBody VenueEntity venue) {
        venueRepository.insert(venue);
        return Result.success(venue);
    }
    
    @PutMapping("/{id}")
    public Result<VenueEntity> updateVenue(@PathVariable Long id, @RequestBody VenueEntity venue) {
        VenueEntity existingVenue = venueRepository.selectById(id);
        if (existingVenue == null) {
            return Result.error("场地不存在");
        }
        venue.setId(id);
        venueRepository.updateById(venue);
        return Result.success(venue);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteVenue(@PathVariable Long id) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            return Result.error("场地不存在");
        }
        venueRepository.deleteById(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/status")
    public Result<VenueEntity> updateVenueStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            return Result.error("场地不存在");
        }
        venue.setStatus(status);
        venue.setUpdatedAt(LocalDateTime.now());
        venueRepository.updateById(venue);
        return Result.success(venue);
    }
}
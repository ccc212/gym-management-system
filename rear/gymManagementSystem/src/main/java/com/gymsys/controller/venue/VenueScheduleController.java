package com.gymsys.controller.venue;

import com.gymsys.common.Result;
import com.gymsys.dto.VenueScheduleDTO;
import com.gymsys.service.venue.VenueScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/venue-schedules")
public class VenueScheduleController {
    
    @Autowired
    private VenueScheduleService venueScheduleService;
    
    @GetMapping("/weekly")
    public Result<List<VenueScheduleDTO>> getWeeklySchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) Long venueId) {
        try {
            List<VenueScheduleDTO> schedules = venueScheduleService.getWeeklySchedule(startDate, endDate, venueType, venueId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("获取场地安排失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/types")
    public Result<List<String>> getVenueTypes() {
        try {
            List<String> types = venueScheduleService.getVenueTypes();
            return Result.success(types);
        } catch (Exception e) {
            return Result.error("获取场地类型失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/venues")
    public Result<List<VenueScheduleDTO>> getVenuesByType(
            @RequestParam(required = false) String type) {
        try {
            List<VenueScheduleDTO> venues = venueScheduleService.getVenuesByType(type);
            return Result.success(venues);
        } catch (Exception e) {
            return Result.error("获取场地列表失败：" + e.getMessage());
        }
    }
} 
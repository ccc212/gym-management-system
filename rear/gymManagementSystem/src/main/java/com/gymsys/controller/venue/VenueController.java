package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.entity.venue.TimeSlot;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.dto.ReservationRequest;
import com.gymsys.repository.venue.VenueRepository;
import com.gymsys.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("/types")
    public ResponseEntity<List<String>> getVenueTypes() {
        return ResponseEntity.ok(Arrays.asList(
            "basketball",
            "football",
            "badminton",
            "tennis",
            "swimming",
            "table_tennis"
        ));
    }
    
    @GetMapping
    public ResponseEntity<Page<VenueEntity>> getAllVenues(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {
        Page<VenueEntity> venuePage = new Page<>(page, size);
        if (type != null && !type.isEmpty()) {
            return ResponseEntity.ok(venueRepository.findByTypeWithPage(type, venuePage));
        }
        return ResponseEntity.ok(venueRepository.selectPage(venuePage, null));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VenueEntity> getVenueById(@PathVariable Long id) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(venue);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<VenueEntity>> getVenuesByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VenueEntity> venuePage = new Page<>(page, size);
        return ResponseEntity.ok(venueRepository.findByTypeWithPage(type, venuePage));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<VenueEntity>> getAvailableVenues() {
        return ResponseEntity.ok(venueRepository.selectList(null));
    }
    
    @GetMapping("/{venueId}/time-slots")
    public ResponseEntity<List<TimeSlot>> getTimeSlots(
            @PathVariable Long venueId,
            @RequestParam String date) {
        String formattedDate = date.replace("年", "-")
                                 .replace("月", "-")
                                 .replace("日", "");
        List<TimeSlot> timeSlots = reservationService.getAvailableTimeSlots(venueId, formattedDate);
        return ResponseEntity.ok(timeSlots);
    }
    
    @PostMapping("/{venueId}/reserve")
    public ResponseEntity<ReservationEntity> createReservation(
            @PathVariable Long venueId,
            @RequestBody ReservationRequest request) {
        String startTimeStr;
        String endTimeStr;
        if (request.getStartTime() instanceof LocalDateTime) {
            startTimeStr = ((LocalDateTime)request.getStartTime()).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            startTimeStr = String.valueOf(request.getStartTime());
        }
        if (request.getEndTime() instanceof LocalDateTime) {
            endTimeStr = ((LocalDateTime)request.getEndTime()).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            endTimeStr = String.valueOf(request.getEndTime());
        }
        ReservationEntity reservation = reservationService.createReservation(
                venueId,
                request.getUserId(),
                startTimeStr,
                endTimeStr,
                request.getNumberOfPeople(),
                request.getRemarks()
        );
        return ResponseEntity.ok(reservation);
    }
    
    @PostMapping
    public ResponseEntity<VenueEntity> createVenue(@RequestBody VenueEntity venue) {
        venueRepository.insert(venue);
        return ResponseEntity.ok(venue);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VenueEntity> updateVenue(@PathVariable Long id, @RequestBody VenueEntity venue) {
        VenueEntity existingVenue = venueRepository.selectById(id);
        if (existingVenue == null) {
            return ResponseEntity.notFound().build();
        }
        venue.setId(id);
        venueRepository.updateById(venue);
        return ResponseEntity.ok(venue);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            return ResponseEntity.notFound().build();
        }
        venueRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<VenueEntity> updateVenueStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue == null) {
            return ResponseEntity.notFound().build();
        }
        venue.setStatus(status);
        venue.setUpdatedAt(LocalDateTime.now());
        venueRepository.updateById(venue);
        return ResponseEntity.ok(venue);
    }
}
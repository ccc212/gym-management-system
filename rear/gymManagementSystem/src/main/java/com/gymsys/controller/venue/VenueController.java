package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.entity.venue.TimeSlot;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.dto.ReservationRequest;
import com.gymsys.repository.venue.VenueRepository;
import com.gymsys.repository.venue.TimeSlotRepository;
import com.gymsys.service.reservation.ReservationService;
import com.gymsys.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    @Autowired
    private ReservationService reservationService;
    
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {
        List<TimeSlot> timeSlots = reservationService.getAvailableTimeSlots(venueId, date);
        return ResponseEntity.ok(timeSlots);
    }
    
    @PostMapping("/{venueId}/reserve")
    public ResponseEntity<ReservationEntity> createReservation(
            @PathVariable Long venueId,
            @RequestBody ReservationRequest request) {
        ReservationEntity reservation = reservationService.createReservation(
                venueId,
                request.getUserId(),
                request.getCardNumber(),
                request.getStartTime(),
                request.getEndTime(),
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

    @GetMapping("/{id}/time-slots")
    public ResponseEntity<List<TimeSlot>> getVenueTimeSlots(
            @PathVariable Long id,
            @RequestParam String date) {
        List<TimeSlot> timeSlots = timeSlotRepository.findByVenueIdAndDate(id, date);
        return ResponseEntity.ok(timeSlots);
    }
}
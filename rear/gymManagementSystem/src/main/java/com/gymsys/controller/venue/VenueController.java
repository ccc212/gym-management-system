package com.gymsys.controller.venue;

import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.service.venue.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueController {
    
    private final VenueService venueService;
    
    /**
     * 添加场地
     */
    @PostMapping
    public ResponseEntity<VenueEntity> addVenue(@RequestBody VenueEntity venue) {
        VenueEntity savedVenue = venueService.addVenue(venue);
        return new ResponseEntity<>(savedVenue, HttpStatus.CREATED);
    }
    
    /**
     * 删除场地
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeVenue(@PathVariable Long id) {
        venueService.removeVenue(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 更新场地
     */
    @PutMapping("/{id}")
    public ResponseEntity<VenueEntity> updateVenue(@PathVariable Long id, @RequestBody VenueEntity venue) {
        venue.setId(Long.valueOf(id.toString()));
        VenueEntity updatedVenue = venueService.updateVenue(venue);
        return ResponseEntity.ok(updatedVenue);
    }
    
    /**
     * 获取场地详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<VenueEntity> getVenue(@PathVariable Long id) {
        return venueService.findVenueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取所有场地
     */
    @GetMapping
    public ResponseEntity<List<VenueEntity>> getAllVenues() {
        List<VenueEntity> venues = venueService.findAllVenues();
        return ResponseEntity.ok(venues);
    }
    
    /**
     * 获取可用场地
     */
    @GetMapping("/available")
    public ResponseEntity<List<VenueEntity>> getAvailableVenues() {
        List<VenueEntity> venues = venueService.findAvailableVenues();
        return ResponseEntity.ok(venues);
    }
    
    /**
     * 获取特定类型的场地
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<VenueEntity>> getVenuesByType(@PathVariable String type) {
        List<VenueEntity> venues = venueService.findVenuesByType(type);
        return ResponseEntity.ok(venues);
    }
}
package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    
    @Autowired
    private VenueRepository venueRepository;
    
    @GetMapping
    public ResponseEntity<Page<VenueEntity>> getAllVenues(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VenueEntity> venuePage = venueRepository.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<VenueEntity>()
        );
        return ResponseEntity.ok(venuePage);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VenueEntity> getVenueById(@PathVariable Long id) {
        VenueEntity venue = venueRepository.selectById(id);
        if (venue != null) {
            return ResponseEntity.ok(venue);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<VenueEntity>> getVenuesByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<VenueEntity> venuePage = venueRepository.findByTypeWithPage(type, new Page<>(page, size));
        return ResponseEntity.ok(venuePage);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<VenueEntity>> getAvailableVenues() {
        List<VenueEntity> venues = venueRepository.selectList(
                new LambdaQueryWrapper<VenueEntity>()
                        .eq(VenueEntity::getStatus, "AVAILABLE")
        );
        return ResponseEntity.ok(venues);
    }
    
    @PostMapping
    public ResponseEntity<VenueEntity> createVenue(@RequestBody VenueEntity venue) {
        venueRepository.insert(venue);
        return ResponseEntity.ok(venue);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VenueEntity> updateVenue(@PathVariable Long id, @RequestBody VenueEntity venue) {
        venue.setId(id);
        venueRepository.updateById(venue);
        return ResponseEntity.ok(venue);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
package com.gymsys.controller.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.repository.venue.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @GetMapping
    public ResponseEntity<Page<ReservationEntity>> getAllReservations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationRepository.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<ReservationEntity>()
        );
        return ResponseEntity.ok(reservationPage);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<Page<ReservationEntity>> getReservationsByVenue(
            @PathVariable Long venueId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationRepository.findByVenueId(venueId, new Page<>(page, size));
        return ResponseEntity.ok(reservationPage);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReservationEntity>> getReservationsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationRepository.findByUserId(userId, new Page<>(page, size));
        return ResponseEntity.ok(reservationPage);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ReservationEntity>> getReservationsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationRepository.findByStatus(status, new Page<>(page, size));
        return ResponseEntity.ok(reservationPage);
    }
    
    @PostMapping
    public ResponseEntity<ReservationEntity> createReservation(@RequestBody ReservationEntity reservation) {
        // 检查时间冲突
        List<ReservationEntity> overlappingReservations = reservationRepository.findOverlappingReservations(
                reservation.getVenueId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
        
        if (!overlappingReservations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        reservationRepository.insert(reservation);
        return ResponseEntity.ok(reservation);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationEntity> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ReservationEntity reservation = reservationRepository.selectById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        
        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.updateById(reservation);
        
        return ResponseEntity.ok(reservation);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
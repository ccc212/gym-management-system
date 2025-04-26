package com.gymsys.controller.reservation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.repository.reservation.ReservationRepository;
import com.gymsys.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping
    public ResponseEntity<Page<ReservationEntity>> getAllReservations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationService.getReservationsByUserId(null, page, size);
        return ResponseEntity.ok(reservationPage);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.getReservationById(id);
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
        Page<ReservationEntity> reservationPage = reservationService.getVenueReservations(venueId, page, size);
        return ResponseEntity.ok(reservationPage);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReservationEntity>> getReservationsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationService.getUserReservations(userId, page, size);
        return ResponseEntity.ok(reservationPage);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ReservationEntity>> getReservationsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationService.getReservationsByStatus(status, page, size);
        return ResponseEntity.ok(reservationPage);
    }
    
    @PostMapping
    public ResponseEntity<ReservationEntity> createReservation(@RequestBody ReservationEntity reservation) {
        // 检查时间冲突
        List<ReservationEntity> overlappingReservations = reservationService.getVenueWeeklyReservations(
                reservation.getVenueId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
        
        if (!overlappingReservations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        ReservationEntity createdReservation = reservationService.createReservation(
                reservation.getVenueId(),
                reservation.getUserId(),
                reservation.getCardNumber(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getNumberOfPeople(),
                reservation.getRemarks()
        );
        
        return ResponseEntity.ok(createdReservation);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationEntity> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                reservationService.confirmReservation(id);
                break;
            case "CANCELLED":
                reservationService.cancelReservation(id);
                break;
            case "COMPLETED":
                reservationService.completeReservation(id);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
} 
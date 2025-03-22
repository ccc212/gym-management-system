package com.gymsys.controller.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.service.venue.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    /**
     * 场地预约
     */
    @PostMapping
    public ResponseEntity<ReservationEntity> reserveVenue(
            @RequestParam Long venueId,
            @RequestParam String cardNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        ReservationEntity reservation = reservationService.reserveVenue(venueId, cardNumber, startTime, endTime);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }
    
    /**
     * 场地预约退订
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @RequestParam String cardNumber) {
        
        reservationService.cancelReservation(id, cardNumber);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 场地预约修改
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationEntity> modifyReservation(
            @PathVariable Long id,
            @RequestParam String cardNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        ReservationEntity reservation = reservationService.modifyReservation(id, cardNumber, startTime, endTime);
        return ResponseEntity.ok(reservation);
    }
    
    /**
     * 场地预约失约处理
     */
    @PostMapping("/{id}/no-show")
    public ResponseEntity<Void> handleNoShow(@PathVariable Long id) {
        reservationService.handleNoShow(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 校队预留（使用）场地
     */
    @PostMapping("/team")
    public ResponseEntity<ReservationEntity> reserveVenueForTeam(
            @RequestParam Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        ReservationEntity reservation = reservationService.reserveVenueForTeam(venueId, startTime, endTime);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }
    
    /**
     * 上课使用场地
     */
    @PostMapping("/class")
    public ResponseEntity<ReservationEntity> reserveVenueForClass(
            @RequestParam Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        ReservationEntity reservation = reservationService.reserveVenueForClass(venueId, startTime, endTime);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }
    
    /**
     * 场地一周信息查询
     */
    @GetMapping("/venue/{venueId}/weekly")
    public ResponseEntity<List<ReservationEntity>> getVenueWeeklyReservations(@PathVariable Long venueId) {
        List<ReservationEntity> reservations = reservationService.getVenueWeeklyReservations(venueId);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * 获取用户所有预约
     */
    @GetMapping("/user/{cardNumber}")
    public ResponseEntity<List<ReservationEntity>> getUserReservations(@PathVariable String cardNumber) {
        List<ReservationEntity> reservations = reservationService.findUserReservations(cardNumber);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservation(@PathVariable Long id) {
        return reservationService.findReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
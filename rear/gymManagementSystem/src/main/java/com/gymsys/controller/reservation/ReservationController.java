package com.gymsys.controller.reservation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.repository.reservation.ReservationRepository;
import com.gymsys.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping
    public ResponseEntity<Page<ReservationEntity>> getAllReservations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReservationEntity> reservationPage = reservationService.page(new Page<>(page, size), new LambdaQueryWrapper<>());
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
    public ResponseEntity<Page<ReservationEntity>> getUserReservations(
            @PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<ReservationEntity> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
        
        // 添加用户ID条件
        wrapper.eq(ReservationEntity::getUserId, userId);
        
        // 添加状态条件
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ReservationEntity::getStatus, status);
        }
        
        // 添加日期范围条件
        if (startDate != null && endDate != null) {
            wrapper.ge(ReservationEntity::getDate, startDate)
                  .le(ReservationEntity::getDate, endDate);
        }
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(ReservationEntity::getCreatedTime);
        
        return ResponseEntity.ok(reservationService.page(pageRequest, wrapper));
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
        reservationService.getVenueWeeklyReservations(
                reservation.getVenueId(),
                null,
                null
        );
        
        ReservationEntity createdReservation = reservationService.createReservation(
                reservation.getVenueId(),
                reservation.getUserId(),
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
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        
        ReservationEntity reservation = reservationService.getById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 更新预约状态为已取消
        reservation.setStatus("CANCELED");
        reservation.setCancelReason(reason);
        reservationService.updateById(reservation);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "预约已成功取消");
        response.put("reservation", reservation);
        
        return ResponseEntity.ok(response);
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
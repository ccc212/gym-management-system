package com.gymsys.controller.reservation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.entity.venue.UserEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.entity.system.User;
import com.gymsys.entity.system.SysUser;
import com.gymsys.mapper.system.UserMapper;
import com.gymsys.mapper.system.SysUserMapper;
import com.gymsys.repository.venue.VenueRepository;
import com.gymsys.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/reservations", "/reservations", "/user/reservations"})
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @GetMapping
    public ResponseEntity<Page<ReservationEntity>> getAllReservations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            System.out.println("获取预约列表 - 查询参数: " +
                "page=" + page + ", size=" + size +
                ", venueType=" + venueType + ", status=" + status +
                ", startDate=" + startDate + ", endDate=" + endDate);
            
            Page<ReservationEntity> pageRequest = new Page<>(page, size);
            LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
            
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
            
            // 查询预约
            Page<ReservationEntity> result = reservationService.getBaseMapper().selectPage(pageRequest, wrapper);
            System.out.println("查询到 " + result.getTotal() + " 条预约记录");
            
            // 填充场馆信息和用户信息
            for (ReservationEntity reservation : result.getRecords()) {
                // 填充场馆信息
                VenueEntity venue = venueRepository.selectById(reservation.getVenueId());
                if (venue != null) {
                    // 如果指定了场地类型，进行过滤
                    if (venueType != null && !venueType.isEmpty() && !venueType.equals(venue.getType())) {
                        continue;
                    }
                    reservation.setVenueInfo(venue);
                }
                
                // 填充用户信息
                User user = userMapper.selectById(reservation.getUserId());
                if (user != null) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(user.getId().longValue());
                    userEntity.setUsername(user.getName());
                    reservation.setUserInfo(userEntity);
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("获取预约列表失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
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
        
        try {
            System.out.println("获取用户预约列表 - 用户ID: " + userId);
            System.out.println("查询参数 - 状态: " + status + ", 开始日期: " + startDate + ", 结束日期: " + endDate);
            
            Page<ReservationEntity> pageRequest = new Page<>(page, size);
            LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
            
            // 添加用户ID条件
            wrapper.eq(ReservationEntity::getUserId, userId);
            
            // 添加状态条件
            if (status != null && !status.isEmpty()) {
                if (status.contains(",")) {
                    // 处理多个状态的情况
                    List<String> statusList = Arrays.asList(status.split(","));
                    wrapper.in(ReservationEntity::getStatus, statusList);
                } else {
                    wrapper.eq(ReservationEntity::getStatus, status);
                }
            }
            
            // 添加日期范围条件
            if (startDate != null && endDate != null) {
                wrapper.ge(ReservationEntity::getDate, startDate)
                      .le(ReservationEntity::getDate, endDate);
            }
            
            // 按创建时间倒序排序
            wrapper.orderByDesc(ReservationEntity::getCreatedTime);
            
            // 查询预约
            Page<ReservationEntity> result = reservationService.getBaseMapper().selectPage(pageRequest, wrapper);
            System.out.println("查询到 " + result.getTotal() + " 条预约记录");
            
            // 填充场馆信息
            for (ReservationEntity reservation : result.getRecords()) {
                VenueEntity venue = venueRepository.selectById(reservation.getVenueId());
                if (venue != null) {
                    reservation.setVenueInfo(venue);
                }
                
                // 填充用户信息
                User user = userMapper.selectById(reservation.getUserId());
                if (user != null) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(user.getId().longValue());
                    userEntity.setUsername(user.getName());
                    reservation.setUserInfo(userEntity);
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("获取用户预约列表失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
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
    
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayReservations() {
        try {
            // 获取当前用户ID（这里需要根据你的认证系统来实现）
            Long currentUserId = 1L; // 测试用，实际应该从认证系统获取
            
            // 获取今天的日期
            LocalDate today = LocalDate.now();
            System.out.println("查询今日预约，日期：" + today);
            
            // 构建查询条件
            LambdaQueryWrapper<ReservationEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ReservationEntity::getUserId, currentUserId)
                  .eq(ReservationEntity::getDate, today)
                  .in(ReservationEntity::getStatus, Arrays.asList("CONFIRMED", "IN_USE", "PENDING"))
                  .orderByAsc(ReservationEntity::getStartTime);
            
            // 查询预约
            List<ReservationEntity> reservations = reservationService.list(wrapper);
            System.out.println("查询到的预约数量：" + reservations.size());
            
            // 填充场馆信息
            for (ReservationEntity reservation : reservations) {
                VenueEntity venue = venueRepository.selectById(reservation.getVenueId());
                reservation.setVenueInfo(venue);
            }
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "success");
            response.put("data", reservations);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取今日预约失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取当前登录用户的ID
     * 这里需要根据实际的认证系统来实现
     */
    private Long getCurrentUserId() {
        // TODO: 从认证系统中获取当前用户ID
        // 临时返回测试用户ID
        return 1L;
    }
    
    @GetMapping("/{id}/usage")
    public ResponseEntity<Map<String, Object>> getUsageDetail(@PathVariable Long id) {
        try {
            ReservationEntity reservation = reservationService.getReservationById(id);
            if (reservation == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("msg", "预约不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "success");
            response.put("data", reservation);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取使用详情失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/settle")
    public ResponseEntity<Map<String, Object>> settleReservation(@RequestBody Map<String, Object> settlementData) {
        try {
            System.out.println("收到结算请求数据: " + settlementData);
            
            // 验证必要字段
            if (!settlementData.containsKey("reservationId") || 
                !settlementData.containsKey("actualStartTime") || 
                !settlementData.containsKey("actualEndTime") || 
                !settlementData.containsKey("duration") || 
                !settlementData.containsKey("actualCost") || 
                !settlementData.containsKey("paymentMethod")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("msg", "缺少必要的结算信息");
                return ResponseEntity.badRequest().body(response);
            }

            Long reservationId = Long.valueOf(settlementData.get("reservationId").toString());
            String actualStartTime = (String) settlementData.get("actualStartTime");
            String actualEndTime = (String) settlementData.get("actualEndTime");
            String duration = (String) settlementData.get("duration");
            BigDecimal actualCost = new BigDecimal(settlementData.get("actualCost").toString());
            String paymentMethod = (String) settlementData.get("paymentMethod");
            
            System.out.println("解析后的结算数据: " + 
                "reservationId=" + reservationId + 
                ", actualStartTime=" + actualStartTime + 
                ", actualEndTime=" + actualEndTime + 
                ", duration=" + duration + 
                ", actualCost=" + actualCost + 
                ", paymentMethod=" + paymentMethod);

            ReservationEntity reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("msg", "预约不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 更新预约信息
            reservation.setActualStartTime(actualStartTime);
            reservation.setActualEndTime(actualEndTime);
            reservation.setDuration(duration);
            reservation.setActualCost(actualCost);
            reservation.setPaymentMethod(paymentMethod);
            reservation.setPaymentTime(LocalDateTime.now());
            reservation.setStatus("COMPLETED");
            reservation.setUpdatedTime(LocalDateTime.now());

            // 保存更新
            boolean success = reservationService.updateById(reservation);
            if (!success) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 500);
                response.put("msg", "更新预约信息失败");
                return ResponseEntity.status(500).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "结算成功");
            response.put("data", reservation);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            System.err.println("结算数据格式错误: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("msg", "结算数据格式错误: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            System.err.println("结算失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "结算失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveReservation(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        
        ReservationEntity reservation = reservationService.getById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 更新预约状态为已确认
        reservation.setStatus("CONFIRMED");
        if (request != null && request.containsKey("comment")) {
            reservation.setRemarks(request.get("comment"));
        }
        reservation.setUpdatedTime(LocalDateTime.now());
        reservationService.updateById(reservation);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "预约已成功批准");
        response.put("reservation", reservation);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectReservation(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        
        ReservationEntity reservation = reservationService.getById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 更新预约状态为已拒绝
        reservation.setStatus("REJECTED");
        if (request != null && request.containsKey("comment")) {
            reservation.setRemarks(request.get("comment"));
        }
        reservation.setUpdatedTime(LocalDateTime.now());
        reservationService.updateById(reservation);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "预约已拒绝");
        response.put("reservation", reservation);
        
        return ResponseEntity.ok(response);
    }
} 
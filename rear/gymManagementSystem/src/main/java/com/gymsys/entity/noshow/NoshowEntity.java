package com.gymsys.entity.noshow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("noshow_records")
public class NoshowEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reservationId;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long venueId;
    private String venueName;
    private String venueType;
    private LocalDate date;
    private String timeRange;
    private Integer numberOfPeople;
    private BigDecimal cost;
    private String status; // PENDING, EXCUSED, PENALIZED
    private BigDecimal penalty;
    private Integer restrictDays;
    private String handler;
    private LocalDateTime handleTime;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
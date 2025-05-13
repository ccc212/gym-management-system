package com.gymsys.entity.venue;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("usage")
public class UsageEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long venueId;
    private Long userId;
    private String cardNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal cost;
    private Boolean paid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
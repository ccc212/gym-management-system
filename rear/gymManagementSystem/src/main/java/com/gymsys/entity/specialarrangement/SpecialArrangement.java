package com.gymsys.entity.specialarrangement;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("special_arrangements")
public class SpecialArrangement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long venueId;
    private String venueName;
    private String venueType;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
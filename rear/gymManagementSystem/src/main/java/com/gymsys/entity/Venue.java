package com.gymsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("venues")
public class Venue {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Integer capacity;
    private String description;
    private Boolean isAvailable;
    private String location;
    private String name;
    private BigDecimal pricePerHour;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
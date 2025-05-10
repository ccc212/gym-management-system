package com.gymsys.entity.venue;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("venues")
public class VenueEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private String description;
    private Boolean isAvailable;
    private String location;
    private Integer capacity;
    private BigDecimal pricePerHour;
    private String status;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
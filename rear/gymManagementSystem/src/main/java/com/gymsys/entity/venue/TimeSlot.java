package com.gymsys.entity.venue;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("venue_time_slots")
public class TimeSlot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long venueId;
    private String startTime;
    private String endTime;
    private String status; // AVAILABLE, BOOKED, SPECIAL
    private String date;
    private BigDecimal price;
} 
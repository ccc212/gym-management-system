package com.gymsys.entity.reservation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gymsys.entity.venue.VenueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("reservations")
public class ReservationEntity {
    
    /**
     * 预约ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 场馆ID
     */
    private Long venueId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 预约日期
     */
    private LocalDate date;
    
    /**
     * 预约开始时间
     */
    private String startTime;
    
    /**
     * 预约结束时间
     */
    private String endTime;
    
    /**
     * 预约人数
     */
    private Integer numberOfPeople;
    
    /**
     * 预约状态（待确认、已确认、已取消、已完成）
     */
    private String status;
    
    /**
     * 预约类型（普通、特殊）
     */
    private String reservationType;
    
    /**
     * 备注
     */
    private String remarks;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 总价格
     */
    private BigDecimal cost;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private VenueEntity venueInfo;
} 
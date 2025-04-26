package com.gymsys.entity.reservation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
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
     * 一卡通号
     */
    private String cardNumber;
    
    /**
     * 预约开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 预约结束时间
     */
    private LocalDateTime endTime;
    
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
    private String remark;
    
    /**
     * 总价格
     */
    private BigDecimal totalPrice;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 
package com.gymsys.entity.venue;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("reservation")
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
     * 卡号
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
     * 预约状态（待确认、已确认、已取消、已完成）
     */
    private String status;
    
    /**
     * 预约类型
     */
    private String reservationType;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.UsageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UsageRepository extends BaseMapper<UsageEntity> {
    
    @Select("SELECT * FROM usage WHERE venue_id = #{venueId}")
    Page<UsageEntity> findByVenueId(@Param("venueId") Long venueId, Page<UsageEntity> page);
    
    @Select("SELECT * FROM usage WHERE user_id = #{userId}")
    Page<UsageEntity> findByUserId(@Param("userId") Long userId, Page<UsageEntity> page);
    
    @Select("SELECT * FROM usage WHERE card_number = #{cardNumber}")
    Page<UsageEntity> findByCardNumber(@Param("cardNumber") String cardNumber, Page<UsageEntity> page);
    
    @Select("SELECT * FROM usage WHERE start_time BETWEEN #{startTime} AND #{endTime}")
    List<UsageEntity> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Select("SELECT * FROM usage WHERE end_time IS NULL")
    List<UsageEntity> findByEndTimeIsNull();
    
    @Select("SELECT * FROM usage WHERE card_number = #{cardNumber} AND paid = #{paid}")
    List<UsageEntity> findByCardNumberAndPaid(@Param("cardNumber") String cardNumber, @Param("paid") boolean paid);
}
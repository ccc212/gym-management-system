package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.ReservationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationRepository extends BaseMapper<ReservationEntity> {
    
    @Select("SELECT * FROM reservation WHERE status = #{status}")
    Page<ReservationEntity> findByStatus(@Param("status") String status, Page<ReservationEntity> page);
    
    @Select("SELECT * FROM reservation WHERE venue_id = #{venueId}")
    Page<ReservationEntity> findByVenueId(@Param("venueId") Long venueId, Page<ReservationEntity> page);
    
    @Select("SELECT * FROM reservation WHERE user_id = #{userId}")
    Page<ReservationEntity> findByUserId(@Param("userId") Long userId, Page<ReservationEntity> page);
    
    @Select("SELECT * FROM reservation WHERE venue_id = #{venueId} AND " +
            "((start_time BETWEEN #{startTime} AND #{endTime}) OR " +
            "(end_time BETWEEN #{startTime} AND #{endTime}) OR " +
            "(start_time <= #{startTime} AND end_time >= #{endTime}))")
    List<ReservationEntity> findOverlappingReservations(
            @Param("venueId") Long venueId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
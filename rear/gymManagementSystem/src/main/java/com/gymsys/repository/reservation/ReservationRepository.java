package com.gymsys.repository.reservation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.reservation.ReservationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationRepository extends BaseMapper<ReservationEntity> {
    
    @Select("SELECT * FROM reservations WHERE status = #{status}")
    Page<ReservationEntity> findByStatus(@Param("status") String status, Page<ReservationEntity> page);

    @Select("SELECT * FROM reservations WHERE venue_id = #{venueId}")
    Page<ReservationEntity> findByVenueId(@Param("venueId") Long venueId, Page<ReservationEntity> page);

    @Select("SELECT * FROM reservations WHERE user_id = #{userId}")
    Page<ReservationEntity> findByUserId(@Param("userId") Long userId, Page<ReservationEntity> page);

    @Select("SELECT * FROM reservations WHERE venue_id = #{venueId} " +
            "AND ((start_time BETWEEN #{startTime} AND #{endTime}) " +
            "OR (end_time BETWEEN #{startTime} AND #{endTime}))")
    List<ReservationEntity> findOverlappingReservations(
            @Param("venueId") Long venueId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM reservations WHERE card_number = #{cardNumber}")
    List<ReservationEntity> findByCardNumber(@Param("cardNumber") String cardNumber);

    @Select("SELECT * FROM reservations WHERE venue_id = #{venueId} AND DATE(start_time) = #{date}")
    List<ReservationEntity> findByVenueIdAndDate(@Param("venueId") Long venueId, @Param("date") String date);
} 
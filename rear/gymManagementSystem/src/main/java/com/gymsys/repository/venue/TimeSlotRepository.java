package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.venue.TimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TimeSlotRepository extends BaseMapper<TimeSlot> {
    @Select("SELECT * FROM venue_time_slots WHERE venue_id = #{venueId} AND date = #{date}")
    List<TimeSlot> findByVenueIdAndDate(@Param("venueId") Long venueId, @Param("date") String date);
} 
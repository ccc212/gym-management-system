package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.venue.TimeSlot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TimeSlotRepository extends BaseMapper<TimeSlot> {
} 
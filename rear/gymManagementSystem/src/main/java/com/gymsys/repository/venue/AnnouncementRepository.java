package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.venue.AnnouncementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AnnouncementRepository extends BaseMapper<AnnouncementEntity> {
    
    @Select("SELECT * FROM announcement WHERE status = #{status}")
    List<AnnouncementEntity> findByStatus(String status);
    
    @Select("SELECT * FROM announcement WHERE publish_time BETWEEN #{startTime} AND #{endTime}")
    List<AnnouncementEntity> findByPublishTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.venue.VenueEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VenueRepository extends BaseMapper<VenueEntity> {
    
    @Select("SELECT * FROM venue WHERE is_available = #{isAvailable}")
    List<VenueEntity> findByIsAvailable(@Param("isAvailable") boolean isAvailable);
    
    @Select("SELECT * FROM venue WHERE status = #{status}")
    Page<VenueEntity> findByStatus(@Param("status") String status, Page<VenueEntity> page);
    
    @Select("SELECT * FROM venue WHERE type = #{type}")
    Page<VenueEntity> findByTypeWithPage(@Param("type") String type, Page<VenueEntity> page);
    
    @Select("SELECT * FROM venue WHERE type = #{type} AND status = #{status}")
    Page<VenueEntity> findByTypeAndStatus(@Param("type") String type, @Param("status") String status, Page<VenueEntity> page);
}
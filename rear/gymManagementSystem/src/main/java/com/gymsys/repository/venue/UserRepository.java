package com.gymsys.repository.venue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.venue.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRepository extends BaseMapper<UserEntity> {
    
    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    UserEntity findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
} 
package com.gymsys.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.system.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {
    //根据用户id查询菜单
    List<Menu> getMenuByUserId(@Param("userId")Integer userId);

    //根据角色id查询菜单
    List<Menu> getMenuByRoleId(@Param("roleId")Integer roleId);
}

package com.gymsys.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.system.MenuRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuRoleMapper extends BaseMapper<MenuRole> {
    //保存角色菜单
    Boolean saveMenuRole(@Param("roleId")  Integer roleId, @Param("menuIds") List<Integer> menuIds);
}

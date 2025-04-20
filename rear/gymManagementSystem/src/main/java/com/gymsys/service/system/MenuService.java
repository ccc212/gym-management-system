package com.gymsys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.system.AssignTreeParm;
import com.gymsys.entity.system.AssignTreeVo;
import com.gymsys.entity.system.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    /**
     * 查询上级菜单
     * @return
     */
    List<Menu> getParent();

    /**
     * 根据用户id查询菜单
     * @param userId
     * @return
     */
    List<Menu> getMenuByUserId(Integer userId);

    /**
     * 根据角色id查询菜单
     * @param roleId
     * @return
     */
    List<Menu> getMenuByRoleId(Integer roleId);



}

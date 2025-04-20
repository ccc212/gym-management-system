package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.*;
import com.gymsys.mapper.system.MenuMapper;
import com.gymsys.service.system.MenuService;
import com.gymsys.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Override
    public List<Menu> getParent() {
        String[] type = {"0", "1"};
        List<String> strings = Arrays.asList(type);
        QueryWrapper<Menu> query = new QueryWrapper<>();
        query.lambda().in(Menu::getType, strings).orderByDesc(Menu::getOrderNum);
        List<Menu> menuList = this.baseMapper.selectList(query);
        //组装顶级树
        Menu menu = new Menu();
        menu.setTitle("顶级菜单");
        menu.setLabel("顶级菜单");
        menu.setParentId(-1);
        menu.setId(0);
        menu.setValue(0);
        menuList.add(menu);
        //组装菜单树
        List<Menu> tree = MakeMenuTree.makeTree(menuList, -1);
        return tree;
    }

    /**
     * 根据用户id获取菜单
     * @param userId
     * @return
     */
    @Override
    public List<Menu> getMenuByUserId(Integer userId) {
        return this.baseMapper.getMenuByUserId(userId);
    }

    /**
     * 根据角色id获取菜单
     * @param roleId
     * @return
     */
    @Override
    public List<Menu> getMenuByRoleId(Integer roleId) {
        return this.baseMapper.getMenuByRoleId(roleId);
    }



}

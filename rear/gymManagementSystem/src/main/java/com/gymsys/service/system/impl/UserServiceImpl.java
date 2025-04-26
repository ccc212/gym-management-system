package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.*;
import com.gymsys.mapper.system.UserMapper;
import com.gymsys.service.system.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private SysUserRoleService  sysUserRoleService;

    @Autowired
    private SysUserDepartService sysUserDepartService;

    @Autowired
    private SysUserSectionService sysUserSectionService;

    @Autowired
    private MenuService menuService;

    /**
     * 新增用户信息
     * @param user
     */
    @Override
    public void saveUser(User user) {
        int i = this.baseMapper.insert(user);
        if(i > 0){
            user.getRoleId();
            user.getDepartId();
            user.getSectionId();
            if(user.getRoleId() != null){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(user.getId());
                sysUserRole.setRoleId(user.getRoleId());
                sysUserRoleService.save(sysUserRole);
            }
            if(user.getDepartId() != null){
                SysUserDepart sysUserDepart = new SysUserDepart();
                sysUserDepart.setUserId(user.getId());
                sysUserDepart.setDepartId(user.getDepartId());
                sysUserDepartService.save(sysUserDepart);
            }
            if(user.getSectionId() != null){
                SysUserSection sysUserSection = new SysUserSection();
                sysUserSection.setUserId(user.getId());
                sysUserSection.setSectionId(user.getSectionId());
                sysUserSectionService.save(sysUserSection);
            }
        }
    }

    @Override
    public void updateUser(User user) {
        int i = this.baseMapper.updateById(user);
        //删除原来的
        QueryWrapper<SysUserRole> query1 = new QueryWrapper<>();
        query1.lambda().eq(SysUserRole::getUserId,user.getId());
        sysUserRoleService.remove(query1);
        QueryWrapper<SysUserDepart> query2 = new QueryWrapper<>();
        query2.lambda().eq(SysUserDepart::getUserId,user.getId());
        sysUserDepartService.remove(query2);
        QueryWrapper<SysUserSection> query3 = new QueryWrapper<>();
        query3.lambda().eq(SysUserSection::getUserId,user.getId());
        sysUserSectionService.remove(query3);
        if(i > 0){
            user.getRoleId();
            user.getDepartId();
            user.getSectionId();
            if(user.getRoleId() != null){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(user.getId());
                sysUserRole.setRoleId(user.getRoleId());
                sysUserRoleService.save(sysUserRole);
            }
            if(user.getDepartId() != null){
                SysUserDepart sysUserDepart = new SysUserDepart();
                sysUserDepart.setUserId(user.getId());
                sysUserDepart.setDepartId(user.getDepartId());
                sysUserDepartService.save(sysUserDepart);
            }
            if(user.getSectionId() != null){
                SysUserSection sysUserSection = new SysUserSection();
                sysUserSection.setUserId(user.getId());
                sysUserSection.setSectionId(user.getSectionId());
                sysUserSectionService.save(sysUserSection);
            }
        }
    }

    /**
     * 删除用户
     * @param id
     */
    @Override
    public void deleteUser(Integer id) {
        //删除用户
        int i = this.baseMapper.deleteById(id);
        if(i > 0){
            QueryWrapper<SysUserRole> query1 = new QueryWrapper<>();
            query1.lambda().eq(SysUserRole::getUserId,id);
            sysUserRoleService.remove(query1);
            QueryWrapper<SysUserDepart> query2 = new QueryWrapper<>();
            query2.lambda().eq(SysUserDepart::getUserId,id);
            sysUserDepartService.remove(query2);
            QueryWrapper<SysUserSection> query3 = new QueryWrapper<>();
            query3.lambda().eq(SysUserSection::getUserId,id);
            sysUserSectionService.remove(query3);
        }
    }

    /**
     * 查询菜单树
     * @param parm
     * @return
     */
    @Override
    public AssignTreeVo getAssingTree(AssignTreeParm parm) {
        //查询用户信息
        User user =this.baseMapper.selectById(parm.getUserId());
        List<Menu> menuList = null;
        //判断是否是超级管理员
        if(user != null && user.getUserNumber().equals("admin")){
            //是超级管理员
            menuList = menuService.list();
        }else{
            menuList = menuService.getMenuByUserId(parm.getUserId());
        }
        List<Menu> makeTree = MakeMenuTree.makeTree(menuList, 0);
        //查询角色原来的菜单
        List<Menu> roleList = menuService.getMenuByRoleId(parm.getRoleId());
        List<Integer> ids = new ArrayList<>();
        Optional.ofNullable(roleList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null)
                .forEach(item -> {
                    ids.add(item.getId());
                });
        //组装返回数据
        AssignTreeVo vo = new AssignTreeVo();
        vo.setCheckList(ids.toArray());
        vo.setMenuList(makeTree);
        return vo;
    }


}

package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.MenuRole;
import com.gymsys.entity.system.SaveMenuParm;
import com.gymsys.mapper.system.MenuRoleMapper;
import com.gymsys.service.system.MenuRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuRoleServiceImpl extends ServiceImpl<MenuRoleMapper, MenuRole> implements MenuRoleService {


    /**
     * 保存角色权限
     * @param parm
     */
    @Override
    @Transactional
    public void saveMenuRole(SaveMenuParm parm) {
       //删除
        QueryWrapper<MenuRole> query = new QueryWrapper<>();
        query.lambda().eq(MenuRole::getRoleId, parm.getRoleId());
        this.baseMapper.delete(query);
       //保存
        this.baseMapper.saveMenuRole(parm.getRoleId(), parm.getList());
    }
}

package com.gymsys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.system.MenuRole;
import com.gymsys.entity.system.SaveMenuParm;

public interface MenuRoleService extends IService<MenuRole> {

    /**
     * 保存用户菜单
     * @param parm
     */
    void saveMenuRole(SaveMenuParm parm);
}

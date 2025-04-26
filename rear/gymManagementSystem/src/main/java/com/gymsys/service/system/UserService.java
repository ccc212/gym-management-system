package com.gymsys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.system.AssignTreeParm;
import com.gymsys.entity.system.AssignTreeVo;
import com.gymsys.entity.system.User;

public interface UserService extends IService<User> {

    /**
     * 新增用户
     * @param user
     */
    void saveUser(User user);

    /**
     * 修改用户
     * @param user
     */
    void updateUser(User user);

    /**
     * 删除用户
     * @param id
     */
    void deleteUser(Integer id);

    /**
     * 查询菜单树
     * @param parm
     * @return
     */
    AssignTreeVo getAssingTree(AssignTreeParm parm);
}

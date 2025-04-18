package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.domain.entity.Result;
import com.gymsys.entity.system.Department;
import com.gymsys.entity.system.DepartmentParm;
import com.gymsys.entity.system.User;
import com.gymsys.entity.system.UserParm;
import com.gymsys.service.system.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/api/system/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user){
        user.setCreateTime(new Date());
        user.setPassword("123456");
        if(userService.save(user)){
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    @PutMapping
    public Result update(@RequestBody User user){
        user.setUpdateTime(new Date());
        if(userService.updateById(user)){
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id){
        if(userService.removeById(id)){
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取用户列表
     * @return
     */
    @GetMapping("/getList")
    public Result getList(UserParm parm){
        //构造分页
        IPage<User> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        //构造查询条件
        QueryWrapper<User> query = new QueryWrapper<>();
        //前端有条件则进行模糊查询
        if((StringUtils.isNotEmpty(parm.getUserNumber()))){
            query.lambda().like(User::getUserNumber, parm.getUserNumber());
        }
        if((StringUtils.isNotEmpty(parm.getName())))
        {
            query.lambda().like(User::getName, parm.getName());
        }
        if((StringUtils.isNotEmpty(parm.getUserType())))
        {
            query.lambda().like(User::getUserType, parm.getUserType());
        }
        IPage<User> list = userService.page(page, query);
        return Result.success(list);
    }

}

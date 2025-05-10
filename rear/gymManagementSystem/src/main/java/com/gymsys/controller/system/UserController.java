package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Result;
import com.gymsys.entity.system.*;
import com.gymsys.jwt.JwtUtils;
import com.gymsys.service.system.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api/system/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysUserDepartService sysUserDepartService;

    @Autowired
    private SysUserSectionService sysUserSectionService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MenuService menuService;




    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user){
        user.setCreateTime(new Date());
        user.setPassword("123456");
        if(user.getUserNumber().equals("admin")){
            return Result.error("管理员账号不能添加");
        }
        userService.saveUser(user);
        return Result.success("添加成功");
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    @PutMapping
    public Result update(@RequestBody User user){
        user.setUpdateTime(new Date());
        userService.updateUser(user);
        return Result.success("修改成功");
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id){
        userService.deleteUser(id);
        return Result.success("删除成功");
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

    /**
     * 编辑按钮回显用
     * @param id
     * @return
     */
    @GetMapping("/getRoleId")
    public Result getRoleId(Integer id){
        QueryWrapper<SysUserRole> query = new QueryWrapper<>();
        query.lambda().eq(SysUserRole::getUserId,id);
        SysUserRole sysUserRole = sysUserRoleService.getOne(query);
        Integer roleId = sysUserRole.getRoleId();
        return Result.success(roleId);
    }
    @GetMapping("/getDepartId")
    public Result getDepartmentId(Integer id){
        QueryWrapper<SysUserDepart> query = new QueryWrapper<>();
        query.lambda().eq(SysUserDepart::getUserId,id);
        SysUserDepart sysUserDepart = sysUserDepartService.getOne(query);
        Integer departId = sysUserDepart.getDepartId();
        return Result.success(departId);
    }
    @GetMapping("/getSectionId")
    public Result getSectionId(Integer id){
        QueryWrapper<SysUserSection> query = new QueryWrapper<>();
        query.lambda().eq(SysUserSection::getUserId,id);
        SysUserSection sysUserSection = sysUserSectionService.getOne(query);
        Integer sectionId = sysUserSection.getSectionId();
        return Result.success(sectionId);
    }

    /**
     * 重置密码
     * @param
     * @return
     */
    @PutMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable("id") Integer id){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(User::getId, id)
                .set(User::getPassword, "123456")
                .set(User::getUpdateTime, new Date());
        if (userService.update(updateWrapper)) {
            return Result.success("重置密码成功");
        } else {
            return Result.error("重置密码失败");
        }
    }

    /**
     * 登录
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginParm Parm){
        //查询用户信息
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUserNumber, Parm.getUserNumber())
                .eq(User::getPassword, Parm.getPassword());
        User one = userService.getOne(query);
        if(one == null){
            return Result.error("用户名或密码错误");
        }
        //返回用户信息
        LoginVo vo = new LoginVo();
        vo.setId(one.getId());
        vo.setUserNumber(one.getUserNumber());
        //生成token
        Map<String,String> map = new HashMap<>();
        map.put("id",Integer.toString(one.getId()));
        String token = jwtUtils.generateToken(map);
        vo.setToken(token);
        return Result.success(vo);
    }

    //查询菜单树
    @GetMapping("/getAssignTree")
    public Result getAssingTree(AssignTreeParm parm) {
        AssignTreeVo assignTree = userService.getAssingTree(parm);
        return Result.success(assignTree);
    }

    /**
     * 用户个人修改密码
     * @param parm
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody UpdatePasswordParm parm){
        User user = userService.getById(parm.getId());
        if(!parm.getOldPassword().equals(user.getPassword())){
            return Result.error("原密码不正确");
        }
        //更新条件
        UpdateWrapper<User> query = new UpdateWrapper<>();
        query.lambda().set(User::getPassword, parm.getPassword())
                .eq(User::getId, parm.getId());
        if(userService.update(query)){
            return Result.success("密码修改成功");
        }
        return Result.error("密码修改失败");
    }

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/getInfo")
    public Result getInfo(Integer id){
        //根据id查询用户信息
        User user = userService.getById(id);
        List<Menu> menuList = null;
        //判断是否为超级管理员
        if(user != null && user.getUserNumber().equals("admin")){
            //是超级管理员
            menuList = menuService.list();
        }else{
            menuList = menuService.getMenuByUserId(user.getId());
        }
        //获取菜单表code字段
        List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getCode()))
                .map(item ->item.getCode())
                .collect(Collectors.toList());
        //设置返回值
        UserInfo userInfo = new UserInfo();
        userInfo.setUserNumber(user.getUserNumber());
        userInfo.setId(user.getId());
        userInfo.setPermissions(collect.toArray());
        return Result.success(userInfo);
    }


}

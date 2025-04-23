package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gymsys.entity.Result;
import com.gymsys.entity.system.*;
import com.gymsys.service.system.MenuService;
import com.gymsys.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/system/menu")
@RestController
public class MenuController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;


    /**
     * 添加菜单
     * @param menu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Menu menu) {
        menu.setCreateTime(new Date());
        if(menuService.save(menu)){
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 编辑菜单
     * @param menu
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Menu menu) {
        menu.setUpdateTime(new Date());
        if(menuService.updateById(menu)){
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        //如果存在下级,取消删除
        QueryWrapper<Menu> query = new QueryWrapper<>();
        query.lambda().eq(Menu::getParentId, id);
        List<Menu> list = menuService.list(query);
        if(list.size() > 0){
            return Result.error("存在下级，不能删除");
        }
        if(menuService.removeById(id)){
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取菜单列表
     * @return
     */
    @GetMapping("/list")
    public Result getList() {
        //排序
        QueryWrapper<Menu> query = new QueryWrapper<>();
        query.lambda().orderByDesc(Menu::getOrderNum);
        //查数据
        List<Menu> list = menuService.list(query);
        List<Menu> menuList = MakeMenuTree.makeTree(list, 0);
        return Result.success(menuList);
    }


    /**
     * 上级菜单
     * @return
     */
    @GetMapping("/getParent")
    public Result getParent() {
        List<Menu> list = menuService.getParent();
        return Result.success(list);
    }

    /**
     * 获取菜单
     * @return
     */
    @GetMapping("/getMenuList")
    public Result getMenuList(Integer id){
        //获取用户的信息
        User user = userService.getById(id);
        //获取菜单数据
        List<Menu> menuList = null;
        //判断是否为超级管理员
        if(user != null && user.getUserNumber().equals("admin")){
            menuList = menuService.list();
        }else{
            menuList = menuService.getMenuByUserId(id);
        }
        //过滤菜单数据,去掉按钮
        List<Menu> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> StringUtils.isNotEmpty(item.getType())&& !item.getType().equals("2")).collect(Collectors.toList());
        //组装路由数据
        List<RouterVO> router = MakeMenuTree.makeRouter(collect, 0);
        return Result.success(router);
    }

}

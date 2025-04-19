package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.gymsys.entity.Result;
import com.gymsys.entity.system.MakeMenuTree;
import com.gymsys.entity.system.Menu;
import com.gymsys.service.system.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/api/system/menu")
@RestController
public class MenuController {
    @Autowired
    private MenuService menuService;

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

}

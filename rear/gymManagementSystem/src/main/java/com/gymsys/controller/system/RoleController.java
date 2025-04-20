package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.Result;
import com.gymsys.entity.system.Role;
import com.gymsys.entity.system.RoleParm;
import com.gymsys.entity.system.SaveMenuParm;
import com.gymsys.entity.system.SelectItme;
import com.gymsys.service.system.MenuRoleService;
import com.gymsys.service.system.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/system/role")
@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuRoleService menuRoleService;

    /**
     * 添加角色
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Role role){
        role.setCreateTime(new Date());
        if(roleService.save(role)){
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 修改角色
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Role role){
        role.setUpdateTime(new Date());
        if(roleService.updateById(role)){
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 删除角色
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id){
        if(roleService.removeById(id)){
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取角色列表
     * @return
     */
    @GetMapping("/getList")
    public Result getList(RoleParm parm){
        //构造分页
        IPage<Role> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        //构造查询条件
        QueryWrapper<Role> query = new QueryWrapper<>();
        //前端有条件则进行模糊查询
        if((StringUtils.isNotEmpty(parm.getRoleName()))){
            query.lambda().like(Role::getRoleName, parm.getRoleName());
        }
        IPage<Role> list = roleService.page(page, query);
        return Result.success(list);
    }

    /**
     * 获取角色下拉列表
     * @return
     */
    @GetMapping("/selectList")
    public Result selectList(){
        List<Role> list = roleService.list();
        List<SelectItme> selectList = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item -> {
                    SelectItme vo = new SelectItme();
                    vo.setLabel(item.getRoleName());
                    vo.setValue(item.getId());
                    selectList.add(vo);
                });
        return Result.success(selectList);
    }

    /**
     * 保存角色菜单
     * @param parm
     * @return
     */
    @PostMapping("/saveRoleMenu")
    public Result saveRoleMenu(@RequestBody SaveMenuParm parm){
        menuRoleService.saveMenuRole(parm);
        return Result.success("添加菜单成功");
    }
}


package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.domain.entity.Result;
import com.gymsys.entity.system.Department;
import com.gymsys.entity.system.DepartmentParm;
import com.gymsys.entity.system.Role;
import com.gymsys.entity.system.RoleParm;
import com.gymsys.service.system.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/api/system/department")
@RestController
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    /**
     * 添加部门
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Department department){
        department.setCreateTime(new Date());
        if(departmentService.save(department)){
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 修改部门
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Department department){
        department.setUpdateTime(new Date());
        if(departmentService.updateById(department)){
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 删除部门
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id){
        if(departmentService.removeById(id)){
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取部门列表
     * @return
     */
    @GetMapping("/getList")
    public Result getList(DepartmentParm parm){
        //构造分页
        IPage<Department> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        //构造查询条件
        QueryWrapper<Department> query = new QueryWrapper<>();
        //前端有条件则进行模糊查询
        if((StringUtils.isNotEmpty(parm.getDepartName()))){
            query.lambda().like(Department::getDepartName, parm.getDepartName());
        }
        if((StringUtils.isNotEmpty(parm.getDepartStuorfac())))
        {
            query.lambda().like(Department::getDepartStuorfac, parm.getDepartStuorfac());
        }
        IPage<Department> list = departmentService.page(page, query);
        return Result.success(list);
    }
}

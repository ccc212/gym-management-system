package com.gymsys.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.domain.entity.Result;
import com.gymsys.entity.system.*;
import com.gymsys.service.system.SectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/system/section")
@RestController
public class SectionController {
    @Autowired
    private SectionService sectionService;

    /**
     * 添加班级
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Section section){
        section.setCreateTime(new Date());
        if(sectionService.save(section)){
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 修改班级
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Section section){
        section.setUpdateTime(new Date());
        if(sectionService.updateById(section)){
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 删除班级
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id){
        if(sectionService.removeById(id)){
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 获取班级列表
     * @return
     */
    @GetMapping("/getList")
    public Result getList(SectionParm parm){
        //构造分页
        IPage<Section> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        //构造查询条件
        QueryWrapper<Section> query = new QueryWrapper<>();
        //前端有条件则进行模糊查询
        if((StringUtils.isNotEmpty(parm.getSectionName()))){
            query.lambda().like(Section::getSectionName, parm.getSectionName());
        }
        IPage<Section> list = sectionService.page(page, query);
        return Result.success(list);
    }

    /**
     * 获取班级下拉列表
     * @return
     */
    @GetMapping("/selectList")
    public Result selectList(){
        List<Section> list = sectionService.list();
        List<SelectItme> selectList = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item -> {
                    SelectItme vo = new SelectItme();
                    vo.setLabel(item.getSectionName());
                    vo.setValue(item.getId());
                    selectList.add(vo);
                });
        return Result.success(selectList);
    }
}

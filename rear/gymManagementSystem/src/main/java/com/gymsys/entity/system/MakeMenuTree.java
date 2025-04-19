package com.gymsys.entity.system;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MakeMenuTree {
    public static List<Menu> makeTree(List<Menu> menulist, Integer parentId) {
        //存放组装数据
        List<Menu> list = new ArrayList<>();
        //组装树
        Optional.ofNullable(menulist).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(parentId))
                .forEach(item -> {
                    Menu menu = new Menu();
                    BeanUtils.copyProperties(item, menu);
                    menu.setLabel(item.getTitle());
                    menu.setValue(item.getId());
                    //查找下级
                    List<Menu> children = makeTree(menulist, item.getId());
                    menu.setChildren(children);
                    list.add(menu);
                });
        return list;
    }
}

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

    //构造路由器
    public static List<RouterVO> makeRouter(List<Menu> menuList, Integer pid) {
        //构建存放路由数据的容器
        List<RouterVO> list = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item -> {
                    RouterVO router = new RouterVO();
                    router.setName(item.getName());
                    router.setPath(item.getPath());
                    //设置children 递归调用
                    List<RouterVO> children = makeRouter(menuList, item.getId());
                    router.setChildren(children);
                    if(item.getParentId() == 0){
                        router.setComponent("Layout");//如果上级是0,那么他的component是Layout
                        //判断该数据是目录还是菜单
                        if(item.getType().equals("1")){
                            //如果是菜单，单独处理
                            router.setRedirect(item.getPath());
                            //菜单设置childeren
                            List<RouterVO> listChild = new ArrayList<>();
                            RouterVO child = new RouterVO();
                            child.setName(item.getName());
                            child.setPath(item.getPath());
                            child.setComponent(item.getUrl());
                            child.setMeta(child.new Meta(
                                    item.getTitle(),
                                    item.getIcon(),
                                    item.getCode().split(",")
                            ));
                            listChild.add(child);
                            router.setChildren(listChild);
                            router.setPath(item.getPath()+"parent");
                            router.setName(item.getName()+"parent");
                        }
                    }else {
                        router.setComponent(item.getUrl());
                    }
                    router.setMeta(router.new Meta(
                            item.getTitle(),
                            item.getIcon(),
                            item.getCode().split(",")
                    ));
                    list.add(router);
                });
        return list;
    }
}

package com.gymsys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.system.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    List<Menu> getParent();
}

package com.gymsys.entity.system;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignTreeVo {
    private List<Menu> menuList = new ArrayList<>();
    private Object[] checkList;
}

package com.gymsys.entity.system;

import lombok.Data;

import java.util.List;

@Data
public class SaveMenuParm {
    private Integer roleId;
    private List<Integer> list;
}

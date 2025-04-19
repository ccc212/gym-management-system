package com.gymsys.entity.system;

import lombok.Data;

@Data
public class EquipmentParm {
    private Long currentPage;
    private Long pageSize;
    private String equipmentName;
    private Integer status; // 按状态筛选
}
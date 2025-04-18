package com.gymsys.entity.system;

import lombok.Data;

@Data
public class DepartmentParm {
    private Long currentPage;
    private Long pageSize;
    private String departName;
    private String departStuorfac;
}

package com.gymsys.entity.system;

import lombok.Data;

@Data
public class SectionParm {
    private Long currentPage;
    private Long pageSize;
    private String sectionName;
}

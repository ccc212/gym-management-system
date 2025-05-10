package com.gymsys.entity.system;

import lombok.Data;

@Data
public class UserParm {
    private Long currentPage;
    private Long pageSize;
    private String userNumber;
    private String name;
    private String userType;
}

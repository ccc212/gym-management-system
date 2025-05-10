package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.Department;
import com.gymsys.mapper.system.DepartmentMapper;
import com.gymsys.service.system.DepartmentService;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
}

package com.gymsys.service.system.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.Role;
import com.gymsys.mapper.system.RoleMapper;
import com.gymsys.service.system.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>implements RoleService {

}

package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.SysUserDepart;
import com.gymsys.mapper.system.SysUserDepartMapper;
import com.gymsys.service.system.SysUserDepartService;
import org.springframework.stereotype.Service;

@Service
public class SysUserDepartServiceImpl extends ServiceImpl<SysUserDepartMapper, SysUserDepart> implements SysUserDepartService {
}

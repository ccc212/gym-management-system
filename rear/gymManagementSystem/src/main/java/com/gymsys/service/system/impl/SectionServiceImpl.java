package com.gymsys.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.system.Section;
import com.gymsys.mapper.system.SectionMapper;
import com.gymsys.service.system.SectionService;
import org.springframework.stereotype.Service;

@Service
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section> implements SectionService {
}

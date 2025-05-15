package com.gymsys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.Venue;
import com.gymsys.mapper.VenueMapper;
import com.gymsys.service.VenueService;
import org.springframework.stereotype.Service;

@Service
public class VenueServiceImpl extends ServiceImpl<VenueMapper, Venue> implements VenueService {
} 
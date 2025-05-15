package com.gymsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.dto.NoshowDTO;
import com.gymsys.entity.noshow.NoshowEntity;
import com.gymsys.mapper.noshow.NoshowMapper;
import com.gymsys.service.NoshowService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class NoshowServiceImpl extends ServiceImpl<NoshowMapper, NoshowEntity> implements NoshowService {

    @Resource
    private NoshowMapper noshowMapper;

    @Override
    public Page<NoshowEntity> getNoshows(Integer page, Integer size, String venueType, Long venueId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<NoshowEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(venueType)) {
            wrapper.eq(NoshowEntity::getVenueType, venueType);
        }
        if (venueId != null) {
            wrapper.eq(NoshowEntity::getVenueId, venueId);
        }
        if (startDate != null) {
            wrapper.ge(NoshowEntity::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(NoshowEntity::getDate, endDate);
        }
        
        wrapper.orderByDesc(NoshowEntity::getCreatedAt);
        
        return noshowMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public NoshowEntity handleNoshow(Long id, NoshowDTO dto) {
        NoshowEntity noshow = noshowMapper.selectById(id);
        if (noshow == null) {
            throw new RuntimeException("失约记录不存在");
        }

        if (!"PENDING".equals(noshow.getStatus())) {
            throw new RuntimeException("该失约记录已处理");
        }

        // 更新处理信息
        noshow.setStatus(dto.getStatus());
        noshow.setHandler(dto.getHandler());
        noshow.setHandleTime(LocalDateTime.now());
        noshow.setReason(dto.getReason());
        
        if ("PENALIZED".equals(dto.getStatus())) {
            noshow.setPenalty(dto.getPenalty());
            noshow.setRestrictDays(dto.getRestrictDays());
        }

        noshow.setUpdatedAt(LocalDateTime.now());
        noshowMapper.updateById(noshow);

        // TODO: 如果需要通知用户，这里可以添加发送通知的逻辑

        return noshow;
    }

    @Override
    public NoshowEntity getNoshowDetail(Long id) {
        return noshowMapper.selectById(id);
    }

    @Override
    public boolean save(NoshowEntity noshow) {
        return noshowMapper.insert(noshow) > 0;
    }
} 
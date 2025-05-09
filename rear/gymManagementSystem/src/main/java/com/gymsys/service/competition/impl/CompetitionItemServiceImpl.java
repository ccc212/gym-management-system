package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.CompetitionItem;
import com.gymsys.entity.competition.CompetitionItemRelation;
import com.gymsys.entity.competition.dto.competitionItem.AddCompetitionItemDTO;
import com.gymsys.entity.competition.dto.competitionItem.ListCompetitionItemDTO;
import com.gymsys.entity.competition.dto.competitionItem.UpdateCompetitionItemDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionItemMapper;
import com.gymsys.mapper.competition.CompetitionItemRelationMapper;
import com.gymsys.service.competition.ICompetitionItemService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 赛事项目表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
public class CompetitionItemServiceImpl extends ServiceImpl<CompetitionItemMapper, CompetitionItem> implements ICompetitionItemService {

    private final CompetitionItemRelationMapper competitionItemRelationMapper;

    @Override
    public void addCompetitionItem(AddCompetitionItemDTO addCompetitionItemDTO) {
        // 检查赛事项目名称是否已存在
        if (lambdaQuery().eq(CompetitionItem::getName, addCompetitionItemDTO.getName()).exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_ITEM_NAME_ALREADY_EXISTS);
        }

        // 保存赛事项目
        save(BeanUtil.copyProperties(addCompetitionItemDTO, CompetitionItem.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompetitionItem(Long id) {
        // 检查赛事项目是否存在
        if (!lambdaQuery().eq(CompetitionItem::getId, id).exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_ITEM_NOT_EXIST);
        }

        // 检查赛事项目是否被使用
        LambdaQueryWrapper<CompetitionItemRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionItemRelation::getCompetitionItemId, id);
        if (competitionItemRelationMapper.exists(queryWrapper)) {
            throw new BizException(StatusCodeEnum.COMPETITION_ITEM_IS_USED);
        }

        // 删除赛事项目
        removeById(id);
    }

    @Override
    public void updateCompetitionItem(UpdateCompetitionItemDTO updateCompetitionItemDTO) {
        // 检查赛事项目是否存在
        if (!lambdaQuery().eq(CompetitionItem::getId, updateCompetitionItemDTO.getId()).exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_ITEM_NOT_EXIST);
        }

        // 检查赛事项目名称是否已存在(不包括自己)
        if (StringUtils.isNotBlank(updateCompetitionItemDTO.getName()) && 
            lambdaQuery().eq(CompetitionItem::getName, updateCompetitionItemDTO.getName())
                        .ne(CompetitionItem::getId, updateCompetitionItemDTO.getId())
                        .exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_ITEM_NAME_ALREADY_EXISTS);
        }

        // 更新赛事项目
        CompetitionItem competitionItem = BeanUtil.copyProperties(updateCompetitionItemDTO, CompetitionItem.class);
        updateById(competitionItem);
    }

    @Override
    public IPage<CompetitionItem> listCompetitionItem(ListCompetitionItemDTO listCompetitionItemDTO) {
        LambdaQueryWrapper<CompetitionItem> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.like(StringUtils.isNotBlank(listCompetitionItemDTO.getName()), 
                           CompetitionItem::getName, listCompetitionItemDTO.getName())
                   .eq(listCompetitionItemDTO.getType() != null, 
                        CompetitionItem::getType, listCompetitionItemDTO.getType())
                   .eq(listCompetitionItemDTO.getCategory() != null, 
                        CompetitionItem::getCategory, listCompetitionItemDTO.getCategory())
                   .eq(listCompetitionItemDTO.getIsTeamCompetition() != null, 
                        CompetitionItem::getIsTeamCompetition, listCompetitionItemDTO.getIsTeamCompetition())
                   .orderByDesc(CompetitionItem::getCreateTime);
        
        // 分页查询
        return page(new Page<>(listCompetitionItemDTO.getPage(), listCompetitionItemDTO.getPageSize()), queryWrapper);
    }
}

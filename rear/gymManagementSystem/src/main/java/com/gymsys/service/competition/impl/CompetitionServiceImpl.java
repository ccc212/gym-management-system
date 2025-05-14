package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.*;
import com.gymsys.entity.competition.dto.competition.AddCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.UpdateCompetitionDTO;
import com.gymsys.entity.competition.vo.CompetitionDetailVO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.*;
import com.gymsys.repository.competition.CompetitionVenueRelationRepository;
import com.gymsys.service.competition.ICompetitionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 赛事表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements ICompetitionService {

    private final CompetitionItemRelationMapper competitionItemRelationMapper;

    private final CompetitionVenueRelationRepository competitionVenueRelationRepository;

    private final CompetitionEquipmentRelationMapper competitionEquipmentRelationMapper;

    private final CompetitionItemMapper competitionItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCompetition(AddCompetitionDTO addCompetitionDTO) {
        // 检查赛事名称是否已存在
        if (lambdaQuery().eq(Competition::getName, addCompetitionDTO.getName()).exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_NAME_ALREADY_EXISTS);
        }

        // 检查截止时间是否在比赛时间之前
        if (addCompetitionDTO.getSignUpDeadline().isAfter(addCompetitionDTO.getStartTime())) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_DEADLINE_AFTER_START_TIME);
        }

        // 保存赛事
        Competition competition = BeanUtil.copyProperties(addCompetitionDTO, Competition.class);
        // 设置初始化状态: 未开始
        competition.setStatus(0);
        // 设置初始已报人数
        competition.setSignUpNum(0);
        
        save(competition);

        // 保存赛事与项目的关联关系
        if (addCompetitionDTO.getCompetitionItemIds() != null && !addCompetitionDTO.getCompetitionItemIds().isEmpty()) {
            saveCompetitionItemRelations(competition.getId(), addCompetitionDTO.getCompetitionItemIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompetition(Long id) {
        // 检查赛事是否存在
        Competition competition = getById(id);
        if (competition == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 删除赛事与项目的关联关系
        LambdaQueryWrapper<CompetitionItemRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionItemRelation::getCompetitionId, id);
        competitionItemRelationMapper.delete(queryWrapper);

        // 删除赛事
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompetition(UpdateCompetitionDTO updateCompetitionDTO) {
        // 检查赛事是否存在
        if (!lambdaQuery().eq(Competition::getId, updateCompetitionDTO.getId()).exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 检查截止时间是否在比赛时间之前
        if (updateCompetitionDTO.getSignUpDeadline() != null
                && updateCompetitionDTO.getStartTime() != null
                && updateCompetitionDTO.getEndTime() != null
                && updateCompetitionDTO.getSignUpDeadline().isAfter(updateCompetitionDTO.getStartTime())) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_DEADLINE_AFTER_START_TIME);
        }

        // 检查赛事名称是否已存在(不包括自己)
        if (StringUtils.isNotBlank(updateCompetitionDTO.getName()) && 
            lambdaQuery().eq(Competition::getName, updateCompetitionDTO.getName())
                        .ne(Competition::getId, updateCompetitionDTO.getId())
                        .exists()) {
            throw new BizException(StatusCodeEnum.COMPETITION_NAME_ALREADY_EXISTS);
        }

        // 更新赛事
        Competition competition = BeanUtil.copyProperties(updateCompetitionDTO, Competition.class);
        updateById(competition);

        // 更新赛事与项目的关联关系
        if (updateCompetitionDTO.getCompetitionItemIds() != null) {
            // 删除原有关联关系
            LambdaQueryWrapper<CompetitionItemRelation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CompetitionItemRelation::getCompetitionId, updateCompetitionDTO.getId());
            competitionItemRelationMapper.delete(queryWrapper);
            
            // 添加新的关联关系
            if (!updateCompetitionDTO.getCompetitionItemIds().isEmpty()) {
                saveCompetitionItemRelations(updateCompetitionDTO.getId(), updateCompetitionDTO.getCompetitionItemIds());
            }
        }
    }

    @Override
    public IPage<Competition> listCompetition(ListCompetitionDTO listCompetitionDTO) {
        LambdaQueryWrapper<Competition> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.like(StringUtils.isNotBlank(listCompetitionDTO.getName()), 
                           Competition::getName, listCompetitionDTO.getName())
                   .eq(listCompetitionDTO.getType() != null, 
                        Competition::getType, listCompetitionDTO.getType())
                   .eq(listCompetitionDTO.getCategory() != null, 
                        Competition::getCategory, listCompetitionDTO.getCategory())
                   .like(StringUtils.isNotBlank(listCompetitionDTO.getHoster()), 
                          Competition::getHoster, listCompetitionDTO.getHoster())
                   .eq(listCompetitionDTO.getStatus() != null, 
                        Competition::getStatus, listCompetitionDTO.getStatus())
                   .eq(listCompetitionDTO.getIsTeamCompetition() != null, 
                        Competition::getIsTeamCompetition, listCompetitionDTO.getIsTeamCompetition())
                   .orderByDesc(Competition::getCreateTime);
        
        // 分页查询
        return page(new Page<>(listCompetitionDTO.getPage(), listCompetitionDTO.getPageSize()), queryWrapper);
    }

    @Override
    public CompetitionDetailVO getDetail(Long id) {
        Competition competition = lambdaQuery().eq(Competition::getId, id).one();

        List<CompetitionVenueRelation> competitionVenueRelations = competitionVenueRelationRepository.findByCompetitionId(id);

        List<CompetitionEquipmentRelation> competitionEquipmentRelations = competitionEquipmentRelationMapper.selectList(new LambdaQueryWrapper<CompetitionEquipmentRelation>()
                .eq(CompetitionEquipmentRelation::getCompetitionId, id));

        return new CompetitionDetailVO()
                .setId(competition.getId())
                .setName(competition.getName())
                .setType(competition.getType())
                .setCategory(competition.getCategory())
                .setHoster(competition.getHoster())
                .setStartTime(competition.getStartTime())
                .setEndTime(competition.getEndTime())
                .setSignUpDeadline(competition.getSignUpDeadline())
                .setIsTeamCompetition(competition.getIsTeamCompetition())
                .setVenueRelations(competitionVenueRelations)
                .setEquipmentRelations(competitionEquipmentRelations)
                .setRequirement(competition.getRequirement())
                .setDescription(competition.getDescription());
    }

    @Override
    public List<CompetitionItem> listItem() {
        return competitionItemMapper.selectList(null);
    }

    @Override
    public List<CompetitionItemRelation> listItemByCompetitionId(Long competitionId) {
        return competitionItemRelationMapper.selectList(new LambdaQueryWrapper<CompetitionItemRelation>()
                .eq(CompetitionItemRelation::getCompetitionId, competitionId));
    }

    /**
     * 保存赛事与项目的关联关系
     *
     * @param competitionId 赛事ID
     * @param itemIds 项目ID列表
     */
    private void saveCompetitionItemRelations(Long competitionId, List<Long> itemIds) {
        List<CompetitionItemRelation> relations = new ArrayList<>();
        for (Long itemId : itemIds) {
            CompetitionItemRelation relation = new CompetitionItemRelation();
            relation.setCompetitionId(competitionId);
            relation.setCompetitionItemId(itemId);
            relations.add(relation);
        }
        for (CompetitionItemRelation relation : relations) {
            competitionItemRelationMapper.insert(relation);
        }
    }
}

package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionItemRelation;
import com.gymsys.entity.competition.dto.competition.AddCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.UpdateCompetitionDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionItemRelationMapper;
import com.gymsys.mapper.competition.CompetitionMapper;
import com.gymsys.service.competition.ICompetitionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public void addCompetition(AddCompetitionDTO addCompetitionDTO) {
        // 添加赛事
        Competition competition = BeanUtil.copyProperties(addCompetitionDTO, Competition.class);
        save(competition);

        // 添加赛事与项目的关联
        List<Long> competitionItemIds = addCompetitionDTO.getCompetitionItemIds();
        List<CompetitionItemRelation> competitionItemRelations = competitionItemIds.stream()
                .map(competitionItemId -> CompetitionItemRelation.builder()
                        .competitionId(competition.getId())
                        .competitionItemId(competitionItemId)
                        .build())
                .toList();
        competitionItemRelationMapper.insert(competitionItemRelations);
    }

    @Override
    public void deleteCompetition(Long id) {
        Competition isExist = lambdaQuery().eq(Competition::getId, id).one();
        if (isExist == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        removeById(id);
    }

    @Override
    public void updateCompetition(UpdateCompetitionDTO updateCompetitionDTO) {
        String competitionName = updateCompetitionDTO.getName();

        // 更新赛事信息
        lambdaUpdate().eq(Competition::getName, competitionName)
                .set(updateCompetitionDTO.getType() != null, Competition::getType, updateCompetitionDTO.getType())
                .set(updateCompetitionDTO.getCategory() != null, Competition::getCategory, updateCompetitionDTO.getCategory())
                .set(updateCompetitionDTO.getHoster() != null, Competition::getHoster, updateCompetitionDTO.getHoster())
                .set(updateCompetitionDTO.getStartTime() != null, Competition::getStartTime, updateCompetitionDTO.getStartTime())
                .set(updateCompetitionDTO.getEndTime() != null, Competition::getEndTime, updateCompetitionDTO.getEndTime())
                .set(updateCompetitionDTO.getSignUpDeadline() != null, Competition::getSignUpDeadline, updateCompetitionDTO.getSignUpDeadline())
                .set(updateCompetitionDTO.getMaxSignUpNum() != null, Competition::getMaxSignUpNum, updateCompetitionDTO.getMaxSignUpNum())
                .set(updateCompetitionDTO.getStatus() != null, Competition::getStatus, updateCompetitionDTO.getStatus())
                .set(updateCompetitionDTO.getIsTeamCompetition() != null, Competition::getIsTeamCompetition, updateCompetitionDTO.getIsTeamCompetition())
                .set(updateCompetitionDTO.getTeamMinNum() != null, Competition::getTeamMinNum, updateCompetitionDTO.getTeamMinNum())
                .set(updateCompetitionDTO.getTeamMaxNum() != null, Competition::getTeamMaxNum, updateCompetitionDTO.getTeamMaxNum())
                .set(updateCompetitionDTO.getRequirement() != null, Competition::getRequirement, updateCompetitionDTO.getRequirement())
                .set(updateCompetitionDTO.getDescription() != null, Competition::getDescription, updateCompetitionDTO.getDescription());

        // 更新赛事与项目的关联
        List<Long> competitionItemIds = updateCompetitionDTO.getCompetitionItemIds();
        if (competitionItemIds != null) {
            Competition competition = lambdaQuery().eq(Competition::getName, competitionName).one();
            competitionItemRelationMapper.delete(new LambdaQueryWrapper<CompetitionItemRelation>()
                    .eq(CompetitionItemRelation::getCompetitionId, competition.getId()));
            competitionItemRelationMapper.insert(competitionItemIds.stream()
                    .map(competitionItemId -> CompetitionItemRelation.builder()
                            .competitionId(competition.getId())
                            .competitionItemId(competitionItemId)
                            .build())
                    .toList());
        }
    }

    @Override
    public IPage<Competition> listCompetition(ListCompetitionDTO listCompetitionDTO) {
        return page(new Page<>(listCompetitionDTO.getPage(), listCompetitionDTO.getPageSize()),
                new LambdaQueryWrapper<Competition>()
                        .eq(listCompetitionDTO.getId() != null, Competition::getId, listCompetitionDTO.getId())
                        .like(StringUtils.isNotBlank(listCompetitionDTO.getName()), Competition::getName, listCompetitionDTO.getName())
                        .eq(listCompetitionDTO.getType() != null, Competition::getType, listCompetitionDTO.getType())
                        .eq(listCompetitionDTO.getStatus() != null, Competition::getStatus, listCompetitionDTO.getStatus()));
    }
}

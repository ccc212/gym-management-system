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

    private final CompetitionVenueRelationMapper competitionVenueRelationMapper;

    private final CompetitionEquipmentRelationMapper competitionEquipmentRelationMapper;

    private final CompetitionItemMapper competitionItemMapper;

    @Override
    public void addCompetition(AddCompetitionDTO addCompetitionDTO) {
        // 添加赛事
        Competition competition = BeanUtil.copyProperties(addCompetitionDTO, Competition.class);
        save(competition);

        // 添加赛事与项目的关联
        List<Long> competitionItemIds = addCompetitionDTO.getCompetitionItemIds();
        List<CompetitionItemRelation> competitionItemRelations = competitionItemIds.stream()
                .map(competitionItemId -> new CompetitionItemRelation()
                        .setCompetitionId(competition.getId())
                        .setCompetitionItemId(competitionItemId))
                .toList();
        competitionItemRelationMapper.insert(competitionItemRelations);
    }

    @Override
    public void deleteCompetition(Long id) {
        if (lambdaQuery().eq(Competition::getId, id).one() == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        removeById(id);
    }

    @Override
    public void updateCompetition(UpdateCompetitionDTO updateCompetitionDTO) {
        String competitionName = updateCompetitionDTO.getName();

        // 更新赛事信息
        lambdaUpdate().eq(Competition::getId, updateCompetitionDTO.getId())
                .set(updateCompetitionDTO.getName() != null, Competition::getName, updateCompetitionDTO.getName())
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
                    .map(competitionItemId -> new CompetitionItemRelation()
                            .setCompetitionId(competition.getId())
                            .setCompetitionItemId(competitionItemId))
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

    @Override
    public CompetitionDetailVO getDetail(Long id) {
        Competition competition = lambdaQuery().eq(Competition::getId, id).one();

        List<CompetitionVenueRelation> competitionVenueRelations = competitionVenueRelationMapper.selectList(new LambdaQueryWrapper<CompetitionVenueRelation>()
                .eq(CompetitionVenueRelation::getCompetitionId, id));

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
}

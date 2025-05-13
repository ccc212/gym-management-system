package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.TeamMemberRelation;
import com.gymsys.entity.competition.dto.teamMemberRelation.AddTeamMemberRelationDTO;
import com.gymsys.entity.competition.dto.teamMemberRelation.ListTeamMemberRelationDTO;
import com.gymsys.entity.competition.vo.TeamMemberRelationVO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.enums.TeamMemberStatusEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.TeamMemberRelationMapper;
import com.gymsys.service.competition.ITeamMemberRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 团队与队员关联表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@Service
@RequiredArgsConstructor
public class TeamMemberRelationServiceImpl extends ServiceImpl<TeamMemberRelationMapper, TeamMemberRelation> implements ITeamMemberRelationService {

    private final TeamMemberRelationMapper teamMemberRelationMapper;

    @Override
    public void addTeamMemberRelation(AddTeamMemberRelationDTO addTeamMemberRelationDTO) {
        // 检查是否已存在关联
        List<TeamMemberRelation> list = lambdaQuery().eq(TeamMemberRelation::getTeamId, addTeamMemberRelationDTO.getTeamId())
                .eq(TeamMemberRelation::getUserId, addTeamMemberRelationDTO.getUserId()).list();

        // 遍历list查看是否存在通过的
        for (TeamMemberRelation teamMemberRelation : list) {
            if (teamMemberRelation.getStatus() == TeamMemberStatusEnum.APPROVED.getCode()) {
                throw new BizException(StatusCodeEnum.TEAM_MEMBER_RELATION_EXISTS);
            }
        }

        save(BeanUtil.copyProperties(addTeamMemberRelationDTO, TeamMemberRelation.class));
    }

    @Override
    public void deleteTeamMemberRelation(Long id) {
        if (lambdaQuery().eq(TeamMemberRelation::getId, id).one() == null) {
            throw new BizException(StatusCodeEnum.TEAM_MEMBER_RELATION_NOT_EXIST);
        }

        removeById(id);
    }

    @Override
    public List<TeamMemberRelationVO> getUserApplications(Long userId, Integer status) {
        return teamMemberRelationMapper.getUserApplications(userId, status);
    }

    @Override
    public List<TeamMemberRelationVO> getTeamApplications(Long teamId, Integer status) {
        return teamMemberRelationMapper.getTeamApplications(teamId, status);
    }

    @Override
    public void updateApplicationStatus(Long id, Integer status) {
        TeamMemberRelation relation = getById(id);
        if (relation == null) {
            throw new BizException(StatusCodeEnum.TEAM_MEMBER_RELATION_NOT_EXIST);
        }

        relation.setStatus(status);
        updateById(relation);
    }

    @Override
    public boolean isUserInTeam(Long teamId, Long userId) {
        LambdaQueryWrapper<TeamMemberRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamMemberRelation::getTeamId, teamId)
                .eq(TeamMemberRelation::getUserId, userId)
                .eq(TeamMemberRelation::getStatus, 1); // 状态为已通过

        return count(queryWrapper) > 0;
    }

    @Override
    public IPage<TeamMemberRelation> listTeamMemberRelation(ListTeamMemberRelationDTO listTeamMemberRelationDTO) {
        LambdaQueryWrapper<TeamMemberRelation> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        queryWrapper.eq(listTeamMemberRelationDTO.getTeamId() != null,
                        TeamMemberRelation::getTeamId, listTeamMemberRelationDTO.getTeamId())
                .eq(listTeamMemberRelationDTO.getUserId() != null,
                        TeamMemberRelation::getUserId, listTeamMemberRelationDTO.getUserId())
                .eq(listTeamMemberRelationDTO.getStatus() != null,
                        TeamMemberRelation::getStatus, listTeamMemberRelationDTO.getStatus());

        return page(new Page<>(listTeamMemberRelationDTO.getPage(), listTeamMemberRelationDTO.getPageSize()), queryWrapper);
    }
}

package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.CompetitionSignUpTeam;
import com.gymsys.entity.competition.Team;
import com.gymsys.entity.competition.TeamMemberRelation;
import com.gymsys.entity.competition.dto.team.AddTeamDTO;
import com.gymsys.entity.competition.dto.team.ListTeamDTO;
import com.gymsys.entity.competition.dto.team.UpdateTeamDTO;
import com.gymsys.entity.competition.vo.TeamDetailVO;
import com.gymsys.entity.system.User;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionSignUpTeamMapper;
import com.gymsys.mapper.competition.TeamMapper;
import com.gymsys.mapper.competition.TeamMemberRelationMapper;
import com.gymsys.mapper.system.DepartmentMapper;
import com.gymsys.mapper.system.UserMapper;
import com.gymsys.service.competition.ITeamService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 团队表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {

    private final TeamMemberRelationMapper teamMemberRelationMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final CompetitionSignUpTeamMapper competitionSignUpTeamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTeam(AddTeamDTO addTeamDTO) {
        // 检查团队名称是否已存在
        if (lambdaQuery().eq(Team::getTeamName, addTeamDTO.getTeamName()).exists()) {
            throw new BizException(StatusCodeEnum.TEAM_NAME_ALREADY_EXISTS);
        }

        // 保存团队
        Team team = BeanUtil.copyProperties(addTeamDTO, Team.class);
        save(team);

        // 保存团队成员关系
        if (addTeamDTO.getMemberIds() != null && !addTeamDTO.getMemberIds().isEmpty()) {
            saveTeamMemberRelations(team.getId(), addTeamDTO.getMemberIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeam(Long id) {
        // 检查团队是否存在
        Team team = getById(id);
        if (team == null) {
            throw new BizException(StatusCodeEnum.TEAM_NOT_EXIST);
        }

        // 检查团队是否被赛事报名使用
        LambdaQueryWrapper<CompetitionSignUpTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionSignUpTeam::getTeamId, id);
        if (competitionSignUpTeamMapper.exists(queryWrapper)) {
            throw new BizException(StatusCodeEnum.TEAM_IS_USED);
        }

        // 删除团队成员关系
        LambdaQueryWrapper<TeamMemberRelation> memberQueryWrapper = new LambdaQueryWrapper<>();
        memberQueryWrapper.eq(TeamMemberRelation::getTeamId, id);
        teamMemberRelationMapper.delete(memberQueryWrapper);

        // 删除团队
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeam(UpdateTeamDTO updateTeamDTO) {
        // 检查团队是否存在
        if (!lambdaQuery().eq(Team::getId, updateTeamDTO.getId()).exists()) {
            throw new BizException(StatusCodeEnum.TEAM_NOT_EXIST);
        }

        // 检查团队名称是否已存在(不包括自己)
        if (StringUtils.isNotBlank(updateTeamDTO.getTeamName()) && 
            lambdaQuery().eq(Team::getTeamName, updateTeamDTO.getTeamName())
                        .ne(Team::getId, updateTeamDTO.getId())
                        .exists()) {
            throw new BizException(StatusCodeEnum.TEAM_NAME_ALREADY_EXISTS);
        }

        // 更新团队
        Team team = BeanUtil.copyProperties(updateTeamDTO, Team.class);
        updateById(team);

        // 更新团队成员关系
        if (updateTeamDTO.getMemberIds() != null) {
            // 删除原有关系
            LambdaQueryWrapper<TeamMemberRelation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeamMemberRelation::getTeamId, updateTeamDTO.getId());
            teamMemberRelationMapper.delete(queryWrapper);
            
            // 添加新的关系
            if (!updateTeamDTO.getMemberIds().isEmpty()) {
                saveTeamMemberRelations(updateTeamDTO.getId(), updateTeamDTO.getMemberIds());
            }
        }
    }

    @Override
    public IPage<Team> listTeam(ListTeamDTO listTeamDTO) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.like(StringUtils.isNotBlank(listTeamDTO.getTeamName()), 
                          Team::getTeamName, listTeamDTO.getTeamName())
                   .like(StringUtils.isNotBlank(listTeamDTO.getLeaderName()), 
                         Team::getLeaderName, listTeamDTO.getLeaderName())
                   .eq(listTeamDTO.getDepartId() != null, 
                       Team::getDepartId, listTeamDTO.getDepartId())
                   .orderByDesc(Team::getCreateTime);
        
        // 分页查询
        return page(new Page<>(listTeamDTO.getPage(), listTeamDTO.getPageSize()), queryWrapper);
    }

    @Override
    public TeamDetailVO getTeamDetail(Long id) {
        // 获取团队基本信息
        Team team = getById(id);
        if (team == null) {
            throw new BizException(StatusCodeEnum.TEAM_NOT_EXIST);
        }
        
        // 获取团队成员关系
        LambdaQueryWrapper<TeamMemberRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamMemberRelation::getTeamId, id);
        List<TeamMemberRelation> relations = teamMemberRelationMapper.selectList(queryWrapper);
        
        // 获取所有成员信息
        List<User> members = new ArrayList<>();
        if (!relations.isEmpty()) {
            List<Long> userIds = relations.stream()
                                         .map(TeamMemberRelation::getUserId)
                                         .collect(Collectors.toList());
            LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.in(User::getId, userIds);
            members = userMapper.selectList(userQueryWrapper);
        }
        
        // 组装返回结果
        TeamDetailVO teamDetailVO = new TeamDetailVO();
        BeanUtil.copyProperties(team, teamDetailVO);
        
        // 设置部门名称
        if (team.getDepartId() != null) {
            teamDetailVO.setDepartName(departmentMapper.selectById(team.getDepartId()).getDepartName());
        }
        
        teamDetailVO.setMembers(members);
        return teamDetailVO;
    }

    @Override
    public void addTeamMember(Long teamId, Long userId) {
        // 检查团队是否存在
        if (!lambdaQuery().eq(Team::getId, teamId).exists()) {
            throw new BizException(StatusCodeEnum.TEAM_NOT_EXIST);
        }
        
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(StatusCodeEnum.USER_NOT_FOUND);
        }
        
        // 检查成员是否已在团队中
        LambdaQueryWrapper<TeamMemberRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamMemberRelation::getTeamId, teamId)
                   .eq(TeamMemberRelation::getUserId, userId);
        if (teamMemberRelationMapper.exists(queryWrapper)) {
            throw new BizException(StatusCodeEnum.TEAM_MEMBER_ALREADY_EXISTS);
        }
        
        // 添加成员关系
        TeamMemberRelation relation = new TeamMemberRelation();
        relation.setTeamId(teamId);
        relation.setUserId(userId);
        teamMemberRelationMapper.insert(relation);
    }

    @Override
    public void removeTeamMember(Long teamId, Long userId) {
        // 检查团队是否存在
        if (!lambdaQuery().eq(Team::getId, teamId).exists()) {
            throw new BizException(StatusCodeEnum.TEAM_NOT_EXIST);
        }
        
        // 检查成员关系是否存在
        LambdaQueryWrapper<TeamMemberRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamMemberRelation::getTeamId, teamId)
                   .eq(TeamMemberRelation::getUserId, userId);
        if (!teamMemberRelationMapper.exists(queryWrapper)) {
            throw new BizException(StatusCodeEnum.TEAM_MEMBER_NOT_EXIST);
        }
        
        // 删除成员关系
        teamMemberRelationMapper.delete(queryWrapper);
    }
    
    /**
     * 保存团队成员关系
     *
     * @param teamId 团队ID
     * @param memberIds 成员ID列表
     */
    private void saveTeamMemberRelations(Long teamId, List<Long> memberIds) {
        List<TeamMemberRelation> relations = new ArrayList<>();
        for (Long userId : memberIds) {
            TeamMemberRelation relation = new TeamMemberRelation();
            relation.setTeamId(teamId);
            relation.setUserId(userId);
            relations.add(relation);
        }
        for (TeamMemberRelation relation : relations) {
            teamMemberRelationMapper.insert(relation);
        }
    }
}

package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gymsys.entity.competition.Team;
import com.gymsys.entity.competition.dto.team.AddTeamDTO;
import com.gymsys.entity.competition.dto.team.ListTeamDTO;
import com.gymsys.entity.competition.dto.team.UpdateTeamDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.TeamMapper;
import com.gymsys.service.competition.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 团队表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {

    @Override
    public void addTeam(AddTeamDTO addTeamDTO) {
        save(BeanUtil.copyProperties(addTeamDTO, Team.class));
    }

    @Override
    public void deleteTeam(Long id) {
        if (lambdaQuery().eq(Team::getId, id) == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        removeById(id);
    }

    @Override
    public void updateTeam(UpdateTeamDTO updateTeamDTO) {
        lambdaUpdate().eq(Team::getId, updateTeamDTO.getId())
                .set(updateTeamDTO.getTeamName() != null, Team::getTeamName, updateTeamDTO.getTeamName())
                .set(updateTeamDTO.getLeaderName() != null, Team::getLeaderName, updateTeamDTO.getLeaderName())
                .set(updateTeamDTO.getLeaderPhone() != null, Team::getLeaderPhone, updateTeamDTO.getLeaderPhone())
                .set(updateTeamDTO.getDepartId() != null, Team::getDepartId, updateTeamDTO.getDepartId());
    }

    @Override
    public IPage<Team> listTeamDTO(ListTeamDTO listTeamDTO) {
        return page(new Page<>(listTeamDTO.getPage(), listTeamDTO.getPageSize()),
                new LambdaQueryWrapper<Team>()
                        .eq(listTeamDTO.getId() != null, Team::getId, listTeamDTO.getId())
                        .like(StringUtils.isNotBlank(listTeamDTO.getTeamName()), Team::getTeamName, listTeamDTO.getTeamName()));
    }
}

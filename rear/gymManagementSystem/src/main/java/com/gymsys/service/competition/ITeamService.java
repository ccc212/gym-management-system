package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.entity.competition.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.dto.team.AddTeamDTO;
import com.gymsys.entity.competition.dto.team.ListTeamDTO;
import com.gymsys.entity.competition.dto.team.UpdateTeamDTO;

/**
 * <p>
 * 团队表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
public interface ITeamService extends IService<Team> {

    void addTeam(AddTeamDTO addTeamDTO);

    void deleteTeam(Long id);

    void updateTeam(UpdateTeamDTO updateTeamDTO);

    IPage<Team> listTeamDTO(ListTeamDTO listTeamDTO);
}

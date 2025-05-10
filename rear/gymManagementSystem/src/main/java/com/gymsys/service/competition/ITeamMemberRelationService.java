package com.gymsys.service.competition;

import com.gymsys.entity.competition.TeamMemberRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.dto.teamMemberRelation.AddTeamMemberRelationDTO;

/**
 * <p>
 * 团队与队员关联表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
public interface ITeamMemberRelationService extends IService<TeamMemberRelation> {

    void addTeamMemberRelation(AddTeamMemberRelationDTO addTeamMemberRelationDTO);

    void deleteTeamMemberRelation(Long id);
}

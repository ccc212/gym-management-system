package com.gymsys.mapper.competition;

import com.gymsys.entity.competition.TeamMemberRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.competition.vo.TeamMemberRelationVO;

import java.util.List;

/**
 * <p>
 * 团队与队员关联表 Mapper 接口
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
public interface TeamMemberRelationMapper extends BaseMapper<TeamMemberRelation> {

    List<TeamMemberRelationVO> getTeamApplications(Long teamId, Integer status);

    List<TeamMemberRelationVO> getUserApplications(Long userId, Integer status);
}

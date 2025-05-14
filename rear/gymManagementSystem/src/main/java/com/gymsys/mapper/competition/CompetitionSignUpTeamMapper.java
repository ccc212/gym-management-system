package com.gymsys.mapper.competition;

import com.gymsys.entity.competition.CompetitionSignUpTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.competition.vo.CompetitionSignUpTeamVO;

import java.util.List;

/**
 * <p>
 * 赛事团体报名表 Mapper 接口
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface CompetitionSignUpTeamMapper extends BaseMapper<CompetitionSignUpTeam> {

    List<CompetitionSignUpTeamVO> getCompetitionSignUpTeam(Long userId);
}

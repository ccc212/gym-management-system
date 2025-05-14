package com.gymsys.mapper.competition;

import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.competition.vo.CompetitionSignUpUserVO;

import java.util.List;

/**
 * <p>
 * 赛事个人报名表 Mapper 接口
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface CompetitionSignUpUserMapper extends BaseMapper<CompetitionSignUpUser> {

    List<CompetitionSignUpUserVO> getCompetitionSignUpUser(Long userId);
}

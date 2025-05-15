package com.gymsys.mapper.competition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import com.gymsys.entity.competition.vo.CompetitionVenueRelationVO;

import java.util.List;

/**
 * <p>
 * 赛事与场地关联表 Mapper 接口
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface CompetitionVenueRelationMapper extends BaseMapper<CompetitionVenueRelation> {

    List<CompetitionVenueRelationVO> getCompetitionVenueRelation(Long competitionId);
}

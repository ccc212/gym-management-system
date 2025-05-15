package com.gymsys.mapper.competition;

import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymsys.entity.competition.vo.CompetitionEquipmentRelationVO;

import java.util.List;

/**
 * <p>
 * 赛事与器材关联表 Mapper 接口
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface CompetitionEquipmentRelationMapper extends BaseMapper<CompetitionEquipmentRelation> {

    List<CompetitionEquipmentRelationVO> getCompetitionEquipmentRelation(Long competitionId);
}

package com.gymsys.service.competition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import com.gymsys.entity.competition.dto.competitionVenueRelation.AddCompetitionVenueRelationDTO;

/**
 * <p>
 * 赛事与场地关联表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionVenueRelationService extends IService<CompetitionVenueRelation> {

    void addCompetitionVenueRelation(AddCompetitionVenueRelationDTO addCompetitionVenueRelationDTO);

    void deleteCompetitionVenueRelation(Long id);
}

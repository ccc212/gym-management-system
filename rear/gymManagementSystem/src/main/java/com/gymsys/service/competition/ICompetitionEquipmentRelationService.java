package com.gymsys.service.competition;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.AddCompetitionEquipmentRelationDTO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 赛事与器材关联表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionEquipmentRelationService extends IService<CompetitionEquipmentRelation> {

    void addCompetitionEquipmentRelation(@Param("addCompetitionEquipmentRelationDTO") AddCompetitionEquipmentRelationDTO addCompetitionEquipmentRelationDTO);

    void deleteCompetitionEquipmentRelation(Long id);
}

package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.AddCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.ListCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.UpdateCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.vo.CompetitionEquipmentRelationVO;

import java.util.List;

/**
 * <p>
 * 赛事与器材关联表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionEquipmentRelationService extends IService<CompetitionEquipmentRelation> {
    /**
     * 添加赛事器材关联
     *
     * @param addCompetitionEquipmentRelationDTO 添加赛事器材关联DTO
     */
    void addCompetitionEquipmentRelation(AddCompetitionEquipmentRelationDTO addCompetitionEquipmentRelationDTO);

    /**
     * 删除赛事器材关联
     *
     * @param id 关联ID
     */
    void deleteCompetitionEquipmentRelation(Long id);

    /**
     * 更新赛事器材关联
     *
     * @param updateCompetitionEquipmentRelationDTO 更新赛事器材关联DTO
     */
    void updateCompetitionEquipmentRelation(UpdateCompetitionEquipmentRelationDTO updateCompetitionEquipmentRelationDTO);

    /**
     * 查询赛事器材关联列表
     *
     * @param listCompetitionEquipmentRelationDTO 查询条件
     * @return 赛事器材关联列表
     */
    IPage<CompetitionEquipmentRelation> listCompetitionEquipmentRelation(ListCompetitionEquipmentRelationDTO listCompetitionEquipmentRelationDTO);
    
    /**
     * 根据赛事ID查询器材关联列表
     *
     * @param competitionId 赛事ID
     * @return 器材关联列表
     */
    List<CompetitionEquipmentRelation> listByCompetitionId(Long competitionId);

    List<CompetitionEquipmentRelationVO> getCompetitionEquipmentRelation(Long competitionId);
}

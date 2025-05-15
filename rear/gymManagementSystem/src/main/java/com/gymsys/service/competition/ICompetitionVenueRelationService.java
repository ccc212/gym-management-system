package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import com.gymsys.entity.competition.dto.competitionVenueRelation.AddCompetitionVenueRelationDTO;
import com.gymsys.entity.competition.dto.competitionVenueRelation.ListCompetitionVenueRelationDTO;
import com.gymsys.entity.competition.dto.competitionVenueRelation.UpdateCompetitionVenueRelationDTO;
import com.gymsys.entity.competition.vo.CompetitionVenueRelationVO;

import java.util.List;

/**
 * <p>
 * 赛事与场地关联表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionVenueRelationService extends IService<CompetitionVenueRelation> {
    /**
     * 添加赛事场地关联
     *
     * @param addCompetitionVenueRelationDTO 添加赛事场地关联DTO
     */
    void addCompetitionVenueRelation(AddCompetitionVenueRelationDTO addCompetitionVenueRelationDTO);

    /**
     * 删除赛事场地关联
     *
     * @param id 关联ID
     */
    void deleteCompetitionVenueRelation(Long id);

    /**
     * 更新赛事场地关联
     *
     * @param updateCompetitionVenueRelationDTO 更新赛事场地关联DTO
     */
    void updateCompetitionVenueRelation(UpdateCompetitionVenueRelationDTO updateCompetitionVenueRelationDTO);

    /**
     * 查询赛事场地关联列表
     *
     * @param listCompetitionVenueRelationDTO 查询条件
     * @return 赛事场地关联列表
     */
    IPage<CompetitionVenueRelation> listCompetitionVenueRelation(ListCompetitionVenueRelationDTO listCompetitionVenueRelationDTO);
    
    /**
     * 根据赛事ID查询场地关联列表
     *
     * @param competitionId 赛事ID
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> listByCompetitionId(Long competitionId);

    List<CompetitionVenueRelationVO> getCompetitionVenueRelation(Long competitionId);
}

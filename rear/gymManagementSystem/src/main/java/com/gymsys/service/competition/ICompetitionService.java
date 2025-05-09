package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionItem;
import com.gymsys.entity.competition.CompetitionItemRelation;
import com.gymsys.entity.competition.dto.competition.AddCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.UpdateCompetitionDTO;
import com.gymsys.entity.competition.vo.CompetitionDetailVO;

import java.util.List;

/**
 * <p>
 * 赛事表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionService extends IService<Competition> {

    /**
     * 添加赛事
     *
     * @param addCompetitionDTO 添加赛事DTO
     */
    void addCompetition(AddCompetitionDTO addCompetitionDTO);

    /**
     * 删除赛事
     *
     * @param id 赛事ID
     */
    void deleteCompetition(Long id);

    /**
     * 更新赛事
     *
     * @param updateCompetitionDTO 更新赛事DTO
     */
    void updateCompetition(UpdateCompetitionDTO updateCompetitionDTO);

    /**
     * 查询赛事列表
     *
     * @param listCompetitionDTO 查询条件
     * @return 赛事列表
     */
    IPage<Competition> listCompetition(ListCompetitionDTO listCompetitionDTO);

    CompetitionDetailVO getDetail(Long id);

    List<CompetitionItem> listItem();

    List<CompetitionItemRelation> listItemByCompetitionId(Long competitionId);
}

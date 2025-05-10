package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.CompetitionItem;
import com.gymsys.entity.competition.dto.competitionItem.AddCompetitionItemDTO;
import com.gymsys.entity.competition.dto.competitionItem.ListCompetitionItemDTO;
import com.gymsys.entity.competition.dto.competitionItem.UpdateCompetitionItemDTO;

/**
 * <p>
 * 赛事项目表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionItemService extends IService<CompetitionItem> {

    /**
     * 添加赛事项目
     *
     * @param addCompetitionItemDTO 添加赛事项目DTO
     */
    void addCompetitionItem(AddCompetitionItemDTO addCompetitionItemDTO);

    /**
     * 删除赛事项目
     *
     * @param id 赛事项目ID
     */
    void deleteCompetitionItem(Long id);

    /**
     * 更新赛事项目
     *
     * @param updateCompetitionItemDTO 更新赛事项目DTO
     */
    void updateCompetitionItem(UpdateCompetitionItemDTO updateCompetitionItemDTO);

    /**
     * 查询赛事项目列表
     *
     * @param listCompetitionItemDTO 查询条件
     * @return 赛事项目列表
     */
    IPage<CompetitionItem> listCompetitionItem(ListCompetitionItemDTO listCompetitionItemDTO);
}

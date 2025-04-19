package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.dto.competition.AddCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.UpdateCompetitionDTO;

/**
 * <p>
 * 赛事表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionService extends IService<Competition> {

    void addCompetition(AddCompetitionDTO addCompetitionDTO);

    void deleteCompetition(Long id);

    void updateCompetition(UpdateCompetitionDTO updateCompetitionDTO);

    IPage<Competition> listCompetition(ListCompetitionDTO listCompetitionDTO);
}

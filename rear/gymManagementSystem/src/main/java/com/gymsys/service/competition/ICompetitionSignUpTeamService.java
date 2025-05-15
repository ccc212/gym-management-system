package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.entity.competition.CompetitionSignUpTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.AddCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.ListCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.UpdateCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.vo.CompetitionSignUpTeamVO;

import java.util.List;

/**
 * <p>
 * 赛事团体报名表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionSignUpTeamService extends IService<CompetitionSignUpTeam> {

    void addCompetitionSignUpTeam(AddCompetitionSignUpTeamDTO addCompetitionSignUpTeamDTO);

    void deleteCompetitionSignUpTeam(Long id);

    void updateCompetitionSignUpTeam(UpdateCompetitionSignUpTeamDTO updateCompetitionSignUpTeamDTO);

    IPage<CompetitionSignUpTeam> listCompetitionSignUpTeamDTO(ListCompetitionSignUpTeamDTO listCompetitionSignUpTeamDTO);

    List<CompetitionSignUpTeamVO> getCompetitionSignUpTeam(Long userId);
}

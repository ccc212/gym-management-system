package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.dto.competitionSignUpUser.AddCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.ListCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.UpdateCompetitionSignUpUserDTO;

/**
 * <p>
 * 赛事个人报名表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionSignUpUserService extends IService<CompetitionSignUpUser> {

    void addCompetitionSignUpUser(AddCompetitionSignUpUserDTO addCompetitionSignUpUserDTO);

    void deleteCompetitionSignUpUser(Long id);

    void updateCompetitionSignUpUser(UpdateCompetitionSignUpUserDTO updateCompetitionSignUpUserDTO);

    IPage<CompetitionSignUpUser> listCompetitionSignUpUserDTO(ListCompetitionSignUpUserDTO listCompetitionSignUpUserDTO);
}

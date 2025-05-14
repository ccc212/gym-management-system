package com.gymsys.controller.competition;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.CompetitionSignUpTeam;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.AddCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.ListCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.UpdateCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.vo.CompetitionSignUpTeamVO;
import com.gymsys.entity.competition.vo.CompetitionSignUpUserVO;
import com.gymsys.service.competition.ICompetitionSignUpTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 赛事团体报名表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/competition-sign-up-team")
@RequiredArgsConstructor
public class CompetitionSignUpTeamController {

    private final ICompetitionSignUpTeamService competitionSignUpTeamService;

    @PostMapping("/add")
    public Result<?> addCompetitionSignUpTeam(@RequestBody @Valid AddCompetitionSignUpTeamDTO addCompetitionSignUpTeamDTO) {
        competitionSignUpTeamService.addCompetitionSignUpTeam(addCompetitionSignUpTeamDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCompetitionSignUpTeam(@PathVariable Long id) {
        competitionSignUpTeamService.deleteCompetitionSignUpTeam(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @PutMapping("/update")
    public Result<?> updateCompetitionSignUpTeam(@RequestBody @Valid UpdateCompetitionSignUpTeamDTO updateCompetitionSignUpTeamDTO) {
        competitionSignUpTeamService.updateCompetitionSignUpTeam(updateCompetitionSignUpTeamDTO);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<IPage<CompetitionSignUpTeam>> listCompetition(@Valid ListCompetitionSignUpTeamDTO listCompetitionSignUpTeamDTO) {
        return Result.success(competitionSignUpTeamService.listCompetitionSignUpTeamDTO(listCompetitionSignUpTeamDTO));
    }

    @GetMapping("/getByUserId/{userId}")
    public Result<List<CompetitionSignUpTeamVO>> getCompetitionSignUpTeam(@PathVariable Long userId) {
        return Result.success(competitionSignUpTeamService.getCompetitionSignUpTeam(userId));
    }

}

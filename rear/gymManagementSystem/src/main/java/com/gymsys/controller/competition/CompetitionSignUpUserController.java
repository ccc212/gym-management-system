package com.gymsys.controller.competition;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.AddCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.ListCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.UpdateCompetitionSignUpUserDTO;
import com.gymsys.service.competition.ICompetitionSignUpUserService;
import com.gymsys.service.competition.ICompetitionVenueRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 赛事个人报名表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/competition-sign-up-user")
@RequiredArgsConstructor
public class CompetitionSignUpUserController {

    private final ICompetitionSignUpUserService competitionSignUpUserService;

    @PostMapping("/add")
    public Result<?> addCompetitionSignUpUser(@RequestBody @Valid AddCompetitionSignUpUserDTO addCompetitionSignUpUserDTO) {
        competitionSignUpUserService.addCompetitionSignUpUser(addCompetitionSignUpUserDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @PostMapping("/delete/{id}")
    public Result<?> deleteCompetitionSignUpUser(@PathVariable Long id) {
        competitionSignUpUserService.deleteCompetitionSignUpUser(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @PutMapping("/update")
    public Result<?> updateCompetitionSignUpUser(@RequestBody @Valid UpdateCompetitionSignUpUserDTO updateCompetitionSignUpUserDTO) {
        competitionSignUpUserService.updateCompetitionSignUpUser(updateCompetitionSignUpUserDTO);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<IPage<CompetitionSignUpUser>> listCompetition(@Valid ListCompetitionSignUpUserDTO listCompetitionSignUpUserDTO) {
        return Result.success(competitionSignUpUserService.listCompetitionSignUpUserDTO(listCompetitionSignUpUserDTO));
    }

}

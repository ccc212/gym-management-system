package com.gymsys.controller.competition;


import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.dto.competition.AddCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.UpdateCompetitionDTO;
import com.gymsys.service.competition.ICompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 赛事表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final ICompetitionService competitionService;

    @PostMapping("/add")
    public Result<?> addCompetition(@RequestBody @Valid AddCompetitionDTO addCompetitionDTO) {
        competitionService.addCompetition(addCompetitionDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCompetition(@PathVariable Long id) {
        competitionService.deleteCompetition(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @PutMapping("/update")
    public Result<?> updateCompetition(@RequestBody @Valid UpdateCompetitionDTO updateCompetitionDTO) {
        competitionService.updateCompetition(updateCompetitionDTO);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<?> listCompetition(@Valid ListCompetitionDTO listCompetitionDTO) {
        return Result.success(competitionService.listCompetition(listCompetitionDTO));
    }
}

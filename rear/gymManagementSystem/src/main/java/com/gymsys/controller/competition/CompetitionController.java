package com.gymsys.controller.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionItem;
import com.gymsys.entity.competition.CompetitionItemRelation;
import com.gymsys.entity.competition.dto.competition.AddCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.ListCompetitionDTO;
import com.gymsys.entity.competition.dto.competition.UpdateCompetitionDTO;
import com.gymsys.entity.competition.vo.CompetitionDetailVO;
import com.gymsys.service.competition.ICompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public Result<IPage<Competition>> listCompetition(@Valid ListCompetitionDTO listCompetitionDTO) {
        return Result.success(competitionService.listCompetition(listCompetitionDTO));
    }

    @GetMapping("/get/{id}")
    public Result<Competition> getCompetition(@PathVariable Long id) {
        return Result.success(competitionService.getById(id));
    }

    @GetMapping("/getDetail/{id}")
    public Result<CompetitionDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(competitionService.getDetail(id));
    }

    @GetMapping("/listItem")
    public Result<List<CompetitionItem>> listItem() {
        return Result.success(competitionService.listItem());
    }

    @GetMapping("/listItemByCompetitionId/{competitionId}")
    public Result<List<CompetitionItemRelation>> listItemByCompetitionId(@PathVariable Long competitionId) {
        return Result.success(competitionService.listItemByCompetitionId(competitionId));
    }
}

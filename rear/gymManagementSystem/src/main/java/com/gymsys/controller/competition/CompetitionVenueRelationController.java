package com.gymsys.controller.competition;


import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.dto.competitionVenueRelation.AddCompetitionVenueRelationDTO;
import com.gymsys.service.competition.ICompetitionVenueRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 赛事与场地关联表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/competition-venue-relation")
@RequiredArgsConstructor
public class CompetitionVenueRelationController {

    private final ICompetitionVenueRelationService competitionVenueRelationService;

    @PostMapping("/add")
    public Result<?> addCompetitionVenueRelation(@RequestBody @Valid AddCompetitionVenueRelationDTO addCompetitionVenueRelationDTO) {
        competitionVenueRelationService.addCompetitionVenueRelation(addCompetitionVenueRelationDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCompetitionVenueRelation(@PathVariable Long id) {
        competitionVenueRelationService.deleteCompetitionVenueRelation(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

}

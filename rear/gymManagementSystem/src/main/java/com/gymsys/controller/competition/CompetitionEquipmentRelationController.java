package com.gymsys.controller.competition;


import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.AddCompetitionEquipmentRelationDTO;
import com.gymsys.service.competition.ICompetitionEquipmentRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 赛事与器材关联表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/competition-equipment-relation")
@RequiredArgsConstructor
public class CompetitionEquipmentRelationController {

    private final ICompetitionEquipmentRelationService competitionEquipmentRelationService;

    @PostMapping("/add")
    public Result<?> addCompetitionEquipmentRelation(@RequestBody AddCompetitionEquipmentRelationDTO addCompetitionEquipmentRelationDTO) {
        competitionEquipmentRelationService.addCompetitionEquipmentRelation(addCompetitionEquipmentRelationDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCompetitionEquipmentRelation(@PathVariable Long id) {
        competitionEquipmentRelationService.deleteCompetitionEquipmentRelation(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

}

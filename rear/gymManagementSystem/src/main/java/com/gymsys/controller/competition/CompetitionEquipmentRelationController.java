package com.gymsys.controller.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.AddCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.ListCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.UpdateCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.vo.CompetitionEquipmentRelationVO;
import com.gymsys.service.competition.ICompetitionEquipmentRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public Result<?> addCompetitionEquipmentRelation(@RequestBody @Valid AddCompetitionEquipmentRelationDTO addCompetitionEquipmentRelationDTO) {
        competitionEquipmentRelationService.addCompetitionEquipmentRelation(addCompetitionEquipmentRelationDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCompetitionEquipmentRelation(@PathVariable Long id) {
        competitionEquipmentRelationService.deleteCompetitionEquipmentRelation(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @PutMapping("/update")
    public Result<?> updateCompetitionEquipmentRelation(@RequestBody @Valid UpdateCompetitionEquipmentRelationDTO updateCompetitionEquipmentRelationDTO) {
        competitionEquipmentRelationService.updateCompetitionEquipmentRelation(updateCompetitionEquipmentRelationDTO);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<IPage<CompetitionEquipmentRelation>> listCompetitionEquipmentRelation(@Valid ListCompetitionEquipmentRelationDTO listCompetitionEquipmentRelationDTO) {
        return Result.success(competitionEquipmentRelationService.listCompetitionEquipmentRelation(listCompetitionEquipmentRelationDTO));
    }

    @GetMapping("/getByCompetitionId/{competitionId}")
    public Result<List<CompetitionEquipmentRelationVO>> getCompetitionEquipmentRelation(@PathVariable Long competitionId) {
        return Result.success(competitionEquipmentRelationService.getCompetitionEquipmentRelation(competitionId));
    }
    
    @GetMapping("/listByCompetitionId/{competitionId}")
    public Result<List<CompetitionEquipmentRelation>> listByCompetitionId(@PathVariable Long competitionId) {
        return Result.success(competitionEquipmentRelationService.listByCompetitionId(competitionId));
    }
}

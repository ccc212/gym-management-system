package com.gymsys.controller.competition;


import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.dto.teamMemberRelation.AddTeamMemberRelationDTO;
import com.gymsys.service.competition.ITeamMemberRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 团队与队员关联表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@RestController
@RequestMapping("/teamMemberRelation-member-relation")
@RequiredArgsConstructor
public class TeamMemberRelationController {

    private final ITeamMemberRelationService teamMemberRelationService;

    @PostMapping("/add")
    public Result<?> addTeamMemberRelation(@RequestBody @Valid AddTeamMemberRelationDTO addTeamMemberRelationDTO) {
        teamMemberRelationService.addTeamMemberRelation(addTeamMemberRelationDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @PostMapping("/delete/{id}")
    public Result<?> deleteTeamMemberRelation(@PathVariable Long id) {
        teamMemberRelationService.deleteTeamMemberRelation(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

}

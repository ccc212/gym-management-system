package com.gymsys.controller.competition;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.TeamMemberRelation;
import com.gymsys.entity.competition.dto.teamMemberRelation.AddTeamMemberRelationDTO;
import com.gymsys.entity.competition.dto.teamMemberRelation.ListTeamMemberRelationDTO;
import com.gymsys.entity.competition.vo.TeamMemberRelationVO;
import com.gymsys.enums.TeamMemberStatusEnum;
import com.gymsys.service.competition.ITeamMemberRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    /**
     * 添加团队成员关联
     */
    @PostMapping("/add")
    public Result<?> addTeamMemberRelation(@RequestBody @Valid AddTeamMemberRelationDTO addTeamMemberRelationDTO) {
        teamMemberRelationService.addTeamMemberRelation(addTeamMemberRelationDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    /**
     * 删除团队成员关联
     */
    @PostMapping("/delete/{id}")
    public Result<?> deleteTeamMemberRelation(@PathVariable Long id) {
        teamMemberRelationService.deleteTeamMemberRelation(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<IPage<TeamMemberRelation>> listTeam(@Valid ListTeamMemberRelationDTO listTeamMemberRelationDTO) {
        return Result.success(teamMemberRelationService.listTeamMemberRelation(listTeamMemberRelationDTO));
    }

    /**
     * 获取用户的申请记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<TeamMemberRelationVO>> getUserApplications(@PathVariable Long userId,
                                                                  @RequestParam(required = false) Integer status) {
        List<TeamMemberRelationVO> applications = teamMemberRelationService.getUserApplications(userId, status);
        return Result.success(applications);
    }

    /**
     * 获取团队的申请记录
     */
    @GetMapping("/team/{teamId}")
    public Result<List<TeamMemberRelationVO>> getTeamApplications(@PathVariable Long teamId,
                                                                  @RequestParam(required = false) Integer status) {
        List<TeamMemberRelationVO> applications = teamMemberRelationService.getTeamApplications(teamId, status);
        return Result.success(applications);
    }

    /**
     * 更新申请状态
     */
    @PostMapping("/status/{id}")
    public Result<?> updateApplicationStatus(@PathVariable Long id, @RequestParam Integer status) {
        teamMemberRelationService.updateApplicationStatus(id, status);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    /**
     * 批准申请
     */
    @PostMapping("/approve/{id}")
    public Result<?> approveApplication(@PathVariable Long id) {
        teamMemberRelationService.updateApplicationStatus(id, TeamMemberStatusEnum.APPROVED.getCode());
        return Result.success("已批准申请");
    }

    /**
     * 拒绝申请
     */
    @PostMapping("/reject/{id}")
    public Result<?> rejectApplication(@PathVariable Long id) {
        teamMemberRelationService.updateApplicationStatus(id, TeamMemberStatusEnum.REJECTED.getCode());
        return Result.success("已拒绝申请");
    }

    /**
     * 检查用户是否已加入团队
     */
    @GetMapping("/check")
    public Result<Boolean> isUserInTeam(@RequestParam Long teamId, @RequestParam Long userId) {
        boolean isInTeam = teamMemberRelationService.isUserInTeam(teamId, userId);
        return Result.success(isInTeam);
    }
}

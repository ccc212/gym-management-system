package com.gymsys.controller.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gymsys.constant.MessageConstant;
import com.gymsys.entity.Result;
import com.gymsys.entity.competition.Team;
import com.gymsys.entity.competition.dto.team.AddTeamDTO;
import com.gymsys.entity.competition.dto.team.ListTeamDTO;
import com.gymsys.entity.competition.dto.team.UpdateTeamDTO;
import com.gymsys.entity.competition.vo.TeamDetailVO;
import com.gymsys.service.competition.ITeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 团队表 前端控制器
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final ITeamService teamService;

    @PostMapping("/add")
    public Result<?> addTeam(@RequestBody @Valid AddTeamDTO addTeamDTO) {
        teamService.addTeam(addTeamDTO);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }

    @PutMapping("/update")
    public Result<?> updateTeam(@RequestBody @Valid UpdateTeamDTO updateTeamDTO) {
        teamService.updateTeam(updateTeamDTO);
        return Result.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/list")
    public Result<IPage<Team>> listTeam(@Valid ListTeamDTO listTeamDTO) {
        return Result.success(teamService.listTeam(listTeamDTO));
    }

    @GetMapping("/get/{id}")
    public Result<Team> getTeam(@PathVariable Long id) {
        return Result.success(teamService.getById(id));
    }
    
    @GetMapping("/detail/{id}")
    public Result<TeamDetailVO> getTeamDetail(@PathVariable Long id) {
        return Result.success(teamService.getTeamDetail(id));
    }
    
    @GetMapping("/listAll")
    public Result<List<Team>> listAllTeams() {
        return Result.success(teamService.list());
    }
    
    @PostMapping("/addMember")
    public Result<?> addTeamMember(@RequestParam Long teamId, @RequestParam Long userId) {
        teamService.addTeamMember(teamId, userId);
        return Result.success(MessageConstant.ADD_SUCCESS);
    }
    
    @PostMapping("/removeMember")
    public Result<?> removeTeamMember(@RequestParam Long teamId, @RequestParam Long userId) {
        teamService.removeTeamMember(teamId, userId);
        return Result.success(MessageConstant.DELETE_SUCCESS);
    }
}

package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.Team;
import com.gymsys.entity.competition.dto.team.AddTeamDTO;
import com.gymsys.entity.competition.dto.team.ListTeamDTO;
import com.gymsys.entity.competition.dto.team.UpdateTeamDTO;
import com.gymsys.entity.competition.vo.TeamDetailVO;

/**
 * <p>
 * 团队表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
public interface ITeamService extends IService<Team> {

    /**
     * 添加团队
     *
     * @param addTeamDTO 添加团队DTO
     */
    Long addTeam(AddTeamDTO addTeamDTO);

    /**
     * 删除团队
     *
     * @param id 团队ID
     */
    void deleteTeam(Long id);

    /**
     * 更新团队
     *
     * @param updateTeamDTO 更新团队DTO
     */
    void updateTeam(UpdateTeamDTO updateTeamDTO);

    /**
     * 查询团队列表
     *
     * @param listTeamDTO 查询条件
     * @return 团队列表
     */
    IPage<Team> listTeam(ListTeamDTO listTeamDTO);
    
    /**
     * 获取团队详情
     *
     * @param id 团队ID
     * @return 团队详情
     */
    TeamDetailVO getTeamDetail(Long id);
    
    /**
     * 添加团队成员
     *
     * @param teamId 团队ID
     * @param userId 用户ID
     */
    void addTeamMember(Long teamId, Long userId);
    
    /**
     * 移除团队成员
     *
     * @param teamId 团队ID
     * @param userId 用户ID
     */
    void removeTeamMember(Long teamId, Long userId);
}

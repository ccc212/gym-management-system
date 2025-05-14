package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.TeamMemberRelation;
import com.gymsys.entity.competition.dto.teamMemberRelation.AddTeamMemberRelationDTO;
import com.gymsys.entity.competition.dto.teamMemberRelation.ListTeamMemberRelationDTO;
import com.gymsys.entity.competition.vo.TeamMemberRelationVO;

import java.util.List;

/**
 * <p>
 * 团队与队员关联表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
public interface ITeamMemberRelationService extends IService<TeamMemberRelation> {

    void addTeamMemberRelation(AddTeamMemberRelationDTO addTeamMemberRelationDTO);

    void deleteTeamMemberRelation(Long id);

    /**
     * 获取用户的申请记录
     *
     * @param userId 用户ID
     * @return 申请记录列表
     */
    List<TeamMemberRelationVO> getUserApplications(Long userId, Integer status);

    /**
     * 获取团队的申请记录
     *
     * @param teamId 团队ID
     * @param status 状态，null表示所有状态
     * @return 申请记录列表
     */
    List<TeamMemberRelationVO> getTeamApplications(Long teamId, Integer status);

    /**
     * 更新申请状态
     *
     * @param id     申请记录ID
     * @param status 新状态
     */
    void updateApplicationStatus(Long id, Integer status);

    /**
     * 检查用户是否已加入团队
     *
     * @param teamId 团队ID
     * @param userId 用户ID
     * @return 是否已加入
     */
    boolean isUserInTeam(Long teamId, Long userId);

    /**
     * 列出团队的成员
     *
     * @param teamId 团队ID
     * @return 成员列表
     */
    IPage<TeamMemberRelation> listTeamMemberRelation(ListTeamMemberRelationDTO listTeamMemberRelationDTO);
}

package com.gymsys.service.competition;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.gymsys.entity.competition.dto.competitionSignUpUser.AddCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.ListCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.UpdateCompetitionSignUpUserDTO;

/**
 * <p>
 * 赛事个人报名表 服务类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
public interface ICompetitionSignUpUserService extends IService<CompetitionSignUpUser> {
    /**
     * 添加赛事个人报名
     *
     * @param addCompetitionSignUpUserDTO 添加个人报名DTO
     */
    void addCompetitionSignUpUser(AddCompetitionSignUpUserDTO addCompetitionSignUpUserDTO);

    /**
     * 删除赛事个人报名
     *
     * @param id 个人报名ID
     */
    void deleteCompetitionSignUpUser(Long id);

    /**
     * 更新赛事个人报名
     *
     * @param updateCompetitionSignUpUserDTO 更新个人报名DTO
     */
    void updateCompetitionSignUpUser(UpdateCompetitionSignUpUserDTO updateCompetitionSignUpUserDTO);

    /**
     * 查询赛事个人报名列表
     *
     * @param listCompetitionSignUpUserDTO 查询条件
     * @return 赛事个人报名列表
     */
    IPage<CompetitionSignUpUser> listCompetitionSignUpUser(ListCompetitionSignUpUserDTO listCompetitionSignUpUserDTO);
    
    /**
     * 批准报名
     *
     * @param id 报名ID
     */
    void approveSignUp(Long id);
    
    /**
     * 拒绝报名
     *
     * @param id 报名ID
     * @param rejectReason 拒绝原因
     */
    void rejectSignUp(Long id, String rejectReason);
}

package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.gymsys.entity.competition.TeamMemberRelation;
import com.gymsys.entity.competition.dto.teamMemberRelation.AddTeamMemberRelationDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.TeamMemberRelationMapper;
import com.gymsys.service.competition.ITeamMemberRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 团队与队员关联表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@Service
public class TeamMemberRelationServiceImpl extends ServiceImpl<TeamMemberRelationMapper, TeamMemberRelation> implements ITeamMemberRelationService {

    @Override
    public void addTeamMemberRelation(AddTeamMemberRelationDTO addTeamMemberRelationDTO) {
        save(BeanUtil.copyProperties(addTeamMemberRelationDTO, TeamMemberRelation.class));
    }

    @Override
    public void deleteTeamMemberRelation(Long id) {
        if (lambdaQuery().eq(TeamMemberRelation::getId, id) == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        removeById(id);
    }
}

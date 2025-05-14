package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.CompetitionSignUpTeam;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.AddCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.ListCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.UpdateCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.vo.CompetitionSignUpTeamVO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionSignUpTeamMapper;
import com.gymsys.service.competition.ICompetitionSignUpTeamService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 赛事团体报名表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
public class CompetitionSignUpTeamServiceImpl extends ServiceImpl<CompetitionSignUpTeamMapper, CompetitionSignUpTeam> implements ICompetitionSignUpTeamService {

    private final CompetitionSignUpTeamMapper competitionSignUpTeamMapper;

    @Override
    public void addCompetitionSignUpTeam(AddCompetitionSignUpTeamDTO addCompetitionSignUpTeamDTO) {
        save(BeanUtil.copyProperties(addCompetitionSignUpTeamDTO, CompetitionSignUpTeam.class));
    }

    @Override
    public void deleteCompetitionSignUpTeam(Long id) {
        if (lambdaQuery().eq(CompetitionSignUpTeam::getId, id) == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_TEAM_NOT_EXIST);
        }

        removeById(id);
    }

    @Override
    public void updateCompetitionSignUpTeam(UpdateCompetitionSignUpTeamDTO updateCompetitionSignUpTeamDTO) {
        lambdaUpdate().eq(CompetitionSignUpTeam::getId, updateCompetitionSignUpTeamDTO.getId())
                .set(updateCompetitionSignUpTeamDTO.getTeamId() != null, CompetitionSignUpTeam::getTeamId, updateCompetitionSignUpTeamDTO.getTeamId())
                .set(updateCompetitionSignUpTeamDTO.getCompetitionId() != null, CompetitionSignUpTeam::getCompetitionId, updateCompetitionSignUpTeamDTO.getCompetitionId())
                .set(updateCompetitionSignUpTeamDTO.getTeamName() != null, CompetitionSignUpTeam::getTeamName, updateCompetitionSignUpTeamDTO.getTeamName())
                .set(updateCompetitionSignUpTeamDTO.getLeaderName() != null, CompetitionSignUpTeam::getLeaderName, updateCompetitionSignUpTeamDTO.getLeaderName())
                .set(updateCompetitionSignUpTeamDTO.getLeaderPhone() != null, CompetitionSignUpTeam::getLeaderPhone, updateCompetitionSignUpTeamDTO.getLeaderPhone())
                .set(updateCompetitionSignUpTeamDTO.getDepartId() != null, CompetitionSignUpTeam::getDepartId, updateCompetitionSignUpTeamDTO.getDepartId())
                .set(updateCompetitionSignUpTeamDTO.getCompetitionItemId() != null, CompetitionSignUpTeam::getCompetitionItemId, updateCompetitionSignUpTeamDTO.getCompetitionItemId())
                .set(updateCompetitionSignUpTeamDTO.getRemark() != null, CompetitionSignUpTeam::getRemark, updateCompetitionSignUpTeamDTO.getRemark())
                .set(updateCompetitionSignUpTeamDTO.getStatus() != null, CompetitionSignUpTeam::getStatus, updateCompetitionSignUpTeamDTO.getStatus())
                .set(updateCompetitionSignUpTeamDTO.getRejectReason() != null, CompetitionSignUpTeam::getRejectReason, updateCompetitionSignUpTeamDTO.getRejectReason());
    }

    @Override
    public IPage<CompetitionSignUpTeam> listCompetitionSignUpTeamDTO(ListCompetitionSignUpTeamDTO listCompetitionSignUpTeamDTO) {
        return page(new Page<>(listCompetitionSignUpTeamDTO.getPage(), listCompetitionSignUpTeamDTO.getPageSize()),
                new LambdaQueryWrapper<CompetitionSignUpTeam>()
                        .eq(listCompetitionSignUpTeamDTO.getCompetitionId() != null, CompetitionSignUpTeam::getCompetitionId, listCompetitionSignUpTeamDTO.getCompetitionId())
                        .like(StringUtils.isNotBlank(listCompetitionSignUpTeamDTO.getTeamName()), CompetitionSignUpTeam::getTeamName, listCompetitionSignUpTeamDTO.getTeamName())
                        .eq(listCompetitionSignUpTeamDTO.getStatus() != null, CompetitionSignUpTeam::getStatus, listCompetitionSignUpTeamDTO.getStatus()));
    }

    @Override
    public List<CompetitionSignUpTeamVO> getCompetitionSignUpTeam(Long userId) {
        return competitionSignUpTeamMapper.getCompetitionSignUpTeam(userId);
    }
}

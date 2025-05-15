package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionSignUpTeam;
import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.AddCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.ListCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.dto.competitionSignUpTeam.UpdateCompetitionSignUpTeamDTO;
import com.gymsys.entity.competition.vo.CompetitionSignUpTeamVO;
import com.gymsys.enums.CompetitionStatusEnum;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionMapper;
import com.gymsys.mapper.competition.CompetitionSignUpTeamMapper;
import com.gymsys.service.competition.ICompetitionSignUpTeamService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    private final CompetitionMapper competitionMapper;
    private final CompetitionSignUpTeamMapper competitionSignUpTeamMapper;

    @Override
    public void addCompetitionSignUpTeam(AddCompetitionSignUpTeamDTO addCompetitionSignUpTeamDTO) {
        // 检查赛事是否存在
        Competition competition = competitionMapper.selectById(addCompetitionSignUpTeamDTO.getCompetitionId());
        if (competition == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 检查赛事是否已截止报名
        if (Objects.equals(CompetitionStatusEnum.getStatusByTime(competition.getSignUpDeadline(), competition.getStartTime(), competition.getEndTime()),
                CompetitionStatusEnum.SIGN_UP_DEADLINE.getCode())) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_DEADLINE);
        }

        // 检查是否已经满员
        if (competition.getSignUpNum() >= competition.getMaxSignUpNum() && competition.getMaxSignUpNum() > 0) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_FULL);
        }

        // 检查团队是否已报名该赛事
        LambdaQueryWrapper<CompetitionSignUpTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionSignUpTeam::getCompetitionId, addCompetitionSignUpTeamDTO.getCompetitionId())
                .eq(CompetitionSignUpTeam::getTeamId, addCompetitionSignUpTeamDTO.getTeamId());
        if (baseMapper.exists(queryWrapper)) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_ALREADY_EXISTS);
        }

        // 保存报名信息
        CompetitionSignUpTeam competitionSignUpUser = BeanUtil.copyProperties(addCompetitionSignUpTeamDTO, CompetitionSignUpTeam.class);
        save(competitionSignUpUser);

        // 更新赛事已报人数
        competition.setSignUpNum(competition.getSignUpNum() + 1);
        competitionMapper.updateById(competition);
    }

    @Override
    public void deleteCompetitionSignUpTeam(Long id) {
        // 检查报名信息是否存在
        CompetitionSignUpTeam competitionSignUpTeam = getById(id);
        if (competitionSignUpTeam == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        // 检查赛事状态
        Competition competition = competitionMapper.selectById(competitionSignUpTeam.getCompetitionId());
        if (competition == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 删除报名信息
        removeById(id);

        // 更新赛事已报人数
//        if (competitionSignUpTeam.getStatus() == 1) { // 只有当报名状态为成功时才需要减少已报人数
            competition.setSignUpNum(competition.getSignUpNum() - 1);
            competitionMapper.updateById(competition);
//        }
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
        List<CompetitionSignUpTeamVO> teamVOList = competitionSignUpTeamMapper.getCompetitionSignUpTeam(userId);

        // 动态计算每个团队报名的比赛状态
        for (CompetitionSignUpTeamVO vo : teamVOList) {
            vo.setCompetitionStatus(CompetitionStatusEnum.getStatusByTime(
                    vo.getSignUpDeadline(),
                    vo.getStartTime(),
                    vo.getEndTime()
            ));
        }

        return teamVOList;
    }
}

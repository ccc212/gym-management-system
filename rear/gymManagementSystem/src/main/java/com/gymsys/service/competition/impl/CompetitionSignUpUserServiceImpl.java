package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.gymsys.entity.competition.dto.competitionSignUpUser.AddCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.ListCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.UpdateCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.vo.CompetitionSignUpUserVO;
import com.gymsys.enums.CompetitionStatusEnum;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionMapper;
import com.gymsys.mapper.competition.CompetitionSignUpUserMapper;
import com.gymsys.service.competition.ICompetitionSignUpUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 赛事个人报名表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
public class CompetitionSignUpUserServiceImpl extends ServiceImpl<CompetitionSignUpUserMapper, CompetitionSignUpUser> implements ICompetitionSignUpUserService {

    private final CompetitionMapper competitionMapper;
    private final CompetitionSignUpUserMapper competitionSignUpUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCompetitionSignUpUser(AddCompetitionSignUpUserDTO addCompetitionSignUpUserDTO) {
        // 检查赛事是否存在
        Competition competition = competitionMapper.selectById(addCompetitionSignUpUserDTO.getCompetitionId());
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

        // 检查用户是否已报名该赛事
        LambdaQueryWrapper<CompetitionSignUpUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionSignUpUser::getCompetitionId, addCompetitionSignUpUserDTO.getCompetitionId())
                   .eq(CompetitionSignUpUser::getUserId, addCompetitionSignUpUserDTO.getUserId());
        if (baseMapper.exists(queryWrapper)) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_ALREADY_EXISTS);
        }

        // 保存报名信息
        CompetitionSignUpUser competitionSignUpUser = BeanUtil.copyProperties(addCompetitionSignUpUserDTO, CompetitionSignUpUser.class);
        // 设置初始化状态：报名中
        competitionSignUpUser.setStatus(0);
        save(competitionSignUpUser);

        // 更新赛事已报人数
        competition.setSignUpNum(competition.getSignUpNum() + 1);
        competitionMapper.updateById(competition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompetitionSignUpUser(Long id) {
        // 检查报名信息是否存在
        CompetitionSignUpUser competitionSignUpUser = getById(id);
        if (competitionSignUpUser == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        // 检查赛事状态
        Competition competition = competitionMapper.selectById(competitionSignUpUser.getCompetitionId());
        if (competition == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 删除报名信息
        removeById(id);

        // 更新赛事已报人数
        if (competitionSignUpUser.getStatus() == 1) { // 只有当报名状态为成功时才需要减少已报人数
            competition.setSignUpNum(competition.getSignUpNum() - 1);
            competitionMapper.updateById(competition);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompetitionSignUpUser(UpdateCompetitionSignUpUserDTO updateCompetitionSignUpUserDTO) {
        // 检查报名信息是否存在
        CompetitionSignUpUser competitionSignUpUser = getById(updateCompetitionSignUpUserDTO.getId());
        if (competitionSignUpUser == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        // 更新报名信息
        CompetitionSignUpUser updateEntity = BeanUtil.copyProperties(updateCompetitionSignUpUserDTO, CompetitionSignUpUser.class);
        updateById(updateEntity);
    }

    @Override
    public IPage<CompetitionSignUpUser> listCompetitionSignUpUser(ListCompetitionSignUpUserDTO listCompetitionSignUpUserDTO) {
        LambdaQueryWrapper<CompetitionSignUpUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.eq(listCompetitionSignUpUserDTO.getCompetitionId() != null, 
                        CompetitionSignUpUser::getCompetitionId, listCompetitionSignUpUserDTO.getCompetitionId())
                   .eq(listCompetitionSignUpUserDTO.getUserId() != null, 
                        CompetitionSignUpUser::getUserId, listCompetitionSignUpUserDTO.getUserId())
                   .like(StringUtils.isNotBlank(listCompetitionSignUpUserDTO.getName()), 
                          CompetitionSignUpUser::getName, listCompetitionSignUpUserDTO.getName())
                   .eq(listCompetitionSignUpUserDTO.getCompetitionItemId() != null, 
                        CompetitionSignUpUser::getCompetitionItemId, listCompetitionSignUpUserDTO.getCompetitionItemId())
                   .eq(listCompetitionSignUpUserDTO.getStatus() != null, 
                        CompetitionSignUpUser::getStatus, listCompetitionSignUpUserDTO.getStatus())
                   .eq(listCompetitionSignUpUserDTO.getDepartId() != null, 
                        CompetitionSignUpUser::getDepartId, listCompetitionSignUpUserDTO.getDepartId());
        
        // 分页查询
        return page(new Page<>(listCompetitionSignUpUserDTO.getPage(), listCompetitionSignUpUserDTO.getPageSize()), queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveSignUp(Long id) {
        // 检查报名信息是否存在
        CompetitionSignUpUser competitionSignUpUser = getById(id);
        if (competitionSignUpUser == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        // 检查报名状态
        if (competitionSignUpUser.getStatus() != 0) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_STATUS_ERROR);
        }

        // 更新报名状态为已通过(1)
        competitionSignUpUser.setStatus(1);
        updateById(competitionSignUpUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectSignUp(Long id, String rejectReason) {
        // 检查报名信息是否存在
        CompetitionSignUpUser competitionSignUpUser = getById(id);
        if (competitionSignUpUser == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        // 检查报名状态
        if (competitionSignUpUser.getStatus() != 0) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_STATUS_ERROR);
        }

        // 更新报名状态为已拒绝(2)
        competitionSignUpUser.setStatus(2);
        competitionSignUpUser.setRejectReason(rejectReason);
        updateById(competitionSignUpUser);

        // 更新赛事已报人数
        Competition competition = competitionMapper.selectById(competitionSignUpUser.getCompetitionId());
        if (competition != null) {
            competition.setSignUpNum(competition.getSignUpNum() - 1);
            competitionMapper.updateById(competition);
        }
    }

    @Override
    public List<CompetitionSignUpUserVO> getCompetitionSignUpUser(Long userId) {
        List<CompetitionSignUpUserVO> userVOList = competitionSignUpUserMapper.getCompetitionSignUpUser(userId);
        
        // 动态计算每个个人报名的比赛状态
        for (CompetitionSignUpUserVO vo : userVOList) {
            Integer status = CompetitionStatusEnum.getStatusByTime(
                vo.getSignUpDeadline(),
                vo.getStartTime(),
                vo.getEndTime()
            );
            vo.setCompetitionStatus(status);
        }
        
        return userVOList;
    }
}

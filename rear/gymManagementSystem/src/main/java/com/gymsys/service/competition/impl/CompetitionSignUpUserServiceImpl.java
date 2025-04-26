package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.CompetitionSignUpUser;
import com.gymsys.entity.competition.dto.competitionSignUpUser.AddCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.ListCompetitionSignUpUserDTO;
import com.gymsys.entity.competition.dto.competitionSignUpUser.UpdateCompetitionSignUpUserDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionSignUpUserMapper;
import com.gymsys.service.competition.ICompetitionSignUpUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 赛事个人报名表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
public class CompetitionSignUpUserServiceImpl extends ServiceImpl<CompetitionSignUpUserMapper, CompetitionSignUpUser> implements ICompetitionSignUpUserService {

    @Override
    public void addCompetitionSignUpUser(AddCompetitionSignUpUserDTO addCompetitionSignUpUserDTO) {
        save(BeanUtil.copyProperties(addCompetitionSignUpUserDTO, CompetitionSignUpUser.class));
    }

    @Override
    public void deleteCompetitionSignUpUser(Long id) {
        if (lambdaQuery().eq(CompetitionSignUpUser::getId, id) == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_SIGN_UP_USER_NOT_EXIST);
        }

        removeById(id);
    }

    @Override
    public void updateCompetitionSignUpUser(UpdateCompetitionSignUpUserDTO updateCompetitionSignUpUserDTO) {
        lambdaUpdate().eq(CompetitionSignUpUser::getId, updateCompetitionSignUpUserDTO.getId())
                .set(updateCompetitionSignUpUserDTO.getUserId() != null, CompetitionSignUpUser::getUserId, updateCompetitionSignUpUserDTO.getUserId())
                .set(updateCompetitionSignUpUserDTO.getCompetitionId() != null, CompetitionSignUpUser::getCompetitionId, updateCompetitionSignUpUserDTO.getCompetitionId())
                .set(updateCompetitionSignUpUserDTO.getName() != null, CompetitionSignUpUser::getName, updateCompetitionSignUpUserDTO.getName())
                .set(updateCompetitionSignUpUserDTO.getUserNumber() != null, CompetitionSignUpUser::getUserNumber, updateCompetitionSignUpUserDTO.getUserNumber())
                .set(updateCompetitionSignUpUserDTO.getSex() != null, CompetitionSignUpUser::getSex, updateCompetitionSignUpUserDTO.getSex())
                .set(updateCompetitionSignUpUserDTO.getPhone() != null, CompetitionSignUpUser::getPhone, updateCompetitionSignUpUserDTO.getPhone())
                .set(updateCompetitionSignUpUserDTO.getEmail() != null, CompetitionSignUpUser::getEmail, updateCompetitionSignUpUserDTO.getEmail())
                .set(updateCompetitionSignUpUserDTO.getDepartId() != null, CompetitionSignUpUser::getDepartId, updateCompetitionSignUpUserDTO.getDepartId())
                .set(updateCompetitionSignUpUserDTO.getCompetitionItemId() != null, CompetitionSignUpUser::getCompetitionItemId, updateCompetitionSignUpUserDTO.getCompetitionItemId())
                .set(updateCompetitionSignUpUserDTO.getUserStuorfac() != null, CompetitionSignUpUser::getUserStuorfac, updateCompetitionSignUpUserDTO.getUserStuorfac())
                .set(updateCompetitionSignUpUserDTO.getRemark() != null, CompetitionSignUpUser::getRemark, updateCompetitionSignUpUserDTO.getRemark())
                .set(updateCompetitionSignUpUserDTO.getStatus() != null, CompetitionSignUpUser::getStatus, updateCompetitionSignUpUserDTO.getStatus())
                .set(updateCompetitionSignUpUserDTO.getRejectReason() != null, CompetitionSignUpUser::getRejectReason, updateCompetitionSignUpUserDTO.getRejectReason());
    }

    @Override
    public IPage<CompetitionSignUpUser> listCompetitionSignUpUserDTO(ListCompetitionSignUpUserDTO listCompetitionSignUpUserDTO) {
        return page(new Page<>(listCompetitionSignUpUserDTO.getPage(), listCompetitionSignUpUserDTO.getPageSize()),
                new LambdaQueryWrapper<CompetitionSignUpUser>()
                        .eq(listCompetitionSignUpUserDTO.getCompetitionId() != null, CompetitionSignUpUser::getCompetitionId, listCompetitionSignUpUserDTO.getCompetitionId())
                        .like(StringUtils.isNotBlank(listCompetitionSignUpUserDTO.getName()), CompetitionSignUpUser::getName, listCompetitionSignUpUserDTO.getName())
                        .eq(listCompetitionSignUpUserDTO.getStatus() != null, CompetitionSignUpUser::getStatus, listCompetitionSignUpUserDTO.getStatus()));
    }
}

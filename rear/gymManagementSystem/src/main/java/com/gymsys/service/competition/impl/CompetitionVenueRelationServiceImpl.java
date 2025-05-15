package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.Venue;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import com.gymsys.entity.competition.dto.competitionVenueRelation.AddCompetitionVenueRelationDTO;
import com.gymsys.entity.competition.dto.competitionVenueRelation.ListCompetitionVenueRelationDTO;
import com.gymsys.entity.competition.dto.competitionVenueRelation.UpdateCompetitionVenueRelationDTO;
import com.gymsys.entity.competition.vo.CompetitionVenueRelationVO;
import com.gymsys.enums.BookingStatusEnum;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.VenueMapper;
import com.gymsys.mapper.competition.CompetitionMapper;
import com.gymsys.mapper.competition.CompetitionVenueRelationMapper;
import com.gymsys.service.competition.ICompetitionVenueRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 赛事与场地关联表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
public class CompetitionVenueRelationServiceImpl extends ServiceImpl<CompetitionVenueRelationMapper, CompetitionVenueRelation> implements ICompetitionVenueRelationService {

    private final CompetitionMapper competitionMapper;
    private final VenueMapper venueMapper;
    private final CompetitionVenueRelationMapper competitionVenueRelationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCompetitionVenueRelation(AddCompetitionVenueRelationDTO addCompetitionVenueRelationDTO) {
        // 检查赛事是否存在
        Competition competition = competitionMapper.selectById(addCompetitionVenueRelationDTO.getCompetitionId());
        if (competition == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 检查场地是否存在
        Venue venue = venueMapper.selectById(addCompetitionVenueRelationDTO.getVenueId());
        if (venue == null) {
            throw new BizException(StatusCodeEnum.VENUE_NOT_EXIST);
        }

        // TODO 检查场地数量是否足够

        // 检查时间冲突和场地总量
        int alreadyUsedNum = 0;
        LambdaQueryWrapper<CompetitionVenueRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionVenueRelation::getVenueId, addCompetitionVenueRelationDTO.getVenueId())
                .and(i -> i.between(CompetitionVenueRelation::getStartTime, addCompetitionVenueRelationDTO.getStartTime(), addCompetitionVenueRelationDTO.getEndTime())
                        .or()
                        .between(CompetitionVenueRelation::getEndTime, addCompetitionVenueRelationDTO.getStartTime(), addCompetitionVenueRelationDTO.getEndTime()))
                .eq(CompetitionVenueRelation::getStatus, BookingStatusEnum.SUCCESS); // 只检查预约成功的记录

        List<CompetitionVenueRelation> conflictRelations = baseMapper.selectList(queryWrapper);
        for (CompetitionVenueRelation relation : conflictRelations) {
            alreadyUsedNum += relation.getNum();
        }

        // TODO 检查场地总量是否超过总共的数量

        // 保存场地关联信息
        save(BeanUtil.copyProperties(addCompetitionVenueRelationDTO, CompetitionVenueRelation.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompetitionVenueRelation(Long id) {
        // 检查关联记录是否存在
        CompetitionVenueRelation competitionVenueRelation = getById(id);
        if (competitionVenueRelation == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_VENUE_RELATION_NOT_EXIST);
        }

        // 删除关联记录
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompetitionVenueRelation(UpdateCompetitionVenueRelationDTO updateCompetitionVenueRelationDTO) {
        // 检查关联记录是否存在
        CompetitionVenueRelation competitionVenueRelation = getById(updateCompetitionVenueRelationDTO.getId());
        if (competitionVenueRelation == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_VENUE_RELATION_NOT_EXIST);
        }

        // 如果更新了场地ID、数量或时间，需要检查
        if (updateCompetitionVenueRelationDTO.getVenueId() != null &&
                !updateCompetitionVenueRelationDTO.getVenueId().equals(competitionVenueRelation.getVenueId()) ||
                updateCompetitionVenueRelationDTO.getNum() != null ||
                updateCompetitionVenueRelationDTO.getStartTime() != null ||
                updateCompetitionVenueRelationDTO.getEndTime() != null) {

            Long equipmentId = updateCompetitionVenueRelationDTO.getVenueId() != null ?
                    updateCompetitionVenueRelationDTO.getVenueId() : competitionVenueRelation.getVenueId();

            // 检查场地是否存在
            Venue venue = venueMapper.selectById(equipmentId);
            if (venue == null) {
                throw new BizException(StatusCodeEnum.EQUIPMENT_NOT_EXIST);
            }

            // 检查更新后的数量
            Integer newNum = updateCompetitionVenueRelationDTO.getNum() != null ?
                    updateCompetitionVenueRelationDTO.getNum() : competitionVenueRelation.getNum();

            // 如果数量增加或者更换了场地，检查场地可用量
            if ((updateCompetitionVenueRelationDTO.getNum() != null &&
                    newNum > competitionVenueRelation.getNum()) ||
                    (updateCompetitionVenueRelationDTO.getVenueId() != null &&
                            !updateCompetitionVenueRelationDTO.getVenueId().equals(competitionVenueRelation.getVenueId()))) {

                // TODO 检查场地可用量是否充足

                // 检查时间冲突和场地总量
                int alreadyUsedNum = 0;
                LambdaQueryWrapper<CompetitionVenueRelation> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CompetitionVenueRelation::getVenueId, equipmentId)
                        .ne(CompetitionVenueRelation::getId, updateCompetitionVenueRelationDTO.getId())
                        .and(i -> i.between(CompetitionVenueRelation::getStartTime,
                                        updateCompetitionVenueRelationDTO.getStartTime() != null ?
                                                updateCompetitionVenueRelationDTO.getStartTime() : competitionVenueRelation.getStartTime(),
                                        updateCompetitionVenueRelationDTO.getEndTime() != null ?
                                                updateCompetitionVenueRelationDTO.getEndTime() : competitionVenueRelation.getEndTime())
                                .or()
                                .between(CompetitionVenueRelation::getEndTime,
                                        updateCompetitionVenueRelationDTO.getStartTime() != null ?
                                                updateCompetitionVenueRelationDTO.getStartTime() : competitionVenueRelation.getStartTime(),
                                        updateCompetitionVenueRelationDTO.getEndTime() != null ?
                                                updateCompetitionVenueRelationDTO.getEndTime() : competitionVenueRelation.getEndTime()))
                        .eq(CompetitionVenueRelation::getStatus, BookingStatusEnum.SUCCESS); // 只检查预约成功的记录

                List<CompetitionVenueRelation> conflictRelations = baseMapper.selectList(queryWrapper);
                for (CompetitionVenueRelation relation : conflictRelations) {
                    alreadyUsedNum += relation.getNum();
                }

                // TODO 检查场地总量是否超过总共的数量
            }
        }

        // 更新关联记录
        CompetitionVenueRelation updateEntity = BeanUtil.copyProperties(updateCompetitionVenueRelationDTO, CompetitionVenueRelation.class);
        updateById(updateEntity);
    }

    @Override
    public IPage<CompetitionVenueRelation> listCompetitionVenueRelation(ListCompetitionVenueRelationDTO listCompetitionVenueRelationDTO) {
        LambdaQueryWrapper<CompetitionVenueRelation> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        queryWrapper.eq(listCompetitionVenueRelationDTO.getCompetitionId() != null,
                        CompetitionVenueRelation::getCompetitionId, listCompetitionVenueRelationDTO.getCompetitionId())
                .eq(listCompetitionVenueRelationDTO.getVenueId() != null,
                        CompetitionVenueRelation::getVenueId, listCompetitionVenueRelationDTO.getVenueId())
                .eq(listCompetitionVenueRelationDTO.getStatus() != null,
                        CompetitionVenueRelation::getStatus, listCompetitionVenueRelationDTO.getStatus());

        // 分页查询
        return page(new Page<>(listCompetitionVenueRelationDTO.getPage(), listCompetitionVenueRelationDTO.getPageSize()), queryWrapper);
    }

    @Override
    public List<CompetitionVenueRelation> listByCompetitionId(Long competitionId) {
        LambdaQueryWrapper<CompetitionVenueRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionVenueRelation::getCompetitionId, competitionId);
        return list(queryWrapper);
    }

    @Override
    public List<CompetitionVenueRelationVO> getCompetitionVenueRelation(Long competitionId) {
        return competitionVenueRelationMapper.getCompetitionVenueRelation(competitionId);
    }
}

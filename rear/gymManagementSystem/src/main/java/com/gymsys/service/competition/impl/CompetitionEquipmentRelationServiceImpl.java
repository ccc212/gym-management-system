package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gymsys.entity.competition.Competition;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.AddCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.ListCompetitionEquipmentRelationDTO;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.UpdateCompetitionEquipmentRelationDTO;
import com.gymsys.entity.equip.Equipment;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionEquipmentRelationMapper;
import com.gymsys.mapper.competition.CompetitionMapper;
import com.gymsys.mapper.equipment.EquipmentMapper;
import com.gymsys.service.competition.ICompetitionEquipmentRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 赛事与器材关联表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
@RequiredArgsConstructor
public class CompetitionEquipmentRelationServiceImpl extends ServiceImpl<CompetitionEquipmentRelationMapper, CompetitionEquipmentRelation> implements ICompetitionEquipmentRelationService {

    private final CompetitionMapper competitionMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCompetitionEquipmentRelation(AddCompetitionEquipmentRelationDTO addCompetitionEquipmentRelationDTO) {
        // 检查赛事是否存在
        Competition competition = competitionMapper.selectById(addCompetitionEquipmentRelationDTO.getCompetitionId());
        if (competition == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_NOT_EXIST);
        }

        // 检查器材是否存在
        Equipment equipment = equipmentMapper.selectById(addCompetitionEquipmentRelationDTO.getEquipmentId());
        if (equipment == null) {
            throw new BizException(StatusCodeEnum.EQUIPMENT_NOT_EXIST);
        }

        // TODO 检查器材数量是否足够

        // 检查时间冲突和器材总量
        int alreadyUsedNum = 0;
        LambdaQueryWrapper<CompetitionEquipmentRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionEquipmentRelation::getEquipmentId, addCompetitionEquipmentRelationDTO.getEquipmentId())
                  .and(i -> i.between(CompetitionEquipmentRelation::getStartTime, addCompetitionEquipmentRelationDTO.getStartTime(), addCompetitionEquipmentRelationDTO.getEndTime())
                       .or()
                       .between(CompetitionEquipmentRelation::getEndTime, addCompetitionEquipmentRelationDTO.getStartTime(), addCompetitionEquipmentRelationDTO.getEndTime()))
                  .eq(CompetitionEquipmentRelation::getStatus, 1); // 只检查预约成功的记录
        
        List<CompetitionEquipmentRelation> conflictRelations = baseMapper.selectList(queryWrapper);
        for (CompetitionEquipmentRelation relation : conflictRelations) {
            alreadyUsedNum += relation.getNum();
        }

        // TODO 检查器材总量是否超过总共的数量
        
        // 保存器材关联信息
        CompetitionEquipmentRelation competitionEquipmentRelation = BeanUtil.copyProperties(addCompetitionEquipmentRelationDTO, CompetitionEquipmentRelation.class);
        // 设置初始状态：预约中(0)
        competitionEquipmentRelation.setStatus(0);
        save(competitionEquipmentRelation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompetitionEquipmentRelation(Long id) {
        // 检查关联记录是否存在
        CompetitionEquipmentRelation competitionEquipmentRelation = getById(id);
        if (competitionEquipmentRelation == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_EQUIPMENT_RELATION_NOT_EXIST);
        }

        // 删除关联记录
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompetitionEquipmentRelation(UpdateCompetitionEquipmentRelationDTO updateCompetitionEquipmentRelationDTO) {
        // 检查关联记录是否存在
        CompetitionEquipmentRelation competitionEquipmentRelation = getById(updateCompetitionEquipmentRelationDTO.getId());
        if (competitionEquipmentRelation == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_EQUIPMENT_RELATION_NOT_EXIST);
        }

        // 如果更新了器材ID、数量或时间，需要检查
        if (updateCompetitionEquipmentRelationDTO.getEquipmentId() != null && 
            !updateCompetitionEquipmentRelationDTO.getEquipmentId().equals(competitionEquipmentRelation.getEquipmentId()) || 
            updateCompetitionEquipmentRelationDTO.getNum() != null ||
            updateCompetitionEquipmentRelationDTO.getStartTime() != null || 
            updateCompetitionEquipmentRelationDTO.getEndTime() != null) {
            
            Long equipmentId = updateCompetitionEquipmentRelationDTO.getEquipmentId() != null ? 
                            updateCompetitionEquipmentRelationDTO.getEquipmentId() : competitionEquipmentRelation.getEquipmentId();
            
            // 检查器材是否存在
            Equipment equipment = equipmentMapper.selectById(equipmentId);
            if (equipment == null) {
                throw new BizException(StatusCodeEnum.EQUIPMENT_NOT_EXIST);
            }
            
            // 检查更新后的数量
            Integer newNum = updateCompetitionEquipmentRelationDTO.getNum() != null ? 
                         updateCompetitionEquipmentRelationDTO.getNum() : competitionEquipmentRelation.getNum();
            
            // 如果数量增加或者更换了器材，检查器材可用量
            if ((updateCompetitionEquipmentRelationDTO.getNum() != null && 
                newNum > competitionEquipmentRelation.getNum()) ||
                (updateCompetitionEquipmentRelationDTO.getEquipmentId() != null && 
                !updateCompetitionEquipmentRelationDTO.getEquipmentId().equals(competitionEquipmentRelation.getEquipmentId()))) {

                // TODO 检查器材可用量是否充足
                
                // 检查时间冲突和器材总量
                int alreadyUsedNum = 0;
                LambdaQueryWrapper<CompetitionEquipmentRelation> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CompetitionEquipmentRelation::getEquipmentId, equipmentId)
                          .ne(CompetitionEquipmentRelation::getId, updateCompetitionEquipmentRelationDTO.getId())
                          .and(i -> i.between(CompetitionEquipmentRelation::getStartTime, 
                                         updateCompetitionEquipmentRelationDTO.getStartTime() != null ? 
                                         updateCompetitionEquipmentRelationDTO.getStartTime() : competitionEquipmentRelation.getStartTime(), 
                                         updateCompetitionEquipmentRelationDTO.getEndTime() != null ? 
                                         updateCompetitionEquipmentRelationDTO.getEndTime() : competitionEquipmentRelation.getEndTime())
                               .or()
                               .between(CompetitionEquipmentRelation::getEndTime, 
                                      updateCompetitionEquipmentRelationDTO.getStartTime() != null ? 
                                      updateCompetitionEquipmentRelationDTO.getStartTime() : competitionEquipmentRelation.getStartTime(), 
                                      updateCompetitionEquipmentRelationDTO.getEndTime() != null ? 
                                      updateCompetitionEquipmentRelationDTO.getEndTime() : competitionEquipmentRelation.getEndTime()))
                          .eq(CompetitionEquipmentRelation::getStatus, 1); // 只检查预约成功的记录
                
                List<CompetitionEquipmentRelation> conflictRelations = baseMapper.selectList(queryWrapper);
                for (CompetitionEquipmentRelation relation : conflictRelations) {
                    alreadyUsedNum += relation.getNum();
                }

                // TODO 检查器材总量是否超过总共的数量
            }
        }

        // 更新关联记录
        CompetitionEquipmentRelation updateEntity = BeanUtil.copyProperties(updateCompetitionEquipmentRelationDTO, CompetitionEquipmentRelation.class);
        updateById(updateEntity);
    }

    @Override
    public IPage<CompetitionEquipmentRelation> listCompetitionEquipmentRelation(ListCompetitionEquipmentRelationDTO listCompetitionEquipmentRelationDTO) {
        LambdaQueryWrapper<CompetitionEquipmentRelation> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.eq(listCompetitionEquipmentRelationDTO.getCompetitionId() != null, 
                        CompetitionEquipmentRelation::getCompetitionId, listCompetitionEquipmentRelationDTO.getCompetitionId())
                   .eq(listCompetitionEquipmentRelationDTO.getEquipmentId() != null, 
                        CompetitionEquipmentRelation::getEquipmentId, listCompetitionEquipmentRelationDTO.getEquipmentId())
                   .eq(listCompetitionEquipmentRelationDTO.getStatus() != null, 
                        CompetitionEquipmentRelation::getStatus, listCompetitionEquipmentRelationDTO.getStatus());
        
        // 分页查询
        return page(new Page<>(listCompetitionEquipmentRelationDTO.getPage(), listCompetitionEquipmentRelationDTO.getPageSize()), queryWrapper);
    }

    @Override
    public List<CompetitionEquipmentRelation> listByCompetitionId(Long competitionId) {
        LambdaQueryWrapper<CompetitionEquipmentRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompetitionEquipmentRelation::getCompetitionId, competitionId);
        return list(queryWrapper);
    }
}

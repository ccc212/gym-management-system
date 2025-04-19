package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.dto.competitionEquipmentRelation.AddCompetitionEquipmentRelationDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionEquipmentRelationMapper;
import com.gymsys.service.competition.ICompetitionEquipmentRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 赛事与器材关联表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
public class CompetitionEquipmentRelationServiceImpl extends ServiceImpl<CompetitionEquipmentRelationMapper, CompetitionEquipmentRelation> implements ICompetitionEquipmentRelationService {

    @Override
    public void addCompetitionEquipmentRelation(AddCompetitionEquipmentRelationDTO addCompetitionEquipmentRelationDTO) {
        save(BeanUtil.copyProperties(addCompetitionEquipmentRelationDTO, CompetitionEquipmentRelation.class));
    }

    @Override
    public void deleteCompetitionEquipmentRelation(Long id) {
        if (lambdaQuery().eq(CompetitionEquipmentRelation::getId, id) == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_EQUIPMENT_RELATION_NOT_EXIST);
        }

        removeById(id);
    }
}

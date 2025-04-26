package com.gymsys.service.competition.impl;

import cn.hutool.core.bean.BeanUtil;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import com.gymsys.entity.competition.dto.competitionVenueRelation.AddCompetitionVenueRelationDTO;
import com.gymsys.enums.StatusCodeEnum;
import com.gymsys.exception.BizException;
import com.gymsys.mapper.competition.CompetitionVenueRelationMapper;
import com.gymsys.service.competition.ICompetitionVenueRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 赛事与场地关联表 服务实现类
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Service
public class CompetitionVenueRelationServiceImpl extends ServiceImpl<CompetitionVenueRelationMapper, CompetitionVenueRelation> implements ICompetitionVenueRelationService {

    @Override
    public void addCompetitionVenueRelation(AddCompetitionVenueRelationDTO addCompetitionVenueRelationDTO) {
        save(BeanUtil.copyProperties(addCompetitionVenueRelationDTO, CompetitionVenueRelation.class));
    }

    @Override
    public void deleteCompetitionVenueRelation(Long id) {
        if (lambdaQuery().eq(CompetitionVenueRelation::getId, id) == null) {
            throw new BizException(StatusCodeEnum.COMPETITION_VENUE_RELATION_NOT_EXIST);
        }

        removeById(id);
    }
}

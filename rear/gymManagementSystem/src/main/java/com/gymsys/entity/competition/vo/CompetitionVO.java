package com.gymsys.entity.competition.vo;

import com.gymsys.entity.competition.Competition;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CompetitionVO extends Competition {

    /**
     * 赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)
     * 该字段由CompetitionStatusEnum.getStatusByTime方法动态计算
     */
    private Integer status;
} 
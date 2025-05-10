package com.gymsys.entity.competition.dto.competition;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListCompetitionDTO extends PageDTO {

    /**
     * 赛事id
     */
    private Long id;

    /**
     * 赛事名称
     */
    private String name;

    /**
     * 赛事类型(0为乒乓球赛，1为篮球赛)
     */
    private Integer type;

    /**
     * 赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)
     */
    private Integer status;
}

package com.gymsys.entity.competition.dto.competition;

import com.gymsys.entity.PageDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListCompetitionDTO extends PageDTO {

    /**
     * 赛事名称
     */
    private String name;

    /**
     * 赛事类型(0为乒乓球赛，1为篮球赛)
     */
    private Integer type;

    /**
     * 赛事类别(0为淘汰赛，1为循环赛)
     */
    private Integer category;

    /**
     * 举办方
     */
    private String hoster;

    /**
     * 赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)
     */
    private Integer status;

    /**
     * 是否为团体赛事
     */
    private Integer isTeamCompetition;
}

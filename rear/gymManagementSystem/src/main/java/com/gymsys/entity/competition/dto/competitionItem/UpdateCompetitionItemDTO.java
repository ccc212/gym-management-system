package com.gymsys.entity.competition.dto.competitionItem;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateCompetitionItemDTO extends BaseDTO {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long id;

    /**
     * 项目名
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
     * 是否为团体赛事
     */
    private Integer isTeamCompetition;
} 
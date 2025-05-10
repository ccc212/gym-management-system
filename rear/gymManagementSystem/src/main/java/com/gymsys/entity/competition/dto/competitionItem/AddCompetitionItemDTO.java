package com.gymsys.entity.competition.dto.competitionItem;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddCompetitionItemDTO extends BaseDTO {

    /**
     * 项目名
     */
    @NotBlank(message = "项目名不能为空")
    private String name;

    /**
     * 赛事类型(0为乒乓球赛，1为篮球赛)
     */
    @NotNull(message = "赛事类型不能为空")
    private Integer type;

    /**
     * 赛事类别(0为淘汰赛，1为循环赛)
     */
    @NotNull(message = "赛事类别不能为空")
    private Integer category;

    /**
     * 是否为团体赛事
     */
    @NotNull(message = "是否为团体赛事不能为空")
    private Integer isTeamCompetition;
} 
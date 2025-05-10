package com.gymsys.entity.competition.dto.competition;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateCompetitionDTO extends BaseDTO {

    /**
     * 赛事ID
     */
    @NotNull(message = "赛事ID不能为空")
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
     * 赛事类别(0为淘汰赛，1为循环赛)
     */
    private Integer category;

    /**
     * 举办方
     */
    private String hoster;

    /**
     * 赛事开始时间
     */
    private LocalDateTime startTime;

    /**
     * 赛事结束时间
     */
    private LocalDateTime endTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime signUpDeadline;

    /**
     * 已报人数
     */
    private Integer signUpNum;

    /**
     * 最大报名人数
     */
    private Integer maxSignUpNum;

    /**
     * 赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)
     */
    private Integer status;

    /**
     * 是否为团体赛事
     */
    private Integer isTeamCompetition;

    /**
     * 每队人数上限
     */
    private Integer teamMaxNum;

    /**
     * 每队人数下限
     */
    private Integer teamMinNum;

    /**
     * 参赛要求
     */
    private String requirement;

    /**
     * 赛事描述
     */
    private String description;
    
    /**
     * 赛事项目id列表
     */
    private List<Long> competitionItemIds;
}

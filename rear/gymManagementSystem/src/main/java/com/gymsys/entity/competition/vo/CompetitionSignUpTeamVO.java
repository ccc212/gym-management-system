package com.gymsys.entity.competition.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompetitionSignUpTeamVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 赛事名称
     */
    private String competitionName;

    /**
     * 赛事类型
     */
    private String competitionType;

    /**
     * 赛事开始时间
     */
    private LocalDateTime startTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime signUpDeadline;

    /**
     * 赛事结束时间
     */
    private LocalDateTime endTime;

    /**
     * 赛事状态
     */
    private Integer competitionStatus;

    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 报名状态
     */
    private Integer signUpStatus;

    /**
     * 拒绝原因
     */
    private String rejectReason;
}

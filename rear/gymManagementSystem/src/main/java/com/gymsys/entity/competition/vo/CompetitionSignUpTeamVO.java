package com.gymsys.entity.competition.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompetitionSignUpTeamVO {

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
     * 赛事状态
     */
    private String competitionStatus;

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

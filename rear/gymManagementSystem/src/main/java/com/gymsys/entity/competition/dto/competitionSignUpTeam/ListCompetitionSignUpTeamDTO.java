package com.gymsys.entity.competition.dto.competitionSignUpTeam;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListCompetitionSignUpTeamDTO extends PageDTO {

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 团队名字
     */
    private String teamName;

    /**
     * 状态(0为报名中，1为报名成功，2为报名失败)
     */
    private Integer status;
}

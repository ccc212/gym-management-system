package com.gymsys.entity.competition.dto.competitionSignUpUser;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListCompetitionSignUpUserDTO extends PageDTO {

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 状态(0为报名中，1为报名成功，2为报名失败)
     */
    private Integer status;
}

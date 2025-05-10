package com.gymsys.entity.competition.dto.team;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListTeamDTO extends PageDTO {

    /**
     * 团队名字
     */
    private String teamName;

    /**
     * 领队姓名
     */
    private String leaderName;

    /**
     * 部门id
     */
    private Integer departId;
}

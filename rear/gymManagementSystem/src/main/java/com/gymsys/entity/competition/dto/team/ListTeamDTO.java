package com.gymsys.entity.competition.dto.team;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListTeamDTO extends PageDTO {

    /**
     * 团队id
     */
    private Long id;

    /**
     * 团队名字
     */
    private String teamName;

}

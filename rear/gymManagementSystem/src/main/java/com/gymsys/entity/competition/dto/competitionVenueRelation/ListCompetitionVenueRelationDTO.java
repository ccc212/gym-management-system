package com.gymsys.entity.competition.dto.competitionVenueRelation;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListCompetitionVenueRelationDTO extends PageDTO {

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 场地id
     */
    private Long venueId;

    /**
     * 状态(0为预约中，1为预约成功，2为预约失败)
     */
    private Integer status;
} 
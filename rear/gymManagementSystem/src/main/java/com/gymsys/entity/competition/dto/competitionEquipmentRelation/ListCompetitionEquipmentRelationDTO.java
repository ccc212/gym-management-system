package com.gymsys.entity.competition.dto.competitionEquipmentRelation;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListCompetitionEquipmentRelationDTO extends PageDTO {

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 器材id
     */
    private Long equipmentId;

    /**
     * 状态(0为预约中，1为预约成功，2为预约失败)
     */
    private Integer status;
} 
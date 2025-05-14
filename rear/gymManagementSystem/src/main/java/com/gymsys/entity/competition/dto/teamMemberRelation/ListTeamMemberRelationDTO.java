package com.gymsys.entity.competition.dto.teamMemberRelation;

import com.gymsys.entity.PageDTO;
import lombok.Data;

@Data
public class ListTeamMemberRelationDTO extends PageDTO {
    /**
     * 团队id
     */
    private Long teamId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer status;
}

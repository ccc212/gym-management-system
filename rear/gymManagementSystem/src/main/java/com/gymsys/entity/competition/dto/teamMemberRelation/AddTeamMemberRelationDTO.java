package com.gymsys.entity.competition.dto.teamMemberRelation;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AddTeamMemberRelationDTO extends BaseDTO {

    /**
     * 团队id
     */
    @NotNull(message = "团队id不能为空")
    private Long teamId;

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;
    
    /**
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer status = 0;
}

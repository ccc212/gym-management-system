package com.gymsys.entity.competition.dto.team;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateTeamDTO extends BaseDTO {

    /**
     * 团队id
     */
    @NotNull(message = "团队id不能为空")
    private Long id;

    /**
     * 团队名字
     */
    private String teamName;

    /**
     * 领队姓名
     */
    private String leaderName;

    /**
     * 联系电话
     */
    private String leaderPhone;

    /**
     * 部门id
     */
    private Integer departId;
}

package com.gymsys.entity.competition.dto.competitionSignUpTeam;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddCompetitionSignUpTeamDTO extends BaseDTO {

    /**
     * 赛事id
     */
    @NotNull(message = "赛事id不能为空")
    private Long competitionId;

    /**
     * 团队id
     */
    @NotNull(message = "团队id不能为空")
    private Long teamId;

    /**
     * 团队名字
     */
    @NotNull(message = "团队名字不能为空")
    private String teamName;

    /**
     * 领队姓名
     */
    @NotBlank(message = "领队姓名不能为空")
    private String leaderName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String leaderPhone;

    /**
     * 部门id
     */
    @NotNull(message = "部门id不能为空")
    private Integer departId;

    /**
     * 赛事项目id
     */
    @NotNull(message = "赛事项目id不能为空")
    private Long competitionItemId;

    /**
     * 备注
     */
    private String remark;
}

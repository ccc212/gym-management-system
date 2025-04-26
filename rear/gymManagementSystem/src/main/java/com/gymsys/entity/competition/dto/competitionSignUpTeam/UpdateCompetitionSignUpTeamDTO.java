package com.gymsys.entity.competition.dto.competitionSignUpTeam;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateCompetitionSignUpTeamDTO extends BaseDTO {

    /**
     * 赛事团体报名表id
     */
    @NotNull(message = "赛事团体报名表id不能为空")
    private Long id;

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 团队id
     */
    private Long teamId;

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

    /**
     * 赛事项目id
     */
    private Long competitionItemId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态(0为报名中，1为报名成功，2为报名失败)
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;
}

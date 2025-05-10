package com.gymsys.entity.competition.dto.team;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateTeamDTO extends BaseDTO {

    /**
     * 团队ID
     */
    @NotNull(message = "团队ID不能为空")
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
    
    /**
     * 团队成员id列表，如果提供则替换现有成员
     */
    private List<Long> memberIds;
}

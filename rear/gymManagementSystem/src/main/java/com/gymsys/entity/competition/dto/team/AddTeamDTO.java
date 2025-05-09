package com.gymsys.entity.competition.dto.team;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddTeamDTO extends BaseDTO {

    /**
     * 团队名字
     */
    @NotBlank(message = "团队名字不能为空")
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
     * 团队成员id列表
     */
    private List<Long> memberIds;
}

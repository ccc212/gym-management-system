package com.gymsys.entity.competition.dto.competitionVenueRelation;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AddCompetitionVenueRelationDTO extends BaseDTO {

    /**
     * 赛事id
     */
    @NotNull(message = "赛事id不能为空")
    private Long competitionId;

    /**
     * 场地id
     */
    @NotNull(message = "场地id不能为空")
    private Long venueId;

    /**
     * 负责人姓名
     */
    @NotBlank(message = "负责人姓名不能为空")
    private String responsibleName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}

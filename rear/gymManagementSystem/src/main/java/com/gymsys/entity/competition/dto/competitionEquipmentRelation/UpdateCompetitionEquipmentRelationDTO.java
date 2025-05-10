package com.gymsys.entity.competition.dto.competitionEquipmentRelation;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class UpdateCompetitionEquipmentRelationDTO extends BaseDTO {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 器材id
     */
    private Long equipmentId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态(0为预约中，1为预约成功，2为预约失败)
     */
    private Integer status;
} 
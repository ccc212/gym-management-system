package com.gymsys.entity.competition.dto.competitionEquipmentRelation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AddCompetitionEquipmentRelationDTO extends BaseDTO {

    /**
     * 赛事id
     */
    @NotNull(message = "赛事id不能为空")
    private Long competitionId;

    /**
     * 器材id
     */
    @NotNull(message = "器材id不能为空")
    private Long equipmentId;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer num;

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

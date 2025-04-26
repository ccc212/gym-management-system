package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 赛事与器材关联表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
public class CompetitionEquipmentRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

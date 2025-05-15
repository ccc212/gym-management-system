package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 赛事与场地关联表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
public class CompetitionVenueRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 场地id
     */
    private Long venueId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 负责人姓名
     */
    private String responsibleName;

    /**
     * 联系电话
     */
    private String phone;

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

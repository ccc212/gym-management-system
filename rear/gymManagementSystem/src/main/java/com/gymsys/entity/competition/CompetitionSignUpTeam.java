package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 赛事团体报名表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
public class CompetitionSignUpTeam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

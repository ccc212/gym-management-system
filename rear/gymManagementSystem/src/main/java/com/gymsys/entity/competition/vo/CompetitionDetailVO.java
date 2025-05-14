package com.gymsys.entity.competition.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gymsys.entity.competition.CompetitionEquipmentRelation;
import com.gymsys.entity.competition.CompetitionVenueRelation;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class CompetitionDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事名称
     */
    private String name;

    /**
     * 赛事类型(0为乒乓球赛，1为篮球赛)
     */
    private Integer type;

    /**
     * 赛事类别(0为淘汰赛，1为循环赛)
     */
    private Integer category;

    /**
     * 举办方
     */
    private String hoster;

    /**
     * 赛事开始时间
     */
    private LocalDateTime startTime;

    /**
     * 赛事结束时间
     */
    private LocalDateTime endTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime signUpDeadline;

    /**
     * 是否为团体赛事
     */
    private Integer isTeamCompetition;

    /**
     * 赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)
     * 由CompetitionStatusEnum.getStatusByTime方法动态计算
     */
    private Integer status;

    /**
     * 场地
     */
    private List<CompetitionVenueRelation> venueRelations;

    /**
     * 器材
     */
    private List<CompetitionEquipmentRelation> equipmentRelations;

    /**
     * 参赛要求
     */
    private String requirement;

    /**
     * 赛事描述
     */
    private String description;
}

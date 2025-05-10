package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 赛事表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
@Accessors(chain = true)
public class Competition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事名称
     */
    @NotBlank(message = "赛事名称不能为空")
    private String name;

    /**
     * 赛事类型(0为乒乓球赛，1为篮球赛)
     */
    @NotNull(message = "赛事类型不能为空")
    private Integer type;

    /**
     * 赛事类别(0为淘汰赛，1为循环赛)
     */
    @NotNull(message = "赛事类别不能为空")
    private Integer category;

    /**
     * 举办方
     */
    @NotBlank(message = "举办方不能为空")
    private String hoster;

    /**
     * 赛事开始时间
     */
    @NotNull(message = "赛事开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 赛事结束时间
     */
    @NotNull(message = "赛事结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 报名截止时间
     */
    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime signUpDeadline;

    /**
     * 已报人数
     */
    private Integer signUpNum;

    /**
     * 最大报名人数
     */
    private Integer maxSignUpNum;

    /**
     * 赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)
     */
    private Integer status;

    /**
     * 是否为团体赛事
     */
    @NotNull(message = "是否为团体赛事不能为空")
    private Integer isTeamCompetition;

    /**
     * 每队人数上限
     */
    private Integer teamMaxNum;

    /**
     * 每队人数下限
     */
    private Integer teamMinNum;

    /**
     * 参赛要求
     */
    private String requirement;

    /**
     * 赛事描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

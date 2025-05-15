package com.gymsys.entity.competition.dto.competition;

import com.gymsys.entity.PageDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ListCompetitionDTO extends PageDTO {

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
     * 是否为团体赛事
     */
    private Integer isTeamCompetition;

    /**
     * 赛事开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 赛事结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

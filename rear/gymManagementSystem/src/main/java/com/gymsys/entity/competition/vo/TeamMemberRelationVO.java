package com.gymsys.entity.competition.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamMemberRelationVO {

    private Long id;

    private Long teamId;

    private Integer userId;

    private String name;

    private String userNumber;

    private Integer status;

    private LocalDateTime createTime;
}

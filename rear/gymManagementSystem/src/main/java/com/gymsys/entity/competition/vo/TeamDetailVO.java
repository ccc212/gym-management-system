package com.gymsys.entity.competition.vo;

import com.gymsys.entity.competition.Team;
import com.gymsys.entity.system.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeamDetailVO {

    /**
     * 团队ID
     */
    private Long id;

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
     * 部门名称
     */
    private String departName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 团队成员列表
     */
    private List<User> members;
} 
package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 赛事与场地关联表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
@Entity
@Table(name = "competition_venue_relation")
public class CompetitionVenueRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事id
     */
    @Column(name = "competition_id")
    private Long competitionId;

    /**
     * 场地id
     */
    @Column(name = "venue_id")
    private Long venueId;

    /**
     * 负责人姓名
     */
    @Column(name = "responsible_name")
    private String responsibleName;

    /**
     * 联系电话
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 状态(0为预约中，1为预约成功，2为预约失败)
     */
    @Column(name = "status")
    private Integer status;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}

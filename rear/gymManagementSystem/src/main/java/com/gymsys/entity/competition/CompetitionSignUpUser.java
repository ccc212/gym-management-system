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
 * 赛事个人报名表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
public class CompetitionSignUpUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 学号或职工号
     */
    private String userNumber;

    /**
     * 性别
     */
    private String sex;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门id
     */
    private Integer departId;

    /**
     * 赛事项目id
     */
    private Long competitionItemId;

    /**
     * 区分学生或教职工 0 学生 1教师
     */
    private String userStuorfac;

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

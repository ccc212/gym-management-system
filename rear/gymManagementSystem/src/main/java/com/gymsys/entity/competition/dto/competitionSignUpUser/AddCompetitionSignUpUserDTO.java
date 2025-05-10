package com.gymsys.entity.competition.dto.competitionSignUpUser;

import com.gymsys.entity.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddCompetitionSignUpUserDTO extends BaseDTO {

    /**
     * 赛事id
     */
    @NotNull(message = "赛事id不能为空")
    private Long competitionId;

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 学号或职工号
     */
    @NotBlank(message = "学号或职工号不能为空")
    private String userNumber;

    /**
     * 性别
     */
    @NotBlank(message = "性别不能为空")
    private String sex;

    /**
     * 电话
     */
    @NotBlank(message = "电话不能为空")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 部门id
     */
    @NotNull(message = "部门id不能为空")
    private Integer departId;

    /**
     * 赛事项目id
     */
    @NotNull(message = "赛事项目id不能为空")
    private Long competitionItemId;

    /**
     * 区分学生或教职工 0 学生 1教师
     */
    @NotBlank(message = "区分学生或教职工不能为空")
    private String userStuorfac;

    /**
     * 备注
     */
    private String remark;
}

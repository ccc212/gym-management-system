package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 团队表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-26
 */
@Data
@Accessors(chain = true)
public class Team implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

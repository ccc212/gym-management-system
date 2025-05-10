package com.gymsys.entity.competition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 赛事与项目关联表
 * </p>
 *
 * @author ccc212
 * @since 2025-04-19
 */
@Data
@Accessors(chain = true)
public class CompetitionItemRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事id
     */
    private Long competitionId;

    /**
     * 赛事项目id
     */
    private Long competitionItemId;


}

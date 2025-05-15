package com.gymsys.entity.venue;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ccc212
 * @since 2025-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("venues")
public class Venues implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer capacity;

    private String description;

    private Boolean isAvailable;

    private String location;

    private String name;

    private BigDecimal pricePerHour;

    private String type;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}

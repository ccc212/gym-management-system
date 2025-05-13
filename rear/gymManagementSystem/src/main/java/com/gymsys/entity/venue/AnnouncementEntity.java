package com.gymsys.entity.venue;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class AnnouncementEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String status;
    private Boolean active;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
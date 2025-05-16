package com.gymsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("announcements")
public class Announcement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String type;
    private LocalDate publishDate;
    private LocalDate endDate;
    private String status;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
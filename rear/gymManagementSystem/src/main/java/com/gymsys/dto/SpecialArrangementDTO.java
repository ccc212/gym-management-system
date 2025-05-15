package com.gymsys.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SpecialArrangementDTO {
    private Long id;
    private Long venueId;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String status;
    private String remarks;
} 
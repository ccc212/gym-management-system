package com.gymsys.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SpecialArrangementDTO {
    private Long id;
    private Long venueId;
    private LocalDate date;
    private List<String> timeSlots;
    private String purpose;
    private String remarks;
    private Boolean notifyUsers;
} 
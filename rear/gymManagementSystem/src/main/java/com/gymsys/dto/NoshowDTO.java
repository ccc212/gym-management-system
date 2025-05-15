package com.gymsys.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class NoshowDTO {
    private Long id;
    private Long reservationId;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long venueId;
    private String venueName;
    private String venueType;
    private LocalDate date;
    private String timeRange;
    private Integer numberOfPeople;
    private BigDecimal cost;
    private String status;
    private BigDecimal penalty;
    private Integer restrictDays;
    private String handler;
    private String reason;
    private Boolean notifyUser;
} 
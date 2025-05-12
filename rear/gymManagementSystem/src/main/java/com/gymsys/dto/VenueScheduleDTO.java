package com.gymsys.dto;

import lombok.Data;

@Data
public class VenueScheduleDTO {
    private Long venueId;
    private String venueName;
    private String type;
    private String date;
    private String timeSlot;      // 例如："08:00 - 08:30"
    private String status;        // 可预约、已预约、使用中、特殊场地、维护中
    private String bookedBy;
    private Integer numberOfPeople;
    private String remarks;
} 
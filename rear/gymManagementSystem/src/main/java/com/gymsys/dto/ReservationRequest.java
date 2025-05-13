package com.gymsys.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    private Long userId;
    private String cardNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer numberOfPeople;
    private String remarks;
} 
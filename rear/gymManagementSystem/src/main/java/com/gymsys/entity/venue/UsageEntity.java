package com.gymsys.entity.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usages")
public class UsageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;
    
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;
    
    @Column(name = "card_number", nullable = false)
    private String cardNumber;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column
    private BigDecimal cost;
    
    @Column
    private Boolean paid = false;
    
    @Column
    private String remark;
}
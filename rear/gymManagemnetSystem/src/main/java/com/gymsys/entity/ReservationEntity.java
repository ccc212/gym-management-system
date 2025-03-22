package com.gymsys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;
    
    @Column(name = "card_number", nullable = false)
    private String cardNumber;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "reservation_type", nullable = false)
    private String reservationType; // REGULAR, TEAM, CLASS
    
    @Column(name = "status", nullable = false)
    private String status; // BOOKED, CANCELLED, COMPLETED, NO_SHOW
    
    @Column
    private String remark;
}
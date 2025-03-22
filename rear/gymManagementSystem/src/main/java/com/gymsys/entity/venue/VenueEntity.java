package com.gymsys.entity.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "venues")
public class VenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    @Column(name = "price_per_hour", nullable = false)
    private BigDecimal pricePerHour;
    
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;
    
    @Column
    private String location;
    
    @Column
    private Integer capacity;
    
    @Column
    private String description;
}
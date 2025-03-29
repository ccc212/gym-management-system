package com.gymsys.repository.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByVenueAndStartTimeBetween(VenueEntity venue, LocalDateTime start, LocalDateTime end);
    
    List<ReservationEntity> findByCardNumberAndStartTimeBetween(String cardNumber, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.venue.id = :venueId AND r.startTime >= :weekStart AND r.startTime < :weekEnd")
    List<ReservationEntity> findVenueWeeklyReservations(Long venueId, LocalDateTime weekStart, LocalDateTime weekEnd);
    
    List<ReservationEntity> findByCardNumberAndStatus(String cardNumber, String status);
    
    List<ReservationEntity> findByReservationType(String reservationType);
}
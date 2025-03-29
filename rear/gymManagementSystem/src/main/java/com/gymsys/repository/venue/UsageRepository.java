package com.gymsys.repository.venue;

import com.gymsys.entity.venue.UsageEntity;
import com.gymsys.entity.venue.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageRepository extends JpaRepository<UsageEntity, Long> {
    List<UsageEntity> findByVenueAndStartTimeBetween(VenueEntity venue, LocalDateTime start, LocalDateTime end);
    
    List<UsageEntity> findByCardNumberAndPaid(String cardNumber, boolean paid);
    
    List<UsageEntity> findByEndTimeIsNull();
}
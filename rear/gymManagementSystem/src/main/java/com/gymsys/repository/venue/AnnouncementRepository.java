package com.gymsys.repository.venue;

import com.gymsys.entity.venue.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    List<AnnouncementEntity> findByActiveTrue();
    
    List<AnnouncementEntity> findByActiveTrueAndPublishTimeBeforeAndExpireTimeAfter(
            LocalDateTime now, LocalDateTime now2);
}
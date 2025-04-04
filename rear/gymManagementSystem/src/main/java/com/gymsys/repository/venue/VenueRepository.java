package com.gymsys.repository.venue;

import com.gymsys.entity.venue.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<VenueEntity, Long> {
    List<VenueEntity> findByIsAvailable(boolean isAvailable);
    List<VenueEntity> findByType(String type);
}
package com.gymsys.repository;

import gym.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<VenueEntity, Long> {
    List<VenueEntity> findByIsAvailable(boolean isAvailable);
    List<VenueEntity> findByType(String type);
}
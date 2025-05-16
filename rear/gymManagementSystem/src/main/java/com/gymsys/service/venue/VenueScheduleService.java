package com.gymsys.service.venue;

import com.gymsys.dto.VenueScheduleDTO;
import java.time.LocalDate;
import java.util.List;

public interface VenueScheduleService {
    List<VenueScheduleDTO> getWeeklySchedule(LocalDate startDate, LocalDate endDate, String venueType, Long venueId);
    List<String> getVenueTypes();
    List<VenueScheduleDTO> getVenuesByType(String type);
} 

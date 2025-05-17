package com.gymsys.service.venue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gymsys.dto.VenueScheduleDTO;
import com.gymsys.entity.reservation.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.reservation.ReservationRepository;
import com.gymsys.repository.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VenueScheduleServiceImpl implements VenueScheduleService {
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<VenueScheduleDTO> getWeeklySchedule(LocalDate startDate, LocalDate endDate, String venueType, Long venueId) {
        List<VenueScheduleDTO> schedules = new ArrayList<>();
        LambdaQueryWrapper<VenueEntity> venueWrapper = new LambdaQueryWrapper<>();
        if (venueType != null && !venueType.isEmpty()) {
            venueWrapper.eq(VenueEntity::getType, venueType);
        }
        if (venueId != null) {
            venueWrapper.eq(VenueEntity::getId, venueId);
        }
        List<VenueEntity> venues = venueRepository.selectList(venueWrapper);
        LambdaQueryWrapper<ReservationEntity> reservationWrapper = new LambdaQueryWrapper<>();
        reservationWrapper.between(ReservationEntity::getDate, startDate, endDate)
                        .in(ReservationEntity::getStatus, Arrays.asList("CONFIRMED", "IN_USE", "PENDING"));
        List<ReservationEntity> reservations = reservationRepository.selectList(reservationWrapper);
        Map<Long, List<ReservationEntity>> reservationsByVenue = reservations.stream()
                .collect(Collectors.groupingBy(ReservationEntity::getVenueId));
        LocalTime startTime = LocalTime.parse("08:00", TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse("22:00", TIME_FORMATTER);
        for (VenueEntity venue : venues) {
            List<ReservationEntity> venueReservations = reservationsByVenue.getOrDefault(venue.getId(), new ArrayList<>());
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                LocalTime currentTime = startTime;
                while (currentTime.isBefore(endTime)) {
                    VenueScheduleDTO schedule = new VenueScheduleDTO();
                    schedule.setVenueId(venue.getId());
                    schedule.setVenueName(venue.getName());
                    schedule.setType(venue.getType());
                    schedule.setDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    LocalTime nextTime = currentTime.plusHours(1);
                    schedule.setTimeSlot(currentTime.format(TIME_FORMATTER) + " - " + nextTime.format(TIME_FORMATTER));
                    String status = determineTimeSlotStatus(venue, date, currentTime, venueReservations);
                    schedule.setStatus(status);
                    schedules.add(schedule);
                    currentTime = nextTime;
                }
            }
        }
        return schedules;
    }

    private String determineTimeSlotStatus(VenueEntity venue, LocalDate date, LocalTime time,
                                         List<ReservationEntity> venueReservations) {
        if ("MAINTENANCE".equals(venue.getStatus())) {
            return "MAINTENANCE";
        }
        if (date.getDayOfWeek().getValue() == 1 && 
            time.getHour() >= 8 && time.getHour() < 10) {
            return "MAINTENANCE";
        }
        LocalDateTime slotDateTime = date.atTime(time);
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            return "PAST";
        }
        String timeStr = time.format(TIME_FORMATTER);
        for (ReservationEntity reservation : venueReservations) {
            if (reservation.getDate().equals(date)) {
                LocalTime reservedStart = LocalTime.parse(reservation.getStartTime(), TIME_FORMATTER);
                LocalTime reservedEnd = LocalTime.parse(reservation.getEndTime(), TIME_FORMATTER);
                if (!(time.plusHours(1).isBefore(reservedStart) || time.isAfter(reservedEnd))) {
                    switch (reservation.getStatus()) {
                        case "IN_USE":
                            return "IN_USE";
                        case "CONFIRMED":
                            return "BOOKED";
                        case "PENDING":
                            return "PENDING";
                    }
                }
            }
        }
        return "AVAILABLE";
    }

    @Override
    public List<String> getVenueTypes() {
        return Arrays.asList(
            "basketball",
            "football",
            "badminton",
            "tennis",
            "swimming",
            "table_tennis"
        );
    }

    @Override
    public List<VenueScheduleDTO> getVenuesByType(String type) {
        LambdaQueryWrapper<VenueEntity> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            String dbType = type.toLowerCase();
            wrapper.eq(VenueEntity::getType, dbType);
        }
        return venueRepository.selectList(wrapper).stream()
                .map(venue -> {
                    VenueScheduleDTO dto = new VenueScheduleDTO();
                    dto.setVenueId(venue.getId());
                    dto.setVenueName(venue.getName());
                    dto.setType(venue.getType());
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 
package com.gymsys.service.venue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ReservationManagement {
    private Map<String, List<Reservation>> reservations = new HashMap<>();

    // 场地预约
    public boolean reserveVenue(String venueId, String cardNumber, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now().plusWeeks(1))) {
            return false;
        }
        Venue venue = findVenueById(venueId);
        if (venue != null && venue.isAvailable()) {
            Reservation reservation = new Reservation(cardNumber, startTime, endTime);
            reservations.computeIfAbsent(venueId, k -> new ArrayList<>()).add(reservation);
            venue.setAvailable(false);
            return true;
        }
        return false;
    }

    // 场地预约退订
    public boolean cancelReservation(String venueId, String cardNumber, LocalDateTime startTime) {
        List<Reservation> venueReservations = reservations.get(venueId);
        if (venueReservations != null) {
            boolean removed = venueReservations.removeIf(reservation -> reservation.getCardNumber().equals(cardNumber) && reservation.getStartTime().equals(startTime));
            if (removed) {
                Venue venue = findVenueById(venueId);
                if (venue != null) {
                    venue.setAvailable(true);
                }
                return true;
            }
        }
        return false;
    }

    // 场地预约修改
    public boolean modifyReservation(String venueId, String cardNumber, LocalDateTime oldStartTime, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        List<Reservation> venueReservations = reservations.get(venueId);
        if (venueReservations != null) {
            for (Reservation reservation : venueReservations) {
                if (reservation.getCardNumber().equals(cardNumber) && reservation.getStartTime().equals(oldStartTime)) {
                    reservation.setStartTime(newStartTime);
                    reservation.setEndTime(newEndTime);
                    return true;
                }
            }
        }
        return false;
    }

    // 场地预约失约处理
    public void handleNoShow(String venueId, String cardNumber, LocalDateTime startTime) {
        cancelReservation(venueId, cardNumber, startTime);
        // 可以添加额外的失约处理逻辑，如罚款等
    }

    // 校队预留（使用）场地
    public boolean reserveVenueForTeam(String venueId, LocalDateTime startTime, LocalDateTime endTime) {
        return reserveVenue(venueId, "TEAM", startTime, endTime);
    }

    // 上课使用场地
    public boolean reserveVenueForClass(String venueId, LocalDateTime startTime, LocalDateTime endTime) {
        return reserveVenue(venueId, "CLASS", startTime, endTime);
    }

    // 场地一周信息查询
    public List<Reservation> getWeeklyReservations(String venueId) {
        List<Reservation> venueReservations = reservations.get(venueId);
        if (venueReservations != null) {
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate oneWeekLater = now.plusWeeks(1);
            List<Reservation> weeklyReservations = new ArrayList<>();
            for (Reservation reservation : venueReservations) {
                if (!reservation.getStartTime().toLocalDate().isBefore(now) && !reservation.getStartTime().toLocalDate().isAfter(oneWeekLater)) {
                    weeklyReservations.add(reservation);
                }
            }
            return weeklyReservations;
        }
        return new ArrayList<>();
    }

    private Venue findVenueById(String venueId) {
        // 这里需要实现查找场地的逻辑
        return null;
    }
}
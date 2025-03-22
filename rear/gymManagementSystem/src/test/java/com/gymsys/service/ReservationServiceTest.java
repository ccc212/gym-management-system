package com.gymsys.service;

import com.gymsys.entity.ReservationEntity;
import com.gymsys.entity.VenueEntity;
import com.gymsys.repository.ReservationRepository;
import com.gymsys.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private ReservationService reservationService;

    private VenueEntity testVenue;
    private ReservationEntity testReservation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置测试场地
        testVenue = new VenueEntity();
        testVenue.setId(1L);
        testVenue.setName("测试场地");
        testVenue.setType("篮球场");
        testVenue.setPricePerHour(new BigDecimal("50.00"));
        testVenue.setAvailable(true);
        
        // 设置测试时间
        startTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        endTime = startTime.plusHours(2);
        
        // 设置测试预约
        testReservation = new ReservationEntity();
        testReservation.setId(1L);
        testReservation.setVenue(testVenue);
        testReservation.setCardNumber("12345678");
        testReservation.setStartTime(startTime);
        testReservation.setEndTime(endTime);
        testReservation.setReservationType("REGULAR");
        testReservation.setStatus("BOOKED");
    }

    @Test
    void testReserveVenue() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        when(reservationRepository.findByVenueAndStartTimeBetween(eq(testVenue), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(testReservation);
        
        ReservationEntity reservation = reservationService.reserveVenue(1L, "12345678", startTime, endTime);
        
        assertNotNull(reservation);
        assertEquals("12345678", reservation.getCardNumber());
        assertEquals(startTime, reservation.getStartTime());
        assertEquals(endTime, reservation.getEndTime());
        assertEquals("REGULAR", reservation.getReservationType());
        assertEquals("BOOKED", reservation.getStatus());
        
        verify(venueRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).findByVenueAndStartTimeBetween(eq(testVenue), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void testReserveVenue_VenueNotFound() {
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.reserveVenue(99L, "12345678", startTime, endTime);
        });
        
        assertEquals("场地不存在", exception.getMessage());
        verify(venueRepository, times(1)).findById(99L);
        verify(reservationRepository, never()).save(any(ReservationEntity.class));
    }

    @Test
    void testReserveVenue_TimeSlotNotAvailable() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        when(reservationRepository.findByVenueAndStartTimeBetween(eq(testVenue), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testReservation));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.reserveVenue(1L, "12345678", startTime, endTime);
        });
        
        assertEquals("该时段已被预约", exception.getMessage());
        verify(venueRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).findByVenueAndStartTimeBetween(eq(testVenue), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(reservationRepository, never()).save(any(ReservationEntity.class));
    }

    @Test
    void testCancelReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(testReservation);
        
        reservationService.cancelReservation(1L, "12345678");
        
        assertEquals("CANCELLED", testReservation.getStatus());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void testCancelReservation_InvalidCardNumber() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(1L, "87654321");
        });
        
        assertEquals("无权取消此预约", exception.getMessage());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(ReservationEntity.class));
    }

    @Test
    void testGetVenueWeeklyReservations() {
        LocalDateTime weekStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime weekEnd = weekStart.plusDays(7);
        
        when(reservationRepository.findVenueWeeklyReservations(1L, weekStart, weekEnd))
                .thenReturn(Arrays.asList(testReservation));
        
        List<ReservationEntity> reservations = reservationService.getVenueWeeklyReservations(1L);
        
        assertEquals(1, reservations.size());
        assertEquals(testReservation.getId(), reservations.get(0).getId());
        
        verify(reservationRepository, times(1)).findVenueWeeklyReservations(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testFindUserReservations() {
        when(reservationRepository.findByCardNumberAndStatus("12345678", "BOOKED"))
                .thenReturn(Arrays.asList(testReservation));
        
        List<ReservationEntity> reservations = reservationService.findUserReservations("12345678");
        
        assertEquals(1, reservations.size());
        assertEquals("12345678", reservations.get(0).getCardNumber());
        assertEquals("BOOKED", reservations.get(0).getStatus());
        
        verify(reservationRepository, times(1)).findByCardNumberAndStatus("12345678", "BOOKED");
    }
}
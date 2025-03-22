package com.gymsys.service.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.UsageEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.repository.venue.ReservationRepository;
import com.gymsys.repository.venue.UsageRepository;
import com.gymsys.repository.venue.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsageServiceTest {

    @Mock
    private UsageRepository usageRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private UsageService usageService;

    private VenueEntity testVenue;
    private ReservationEntity testReservation;
    private UsageEntity testUsage;

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
        LocalDateTime startTime = LocalDateTime.now().minusHours(2);
        LocalDateTime endTime = LocalDateTime.now();

        // 设置测试预约
        testReservation = new ReservationEntity();
        testReservation.setId(1L);
        testReservation.setVenue(testVenue);
        testReservation.setCardNumber("12345678");
        testReservation.setStartTime(startTime);
        testReservation.setEndTime(endTime);
        testReservation.setReservationType("REGULAR");
        testReservation.setStatus("BOOKED");

        // 设置测试使用记录
        testUsage = new UsageEntity();
        testUsage.setId(1L);
        testUsage.setVenue(testVenue);
        testUsage.setReservation(testReservation);
        testUsage.setCardNumber("12345678");
        testUsage.setStartTime(startTime);
        testUsage.setPaid(false);
    }

    @Test
    void testStartVenueUsage() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(usageRepository.save(any(UsageEntity.class))).thenReturn(testUsage);

        UsageEntity usage = usageService.startVenueUsage(1L, "12345678", 1L);

        assertNotNull(usage);
        assertEquals(testVenue, usage.getVenue());
        assertEquals(testReservation, usage.getReservation());
        assertEquals("12345678", usage.getCardNumber());

        verify(venueRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).findById(1L);
        verify(usageRepository, times(1)).save(any(UsageEntity.class));
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void testStartVenueUsage_VenueNotFound() {
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usageService.startVenueUsage(99L, "12345678", null);
        });

        assertEquals("场地不存在", exception.getMessage());
        verify(venueRepository, times(1)).findById(99L);
        verify(usageRepository, never()).save(any(UsageEntity.class));
    }

    @Test
    void testEndVenueUsage() {
        testUsage.setEndTime(null);

        when(usageRepository.findById(1L)).thenReturn(Optional.of(testUsage));
        when(usageRepository.save(any(UsageEntity.class))).thenReturn(testUsage);

        UsageEntity usage = usageService.endVenueUsage(1L);

        assertNotNull(usage);
        assertNotNull(usage.getEndTime());
        assertNotNull(usage.getCost());

        verify(usageRepository, times(1)).findById(1L);
        verify(usageRepository, times(1)).save(testUsage);
    }

    @Test
    void testEndVenueUsage_AlreadyEnded() {
        testUsage.setEndTime(LocalDateTime.now());

        when(usageRepository.findById(1L)).thenReturn(Optional.of(testUsage));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usageService.endVenueUsage(1L);
        });

        assertEquals("场地使用已经结束", exception.getMessage());
        verify(usageRepository, times(1)).findById(1L);
        verify(usageRepository, never()).save(any(UsageEntity.class));
    }

    @Test
    void testPayUsage() {
        testUsage.setEndTime(LocalDateTime.now());
        testUsage.setCost(new BigDecimal("100.00"));

        when(usageRepository.findById(1L)).thenReturn(Optional.of(testUsage));
        when(usageRepository.save(any(UsageEntity.class))).thenReturn(testUsage);

        UsageEntity usage = usageService.payUsage(1L);

        assertNotNull(usage);
        assertTrue(usage.getPaid());

        verify(usageRepository, times(1)).findById(1L);
        verify(usageRepository, times(1)).save(testUsage);
    }

    @Test
    void testPayUsage_AlreadyPaid() {
        testUsage.setEndTime(LocalDateTime.now());
        testUsage.setCost(new BigDecimal("100.00"));
        testUsage.setPaid(true);

        when(usageRepository.findById(1L)).thenReturn(Optional.of(testUsage));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usageService.payUsage(1L);
        });

        assertEquals("已经付费", exception.getMessage());
        verify(usageRepository, times(1)).findById(1L);
        verify(usageRepository, never()).save(any(UsageEntity.class));
    }

    @Test
    void testGetVenuePrice() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));

        BigDecimal price = usageService.getVenuePrice(1L);

        assertEquals(new BigDecimal("50.00"), price);
        verify(venueRepository, times(1)).findById(1L);
    }

    @Test
    void testGetActiveUsages() {
        when(usageRepository.findByEndTimeIsNull()).thenReturn(Arrays.asList(testUsage));

        List<UsageEntity> usages = usageService.getActiveUsages();

        assertEquals(1, usages.size());
        assertEquals(testUsage.getId(), usages.get(0).getId());

        verify(usageRepository, times(1)).findByEndTimeIsNull();
    }

    @Test
    void testGetUnpaidUsages() {
        when(usageRepository.findByCardNumberAndPaid("12345678", false))
                .thenReturn(Arrays.asList(testUsage));

        List<UsageEntity> usages = usageService.getUnpaidUsages("12345678");

        assertEquals(1, usages.size());
        assertEquals("12345678", usages.get(0).getCardNumber());
        assertFalse(usages.get(0).getPaid());

        verify(usageRepository, times(1)).findByCardNumberAndPaid("12345678", false);
    }
}
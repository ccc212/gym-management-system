package com.gymsys.service;

import com.gymsys.entity.VenueEntity;
import com.gymsys.repository.VenueRepository;
import com.gymsys.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    private VenueEntity testVenue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testVenue = new VenueEntity();
        testVenue.setId(1L);
        testVenue.setName("测试场地");
        testVenue.setType("篮球场");
        testVenue.setPricePerHour(new BigDecimal("50.00"));
        testVenue.setAvailable(true);
        testVenue.setCapacity(20);
        testVenue.setLocation("东区体育馆");
        testVenue.setDescription("标准篮球场");
    }

    @Test
    void testAddVenue() {
        when(venueRepository.save(any(VenueEntity.class))).thenReturn(testVenue);

        VenueEntity savedVenue = venueService.addVenue(testVenue);

        assertNotNull(savedVenue);
        assertEquals("测试场地", savedVenue.getName());
        assertEquals("篮球场", savedVenue.getType());
        verify(venueRepository, times(1)).save(any(VenueEntity.class));
    }

    @Test
    void testFindVenueById() {
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));

        Optional<VenueEntity> foundVenue = venueService.findVenueById(1L);

        assertTrue(foundVenue.isPresent());
        assertEquals("测试场地", foundVenue.get().getName());
        verify(venueRepository, times(1)).findById(1L);
    }

    @Test
    void testFindVenueById_NotFound() {
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<VenueEntity> foundVenue = venueService.findVenueById(99L);

        assertFalse(foundVenue.isPresent());
        verify(venueRepository, times(1)).findById(99L);
    }

    @Test
    void testFindAllVenues() {
        VenueEntity venue2 = new VenueEntity();
        venue2.setId(2L);
        venue2.setName("测试场地2");
        venue2.setType("网球场");

        when(venueRepository.findAll()).thenReturn(Arrays.asList(testVenue, venue2));

        List<VenueEntity> venues = venueService.findAllVenues();

        assertEquals(2, venues.size());
        assertEquals("测试场地", venues.get(0).getName());
        assertEquals("测试场地2", venues.get(1).getName());
        verify(venueRepository, times(1)).findAll();
    }

    @Test
    void testFindAvailableVenues() {
        when(venueRepository.findByIsAvailable(true)).thenReturn(Arrays.asList(testVenue));

        List<VenueEntity> venues = venueService.findAvailableVenues();

        assertEquals(1, venues.size());
        assertEquals("测试场地", venues.get(0).getName());
        verify(venueRepository, times(1)).findByIsAvailable(true);
    }

    @Test
    void testFindVenuesByType() {
        when(venueRepository.findByType("篮球场")).thenReturn(Arrays.asList(testVenue));

        List<VenueEntity> venues = venueService.findVenuesByType("篮球场");

        assertEquals(1, venues.size());
        assertEquals("篮球场", venues.get(0).getType());
        verify(venueRepository, times(1)).findByType("篮球场");
    }

    @Test
    void testRemoveVenue() {
        doNothing().when(venueRepository).deleteById(1L);

        venueService.removeVenue(1L);

        verify(venueRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateVenue() {
        testVenue.setName("更新后的场地名");
        when(venueRepository.save(testVenue)).thenReturn(testVenue);

        VenueEntity updatedVenue = venueService.updateVenue(testVenue);

        assertEquals("更新后的场地名", updatedVenue.getName());
        verify(venueRepository, times(1)).save(testVenue);
    }
}
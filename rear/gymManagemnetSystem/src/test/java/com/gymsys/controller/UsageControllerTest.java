package com.gymsys.controller;

import com.gymsys.entity.ReservationEntity;
import com.gymsys.entity.UsageEntity;
import com.gymsys.entity.VenueEntity;
import com.gymsys.service.UsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsageService usageService;

    @InjectMocks
    private UsageController usageController;

    private VenueEntity testVenue;
    private ReservationEntity testReservation;
    private UsageEntity testUsage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usageController).build();

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
    void testStartVenueUsage() throws Exception {
        when(usageService.startVenueUsage(eq(1L), eq("12345678"), any()))
                .thenReturn(testUsage);

        mockMvc.perform(post("/api/usages/start")
                .param("venueId", "1")
                .param("cardNumber", "12345678")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cardNumber", is("12345678")));

        verify(usageService, times(1)).startVenueUsage(eq(1L), eq("12345678"), any());
    }

    @Test
    void testEndVenueUsage() throws Exception {
        testUsage.setEndTime(LocalDateTime.now());
        testUsage.setCost(new BigDecimal("100.00"));
        
        when(usageService.endVenueUsage(1L)).thenReturn(testUsage);

        mockMvc.perform(post("/api/usages/1/end"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.endTime", notNullValue()))
                .andExpect(jsonPath("$.cost", notNullValue()));

        verify(usageService, times(1)).endVenueUsage(1L);
    }

    @Test
    void testPayUsage() throws Exception {
        testUsage.setEndTime(LocalDateTime.now());
        testUsage.setCost(new BigDecimal("100.00"));
        testUsage.setPaid(true);
        
        when(usageService.payUsage(1L)).thenReturn(testUsage);

        mockMvc.perform(post("/api/usages/1/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.paid", is(true)));

        verify(usageService, times(1)).payUsage(1L);
    }

    @Test
    void testGetVenuePrice() throws Exception {
        when(usageService.getVenuePrice(1L)).thenReturn(new BigDecimal("50.00"));

        mockMvc.perform(get("/api/usages/price/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pricePerHour", is(50.00)));

        verify(usageService, times(1)).getVenuePrice(1L);
    }

    @Test
    void testGetActiveUsages() throws Exception {
        List<UsageEntity> activeUsages = Arrays.asList(testUsage);
        
        when(usageService.getActiveUsages()).thenReturn(activeUsages);

        mockMvc.perform(get("/api/usages/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].cardNumber", is("12345678")));

        verify(usageService, times(1)).getActiveUsages();
    }

    @Test
    void testGetUnpaidUsages() throws Exception {
        List<UsageEntity> unpaidUsages = Arrays.asList(testUsage);
        
        when(usageService.getUnpaidUsages("12345678")).thenReturn(unpaidUsages);

        mockMvc.perform(get("/api/usages/unpaid/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].cardNumber", is("12345678")));

        verify(usageService, times(1)).getUnpaidUsages("12345678");
    }
}
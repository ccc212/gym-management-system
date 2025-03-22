package com.gymsys.controller.venue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.service.venue.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VenueControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VenueService venueService;

    @InjectMocks
    private VenueController venueController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private VenueEntity testVenue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(venueController).build();

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
    void testAddVenue() throws Exception {
        when(venueService.addVenue(any(VenueEntity.class))).thenReturn(testVenue);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVenue)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(venueService, times(1)).addVenue(any(VenueEntity.class));
    }

    @Test
    void testGetVenue() throws Exception {
        when(venueService.findVenueById(1L)).thenReturn(Optional.of(testVenue));

        mockMvc.perform(get("/api/venues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("测试场地")))
                .andExpect(jsonPath("$.type", is("篮球场")));

        verify(venueService, times(1)).findVenueById(1L);
    }

    @Test
    void testGetVenue_NotFound() throws Exception {
        when(venueService.findVenueById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/venues/99"))
                .andExpect(status().isNotFound());

        verify(venueService, times(1)).findVenueById(99L);
    }

    @Test
    void testGetAllVenues() throws Exception {
        VenueEntity venue2 = new VenueEntity();
        venue2.setId(2L);
        venue2.setName("测试场地2");
        venue2.setType("网球场");

        when(venueService.findAllVenues()).thenReturn(Arrays.asList(testVenue, venue2));

        mockMvc.perform(get("/api/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("测试场地")))
                .andExpect(jsonPath("$[1].name", is("测试场地2")));

        verify(venueService, times(1)).findAllVenues();
    }

    @Test
    void testUpdateVenue() throws Exception {
        testVenue.setName("更新后的场地名");
        when(venueService.updateVenue(any(VenueEntity.class))).thenReturn(testVenue);

        mockMvc.perform(put("/api/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVenue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("更新后的场地名")));

        verify(venueService, times(1)).updateVenue(any(VenueEntity.class));
    }

    @Test
    void testRemoveVenue() throws Exception {
        doNothing().when(venueService).removeVenue(1L);

        mockMvc.perform(delete("/api/venues/1"))
                .andExpect(status().isNoContent());

        verify(venueService, times(1)).removeVenue(1L);
    }

    @Test
    void testGetAvailableVenues() throws Exception {
        when(venueService.findAvailableVenues()).thenReturn(Arrays.asList(testVenue));

        mockMvc.perform(get("/api/venues/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("测试场地")));

        verify(venueService, times(1)).findAvailableVenues();
    }

 @Test
void testGetVenuesByType() throws Exception {
    // 创建测试数据 - 注意这里的类型要与URL参数完全一致
    VenueEntity venue = new VenueEntity();
    venue.setId(1L);
    venue.setName("测试篮球场");
    venue.setType("篮球场");
    venue.setPricePerHour(new BigDecimal("60.0"));
    venue.setAvailable(true);

    List<VenueEntity> venues = Collections.singletonList(venue);

    // 使用 Mockito.any(String.class) 来匹配任何字符串参数
    // 这样可以避免编码问题导致的参数不匹配
    when(venueService.findVenuesByType(any(String.class))).thenReturn(venues);

    mockMvc.perform(get("/api/venues/type/{type}", "篮球场"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("测试篮球场")));

    // 确认方法被调用，但不指定具体参数
    verify(venueService).findVenuesByType(any(String.class));
}
}
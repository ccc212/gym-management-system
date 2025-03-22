package com.gymsys;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GymApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // 测试应用程序上下文能否正常加载
    }

    @Test
    void testVenueEndpoint() throws Exception {
        // 测试场地API端点可以访问
        mockMvc.perform(get("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testReservationEndpoint() throws Exception {
        // 测试预约API端点可以访问
        mockMvc.perform(get("/api/reservations/user/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAnnouncementEndpoint() throws Exception {
        // 测试公告API端点可以访问
        mockMvc.perform(get("/api/announcements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
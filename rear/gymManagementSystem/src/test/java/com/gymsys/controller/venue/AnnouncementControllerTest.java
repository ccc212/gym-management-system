package com.gymsys.controller.venue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymsys.entity.venue.AnnouncementEntity;
import com.gymsys.service.venue.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 公告控制器测试类
 * 用于测试公告相关的API接口功能
 */
class AnnouncementControllerTest {

    /**
     * MockMvc实例，用于模拟HTTP请求和响应6
     */
    private MockMvc mockMvc;

    /**
     * 模拟的公告服务对象
     * @Mock注解用于创建模拟对象
     */
    @Mock
    private AnnouncementService announcementService;

    /**
     * 被测试的公告控制器对象
     * @InjectMocks注解会将模拟对象注入到控制器中
     */
    @InjectMocks
    private AnnouncementController announcementController;

    /**
     * JSON对象映射器，用于对象与JSON字符串的转换
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 测试用的公告实体对象
     */
    private AnnouncementEntity testAnnouncement;

    /**
     * 测试前的初始化方法
     * 在每个测试方法执行前都会运行此方法
     */
    @BeforeEach
    void setUp() {
        // 初始化所有使用@Mock注解的模拟对象
        MockitoAnnotations.openMocks(this);
        // 构建MockMvc实例，设置控制器
        mockMvc = MockMvcBuilders.standaloneSetup(announcementController).build();

        // 配置ObjectMapper以支持Java 8日期/时间类型
        // 这是解决LocalDateTime序列化问题的关键
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 创建用于测试的公告实体对象
        testAnnouncement = new AnnouncementEntity();
        testAnnouncement.setId(1L);
        testAnnouncement.setTitle("测试公告");
        testAnnouncement.setContent("这是一条测试公告内容");
        testAnnouncement.setPublishTime(LocalDateTime.now().minusDays(1)); // 发布时间设为昨天
        testAnnouncement.setExpireTime(LocalDateTime.now().plusDays(7));   // 过期时间设为7天后
        testAnnouncement.setActive(true);                                  // 设置为活动状态
    }

    /**
     * 测试创建公告API
     * 验证POST /api/announcements接口
     */
    @Test
    void testCreateAnnouncement() throws Exception {
        // 模拟服务层方法，当调用createAnnouncement时返回测试公告对象
        when(announcementService.createAnnouncement(any(AnnouncementEntity.class))).thenReturn(testAnnouncement);

        // 执行POST请求，发送JSON格式的公告对象
        mockMvc.perform(post("/api/announcements")                           // 执行POST请求
                .contentType(MediaType.APPLICATION_JSON)                     // 设置Content-Type为application/json
                .content(objectMapper.writeValueAsString(testAnnouncement))) // 将测试公告对象转换为JSON字符串
                .andExpect(status().isCreated())                             // 期望响应状态码为201 Created
                .andExpect(jsonPath("$.id", is(1)))                          // 验证返回的JSON中id字段为1
                .andExpect(jsonPath("$.title", is("测试公告")));              // 验证返回的JSON中title字段为"测试公告"

        // 验证服务层的createAnnouncement方法被调用了一次
        verify(announcementService, times(1)).createAnnouncement(any(AnnouncementEntity.class));
    }

    /**
     * 测试获取活动公告列表API
     * 验证GET /api/announcements接口
     */
    @Test
    void testGetActiveAnnouncements() throws Exception {
        // 创建包含测试公告的列表
        List<AnnouncementEntity> announcements = Arrays.asList(testAnnouncement);
        
        // 模拟服务层方法，当调用getActiveAnnouncements时返回公告列表
        when(announcementService.getActiveAnnouncements()).thenReturn(announcements);

        // 执行GET请求获取活动公告列表
        mockMvc.perform(get("/api/announcements"))                // 执行GET请求
                .andExpect(status().isOk())                       // 期望响应状态码为200 OK
                .andExpect(jsonPath("$", hasSize(1)))             // 验证返回的JSON数组大小为1
                .andExpect(jsonPath("$[0].id", is(1)))            // 验证第一个元素的id为1
                .andExpect(jsonPath("$[0].title", is("测试公告"))); // 验证第一个元素的title为"测试公告"

        // 验证服务层的getActiveAnnouncements方法被调用了一次
        verify(announcementService, times(1)).getActiveAnnouncements();
    }

    /**
     * 测试停用公告API
     * 验证POST /api/announcements/{id}/deactivate接口
     */
    @Test
    void testDeactivateAnnouncement() throws Exception {
        // 模拟服务层方法，当调用deactivateAnnouncement时不执行任何操作
        doNothing().when(announcementService).deactivateAnnouncement(1L);

        // 执行POST请求停用指定ID的公告
        mockMvc.perform(post("/api/announcements/1/deactivate")) // 执行POST请求
                .andExpect(status().isNoContent());              // 期望响应状态码为204 No Content

        // 验证服务层的deactivateAnnouncement方法被调用了一次，参数为1L
        verify(announcementService, times(1)).deactivateAnnouncement(1L);
    }

    /**
     * 测试获取最新公告内容API
     * 验证GET /api/announcements/late    st接口
     */
    @Test
    void testGetLatestAnnouncement() throws Exception {
        // 模拟服务层方法，当调用getLatestAnnouncementContent时返回测试公告内容
        when(announcementService.getLatestAnnouncementContent()).thenReturn("这是一条测试公告内容");

            // 执行GET请求获取最新公告
        mockMvc.perform(get("/api/announcements/latest"))                      // 执行GET请求
                .andExpect(status().isOk())                                    // 期望响应状态码为200 OK
                .andExpect(jsonPath("$.content", is("这是一条测试公告内容")));    // 验证返回的JSON中content字段

        // 验证服务层的getLatestAnnouncementContent方法被调用了一次
        verify(announcementService, times(1)).getLatestAnnouncementContent();
    }
}
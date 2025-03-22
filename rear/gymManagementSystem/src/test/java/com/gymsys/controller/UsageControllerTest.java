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

/**
 * 该类用于对 UsageController 进行单元测试。
 * 通过 Mockito 框架模拟依赖服务，并使用 Spring 的 MockMvc 进行 HTTP 请求的模拟。
 */
class UsageControllerTest {

    // 用于模拟 HTTP 请求和响应的工具
    private MockMvc mockMvc;

    // 使用 Mockito 模拟 UsageService 服务
    @Mock
    private UsageService usageService;

    // 将模拟的 UsageService 注入到 UsageController 中
    @InjectMocks
    private UsageController usageController;

    // 测试用的场地实体
    private VenueEntity testVenue;
    // 测试用的预约实体
    private ReservationEntity testReservation;
    // 测试用的使用记录实体
    private UsageEntity testUsage;

    /**
     * 在每个测试方法执行前进行初始化操作。
     * 包括初始化 Mockito 模拟对象、设置 MockMvc 以及创建测试用的实体对象。
     */
    @BeforeEach
    void setUp() {
        // 初始化 Mockito 模拟对象
        MockitoAnnotations.openMocks(this);
        // 创建 MockMvc 实例，用于模拟 HTTP 请求
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

    /**
     * 测试开始场地使用的功能。
     * 模拟调用 startVenueUsage 方法，并验证返回结果和服务方法的调用次数。
     * @throws Exception 如果模拟 HTTP 请求过程中出现异常
     */
    @Test
    void testStartVenueUsage() throws Exception {
        // 当调用 usageService 的 startVenueUsage 方法时，返回测试用的使用记录
        when(usageService.startVenueUsage(eq(1L), eq("12345678"), any()))
                .thenReturn(testUsage);

        // 模拟发送 POST 请求到 /api/usages/start 接口
        mockMvc.perform(post("/api/usages/start")
                .param("venueId", "1")
                .param("cardNumber", "12345678")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                // 验证响应状态码为 201 Created
                .andExpect(status().isCreated())
                // 验证返回的 JSON 数据中的 id 字段为 1
                .andExpect(jsonPath("$.id", is(1)))
                // 验证返回的 JSON 数据中的 cardNumber 字段为 "12345678"
                .andExpect(jsonPath("$.cardNumber", is("12345678")));

        // 验证 usageService 的 startVenueUsage 方法是否被调用了 1 次
        verify(usageService, times(1)).startVenueUsage(eq(1L), eq("12345678"), any());
    }

    /**
     * 测试结束场地使用的功能。
     * 模拟调用 endVenueUsage 方法，并验证返回结果和服务方法的调用次数。
     * @throws Exception 如果模拟 HTTP 请求过程中出现异常
     */
    @Test
    void testEndVenueUsage() throws Exception {
        // 设置测试使用记录的结束时间和费用
        testUsage.setEndTime(LocalDateTime.now());
        testUsage.setCost(new BigDecimal("100.00"));
        
        // 当调用 usageService 的 endVenueUsage 方法时，返回测试用的使用记录
        when(usageService.endVenueUsage(1L)).thenReturn(testUsage);

        // 模拟发送 POST 请求到 /api/usages/1/end 接口
        mockMvc.perform(post("/api/usages/1/end"))
                // 验证响应状态码为 200 OK
                .andExpect(status().isOk())
                // 验证返回的 JSON 数据中的 id 字段为 1
                .andExpect(jsonPath("$.id", is(1)))
                // 验证返回的 JSON 数据中的 endTime 字段不为空
                .andExpect(jsonPath("$.endTime", notNullValue()))
                // 验证返回的 JSON 数据中的 cost 字段不为空
                .andExpect(jsonPath("$.cost", notNullValue()));

        // 验证 usageService 的 endVenueUsage 方法是否被调用了 1 次
        verify(usageService, times(1)).endVenueUsage(1L);
    }

    /**
     * 测试支付使用费用的功能。
     * 模拟调用 payUsage 方法，并验证返回结果和服务方法的调用次数。
     * @throws Exception 如果模拟 HTTP 请求过程中出现异常
     */
    @Test
    void testPayUsage() throws Exception {
        // 设置测试使用记录的结束时间、费用和支付状态
        testUsage.setEndTime(LocalDateTime.now());
        testUsage.setCost(new BigDecimal("100.00"));
        testUsage.setPaid(true);
        
        // 当调用 usageService 的 payUsage 方法时，返回测试用的使用记录
        when(usageService.payUsage(1L)).thenReturn(testUsage);

        // 模拟发送 POST 请求到 /api/usages/1/pay 接口
        mockMvc.perform(post("/api/usages/1/pay"))
                // 验证响应状态码为 200 OK
                .andExpect(status().isOk())
                // 验证返回的 JSON 数据中的 id 字段为 1
                .andExpect(jsonPath("$.id", is(1)))
                // 验证返回的 JSON 数据中的 paid 字段为 true
                .andExpect(jsonPath("$.paid", is(true)));

        // 验证 usageService 的 payUsage 方法是否被调用了 1 次
        verify(usageService, times(1)).payUsage(1L);
    }

    /**
     * 测试获取场地价格的功能。
     * 模拟调用 getVenuePrice 方法，并验证返回结果和服务方法的调用次数。
     * @throws Exception 如果模拟 HTTP 请求过程中出现异常
     */
    @Test
    void testGetVenuePrice() throws Exception {
        // 当调用 usageService 的 getVenuePrice 方法时，返回测试用的场地价格
        when(usageService.getVenuePrice(1L)).thenReturn(new BigDecimal("50.00"));

        // 模拟发送 GET 请求到 /api/usages/price/1 接口
        mockMvc.perform(get("/api/usages/price/1"))
                // 验证响应状态码为 200 OK
                .andExpect(status().isOk())
                // 验证返回的 JSON 数据中的 pricePerHour 字段为 50.00
                .andExpect(jsonPath("$.pricePerHour", is(50.00)));

        // 验证 usageService 的 getVenuePrice 方法是否被调用了 1 次
        verify(usageService, times(1)).getVenuePrice(1L);
    }

    /**
     * 测试获取活跃使用记录的功能。
     * 模拟调用 getActiveUsages 方法，并验证返回结果和服务方法的调用次数。
     * @throws Exception 如果模拟 HTTP 请求过程中出现异常
     */
    @Test
    void testGetActiveUsages() throws Exception {
        // 创建包含测试使用记录的列表
        List<UsageEntity> activeUsages = Arrays.asList(testUsage);
        
        // 当调用 usageService 的 getActiveUsages 方法时，返回活跃使用记录列表
        when(usageService.getActiveUsages()).thenReturn(activeUsages);

        // 模拟发送 GET 请求到 /api/usages/active 接口
        mockMvc.perform(get("/api/usages/active"))
                // 验证响应状态码为 200 OK
                .andExpect(status().isOk())
                // 验证返回的 JSON 数据列表长度为 1
                .andExpect(jsonPath("$", hasSize(1)))
                // 验证返回的 JSON 数据列表中第一个元素的 id 字段为 1
                .andExpect(jsonPath("$[0].id", is(1)))
                // 验证返回的 JSON 数据列表中第一个元素的 cardNumber 字段为 "12345678"
                .andExpect(jsonPath("$[0].cardNumber", is("12345678")));

        // 验证 usageService 的 getActiveUsages 方法是否被调用了 1 次
        verify(usageService, times(1)).getActiveUsages();
    }

    /**
     * 测试获取未支付使用记录的功能。
     * 模拟调用 getUnpaidUsages 方法，并验证返回结果和服务方法的调用次数。
     * @throws Exception 如果模拟 HTTP 请求过程中出现异常
     */
    @Test
    void testGetUnpaidUsages() throws Exception {
        // 创建包含测试使用记录的列表
        List<UsageEntity> unpaidUsages = Arrays.asList(testUsage);
        
        // 当调用 usageService 的 getUnpaidUsages 方法时，返回未支付使用记录列表
        when(usageService.getUnpaidUsages("12345678")).thenReturn(unpaidUsages);

        // 模拟发送 GET 请求到 /api/usages/unpaid/12345678 接口
        mockMvc.perform(get("/api/usages/unpaid/12345678"))
                // 验证响应状态码为 200 OK
                .andExpect(status().isOk())
                // 验证返回的 JSON 数据列表长度为 1
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].cardNumber", is("12345678")));

        // 验证 usageService 的 getUnpaidUsages 方法是否被调用了 1 次
        verify(usageService, times(1)).getUnpaidUsages("12345678");
    }
}
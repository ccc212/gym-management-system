package com.gymsys.controller.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import com.gymsys.service.venue.ReservationService;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 场地预约控制器测试类
 * 用于测试场地预约相关的API接口功能
 */
class ReservationControllerTest {

    /**
     * MockMvc实例，用于模拟HTTP请求和响应
     */
    private MockMvc mockMvc;

    /**
     * 模拟的预约服务对象
     * @Mock注解用于创建模拟对象
     */
    @Mock
    private ReservationService reservationService;

    /**
     * 被测试的预约控制器对象
     * @InjectMocks注解会将模拟对象注入到控制器中
     */
    @InjectMocks
    private ReservationController reservationController;

    /**
     * 测试用的场地实体对象
     */
    private VenueEntity testVenue;

    /**
     * 测试用的预约实体对象
     */
    private ReservationEntity testReservation;

    /**
     * 测试预约的开始时间
     */
    private LocalDateTime startTime;

    /**
     * 测试预约的结束时间
     */
    private LocalDateTime endTime;

    /**
     * 日期时间格式化器，用于将LocalDateTime转换为ISO标准的字符串
     */
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * 测试前的初始化方法
     * 在每个测试方法执行前都会运行此方法
     */
    @BeforeEach
    void setUp() {
        // 初始化所有使用@Mock注解的模拟对象
        MockitoAnnotations.openMocks(this);
        // 构建MockMvc实例，设置控制器
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();

        // 设置测试场地
        testVenue = new VenueEntity();
        testVenue.setId(1L);                                 // 设置场地ID
        testVenue.setName("测试场地");                         // 设置场地名称
        testVenue.setType("篮球场");                           // 设置场地类型
        testVenue.setPricePerHour(new BigDecimal("50.00"));  // 设置每小时价格
        testVenue.setAvailable(true);                        // 设置场地可用状态

        // 设置测试时间
        startTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);  // 明天下午2点开始
        endTime = startTime.plusHours(2);                                        // 预约2小时，到下午4点结束

        // 设置测试预约
        testReservation = new ReservationEntity();
        testReservation.setId(1L);                    // 设置预约ID
        testReservation.setVenue(testVenue);          // 关联预约场地
        testReservation.setCardNumber("12345678");    // 设置会员卡号
        testReservation.setStartTime(startTime);      // 设置开始时间
        testReservation.setEndTime(endTime);          // 设置结束时间
        testReservation.setReservationType("REGULAR"); // 设置预约类型为普通预约
        testReservation.setStatus("BOOKED");          // 设置预约状态为已预约
    }

    /**
     * 测试预约场地API
     * 验证POST /api/reservations接口
     */
    @Test
    void testReserveVenue() throws Exception {
        // 模拟服务层方法，当调用reserveVenue时返回测试预约对象
        when(reservationService.reserveVenue(eq(1L), eq("12345678"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testReservation);

        // 执行POST请求创建预约
        mockMvc.perform(post("/api/reservations")                         // 执行POST请求
                .param("venueId", "1")                                    // 设置场地ID参数
                .param("cardNumber", "12345678")                          // 设置会员卡号参数
                .param("startTime", startTime.format(formatter))          // 设置开始时间参数，格式化为ISO标准
                .param("endTime", endTime.format(formatter))              // 设置结束时间参数，格式化为ISO标准
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))      // 设置Content-Type为表单类型
                .andExpect(status().isCreated())                          // 期望响应状态码为201 Created
                .andExpect(jsonPath("$.id", is(1)))                       // 验证返回的JSON中id字段为1
                .andExpect(jsonPath("$.cardNumber", is("12345678")));     // 验证返回的JSON中cardNumber字段

        // 验证服务层的reserveVenue方法被调用了一次，并且参数匹配
        verify(reservationService, times(1))
                .reserveVenue(eq(1L), eq("12345678"), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试取消预约API
     * 验证POST /api/reservations/{id}/cancel接口
     */
    @Test
    void testCancelReservation() throws Exception {
        // 模拟服务层方法，当调用cancelReservation时不执行任何操作
        doNothing().when(reservationService).cancelReservation(1L, "12345678");

        // 执行POST请求取消指定ID的预约
        mockMvc.perform(post("/api/reservations/1/cancel")                // 执行POST请求
                .param("cardNumber", "12345678")                           // 设置会员卡号参数
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))       // 设置Content-Type为表单类型
                .andExpect(status().isNoContent());                        // 期望响应状态码为204 No Content

        // 验证服务层的cancelReservation方法被调用了一次，参数为1L和"12345678"
        verify(reservationService, times(1)).cancelReservation(1L, "12345678");
    }

    /**
     * 测试修改预约API
     * 验证PUT /api/reservations/{id}接口
     */
    @Test
    void testModifyReservation() throws Exception {
        // 模拟服务层方法，当调用modifyReservation时返回更新后的测试预约对象
        when(reservationService.modifyReservation(eq(1L), eq("12345678"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testReservation);

        // 执行PUT请求修改指定ID的预约
        mockMvc.perform(put("/api/reservations/1")                        // 执行PUT请求
                .param("cardNumber", "12345678")                          // 设置会员卡号参数
                .param("startTime", startTime.format(formatter))          // 设置新开始时间参数
                .param("endTime", endTime.format(formatter))              // 设置新结束时间参数
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))      // 设置Content-Type为表单类型
                .andExpect(status().isOk())                               // 期望响应状态码为200 OK
                .andExpect(jsonPath("$.id", is(1)))                       // 验证返回的JSON中id字段为1
                .andExpect(jsonPath("$.cardNumber", is("12345678")));     // 验证返回的JSON中cardNumber字段

        // 验证服务层的modifyReservation方法被调用了一次，并且参数匹配
        verify(reservationService, times(1))
                .modifyReservation(eq(1L), eq("12345678"), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试处理用户未到场API
     * 验证POST /api/reservations/{id}/no-show接口
     */
    @Test
    void testHandleNoShow() throws Exception {
        // 模拟服务层方法，当调用handleNoShow时不执行任何操作
        doNothing().when(reservationService).handleNoShow(1L);

        // 执行POST请求标记指定ID的预约为未到场
        mockMvc.perform(post("/api/reservations/1/no-show"))              // 执行POST请求
                .andExpect(status().isNoContent());                        // 期望响应状态码为204 No Content

        // 验证服务层的handleNoShow方法被调用了一次，参数为1L
        verify(reservationService, times(1)).handleNoShow(1L);
    }

    /**
     * 测试获取场地周预约API
     * 验证GET /api/reservations/venue/{id}/weekly接口
     */
    @Test
    void testGetVenueWeeklyReservations() throws Exception {
        // 模拟服务层方法，当调用getVenueWeeklyReservations时返回包含测试预约的列表
        when(reservationService.getVenueWeeklyReservations(1L))
                .thenReturn(Arrays.asList(testReservation));

        // 执行GET请求获取指定场地的周预约情况
        mockMvc.perform(get("/api/reservations/venue/1/weekly"))          // 执行GET请求
                .andExpect(status().isOk())                               // 期望响应状态码为200 OK
                .andExpect(jsonPath("$", hasSize(1)))                     // 验证返回的JSON数组大小为1
                .andExpect(jsonPath("$[0].id", is(1)))                    // 验证第一个元素的id为1
                .andExpect(jsonPath("$[0].cardNumber", is("12345678")));  // 验证第一个元素的cardNumber字段

        // 验证服务层的getVenueWeeklyReservations方法被调用了一次，参数为1L
        verify(reservationService, times(1)).getVenueWeeklyReservations(1L);
    }

    /**
     * 测试获取用户预约列表API
     * 验证GET /api/reservations/user/{cardNumber}接口
     */
    @Test
    void testGetUserReservations() throws Exception {
        // 模拟服务层方法，当调用findUserReservations时返回包含测试预约的列表
        when(reservationService.findUserReservations("12345678"))
                .thenReturn(Arrays.asList(testReservation));

        // 执行GET请求获取指定用户的预约列表
        mockMvc.perform(get("/api/reservations/user/12345678"))           // 执行GET请求
                .andExpect(status().isOk())                               // 期望响应状态码为200 OK
                .andExpect(jsonPath("$", hasSize(1)))                     // 验证返回的JSON数组大小为1
                .andExpect(jsonPath("$[0].id", is(1)))                    // 验证第一个元素的id为1
                .andExpect(jsonPath("$[0].cardNumber", is("12345678")));  // 验证第一个元素的cardNumber字段

        // 验证服务层的findUserReservations方法被调用了一次，参数为"12345678"
        verify(reservationService, times(1)).findUserReservations("12345678");
    }

    /**
     * 测试获取单个预约详情API
     * 验证GET /api/reservations/{id}接口 - 成功情况
     */
    @Test
    void testGetReservation() throws Exception {
        // 模拟服务层方法，当调用findReservationById时返回包含测试预约的Optional对象
        when(reservationService.findReservationById(1L)).thenReturn(Optional.of(testReservation));

        // 执行GET请求获取指定ID的预约详情
        mockMvc.perform(get("/api/reservations/1"))                       // 执行GET请求
                .andExpect(status().isOk())                               // 期望响应状态码为200 OK
                .andExpect(jsonPath("$.id", is(1)))                       // 验证返回的JSON中id字段为1
                .andExpect(jsonPath("$.cardNumber", is("12345678")));     // 验证返回的JSON中cardNumber字段

        // 验证服务层的findReservationById方法被调用了一次，参数为1L
        verify(reservationService, times(1)).findReservationById(1L);
    }

    /**
     * 测试获取单个预约详情API - 预约不存在的情况
     * 验证GET /api/reservations/{id}接口 - 失败情况
     */
    @Test
    void testGetReservation_NotFound() throws Exception {
        // 模拟服务层方法，当调用findReservationById时返回空的Optional对象，表示预约不存在
        when(reservationService.findReservationById(99L)).thenReturn(Optional.empty());

        // 执行GET请求获取不存在的预约ID
        mockMvc.perform(get("/api/reservations/99"))                     // 执行GET请求
                .andExpect(status().isNotFound());                        // 期望响应状态码为404 Not Found

        // 验证服务层的findReservationById方法被调用了一次，参数为99L
        verify(reservationService, times(1)).findReservationById(99L);
    }
}
package com.gymsys.repository.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.VenueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VenueRepository venueRepository;

    private VenueEntity testVenue;

    @BeforeEach
    public void setup() {
        // 清空数据
        reservationRepository.deleteAll();
        venueRepository.deleteAll();

        // 创建测试场地
        testVenue = VenueEntity.builder()
                .name("测试场地")
                .type("羽毛球场")
                .pricePerHour(new BigDecimal("50.0"))
                .isAvailable(true)
                .location("一楼东侧")
                .capacity(4)
                .build();

        testVenue = venueRepository.save(testVenue);
    }

    @Test
    public void testSaveReservation() {
        // 创建测试实体
        ReservationEntity reservation = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("1001")
                .startTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .endTime(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS))
                .reservationType("REGULAR")
                .status("BOOKED")
                .remark("测试预约")
                .build();

        // 保存到数据库
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        // 验证保存成功并分配了ID
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getVenue().getId()).isEqualTo(testVenue.getId());
        assertThat(savedReservation.getCardNumber()).isEqualTo("1001");
        assertThat(savedReservation.getStatus()).isEqualTo("BOOKED");
    }

    @Test
    public void testFindById() {
        // 创建测试实体
        ReservationEntity reservation = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("1002")
                .startTime(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS))
                .endTime(LocalDateTime.now().plusHours(4).truncatedTo(ChronoUnit.SECONDS))
                .reservationType("TEAM")
                .status("BOOKED")
                .build();

        // 保存到数据库
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        // 根据ID查询
        Optional<ReservationEntity> foundReservation = reservationRepository.findById(savedReservation.getId());

        // 验证查询结果
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation.get().getCardNumber()).isEqualTo("1002");
        assertThat(foundReservation.get().getReservationType()).isEqualTo("TEAM");
    }

    @Test
    public void testFindByVenueAndStartTimeBetween() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 创建测试实体
        ReservationEntity reservation1 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("1003")
                .startTime(now.plusHours(1))
                .endTime(now.plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation2 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("1004")
                .startTime(now.plusHours(2))
                .endTime(now.plusHours(3))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation3 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("1005")
                .startTime(now.plusHours(5))
                .endTime(now.plusHours(6))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        // 保存到数据库
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        // 查询特定时间范围的预约
        List<ReservationEntity> foundReservations = reservationRepository
                .findByVenueAndStartTimeBetween(testVenue, now, now.plusHours(4));

        // 验证结果
        assertThat(foundReservations).hasSize(2);
        assertThat(foundReservations)
                .extracting(ReservationEntity::getCardNumber)
                .containsExactlyInAnyOrder("1003", "1004");
    }

    @Test
    public void testFindByCardNumberAndStartTimeBetween() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 创建测试实体 - 同一会员卡号不同场地的预约
        ReservationEntity reservation1 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("2001")
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(1).plusHours(1))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation2 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("2001")
                .startTime(now.plusDays(2))
                .endTime(now.plusDays(2).plusHours(1))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation3 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("2002")
                .startTime(now.plusDays(1).plusHours(2))
                .endTime(now.plusDays(1).plusHours(3))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        // 保存到数据库
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        // 查询特定会员特定时间范围的预约
        List<ReservationEntity> foundReservations = reservationRepository
                .findByCardNumberAndStartTimeBetween("2001", now, now.plusDays(5));

        // 验证结果
        assertThat(foundReservations).hasSize(2);
        assertThat(foundReservations)
                .extracting(ReservationEntity::getStartTime)
                .containsExactlyInAnyOrder(now.plusDays(1), now.plusDays(2));
    }

    @Test
    public void testFindVenueWeeklyReservations() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime weekStart = now.plusDays(1);
        LocalDateTime weekEnd = now.plusDays(8);

        // 创建测试实体
        ReservationEntity reservation1 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("3001")
                .startTime(weekStart.plusHours(1))
                .endTime(weekStart.plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation2 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("3002")
                .startTime(weekStart.plusDays(2))
                .endTime(weekStart.plusDays(2).plusHours(1))
                .reservationType("TEAM")
                .status("BOOKED")
                .build();

        ReservationEntity reservation3 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("3003")
                .startTime(weekEnd.plusHours(1))
                .endTime(weekEnd.plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        // 保存到数据库
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        // 查询场地周预约
        List<ReservationEntity> weeklyReservations = reservationRepository
                .findVenueWeeklyReservations(testVenue.getId(), weekStart, weekEnd);

        // 验证结果
        assertThat(weeklyReservations).hasSize(2);
        assertThat(weeklyReservations)
                .extracting(ReservationEntity::getCardNumber)
                .containsExactlyInAnyOrder("3001", "3002");
    }

    @Test
    public void testFindByCardNumberAndStatus() {
        // 创建测试实体 - 同一会员不同状态的预约
        ReservationEntity reservation1 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("4001")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation2 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("4001")
                .startTime(LocalDateTime.now().plusHours(3))
                .endTime(LocalDateTime.now().plusHours(4))
                .reservationType("REGULAR")
                .status("CANCELLED")
                .build();

        ReservationEntity reservation3 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("4001")
                .startTime(LocalDateTime.now().plusHours(5))
                .endTime(LocalDateTime.now().plusHours(6))
                .reservationType("TEAM")
                .status("BOOKED")
                .build();

        // 保存到数据库
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        // 查询特定会员特定状态的预约
        List<ReservationEntity> bookedReservations = reservationRepository
                .findByCardNumberAndStatus("4001", "BOOKED");

        // 验证结果
        assertThat(bookedReservations).hasSize(2);
        assertThat(bookedReservations)
                .extracting(ReservationEntity::getReservationType)
                .containsExactlyInAnyOrder("REGULAR", "TEAM");
    }

    @Test
    public void testFindByReservationType() {
        // 创建测试实体 - 不同类型的预约
        ReservationEntity reservation1 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("5001")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        ReservationEntity reservation2 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("5002")
                .startTime(LocalDateTime.now().plusHours(3))
                .endTime(LocalDateTime.now().plusHours(4))
                .reservationType("TEAM")
                .status("BOOKED")
                .build();

        ReservationEntity reservation3 = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("5003")
                .startTime(LocalDateTime.now().plusHours(5))
                .endTime(LocalDateTime.now().plusHours(6))
                .reservationType("CLASS")
                .status("BOOKED")
                .build();

        // 保存到数据库
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        // 查询特定类型的预约
        List<ReservationEntity> teamReservations = reservationRepository
                .findByReservationType("TEAM");

        // 验证结果
        assertThat(teamReservations).hasSize(1);
        assertThat(teamReservations.get(0).getCardNumber()).isEqualTo("5002");
    }

    @Test
    public void testUpdateReservation() {
        // 创建测试实体
        ReservationEntity reservation = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("6001")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .remark("原始备注")
                .build();

        // 保存到数据库
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        // 更新属性
        savedReservation.setStatus("CANCELLED");
        savedReservation.setRemark("取消原因：个人原因");

        // 保存更新
        reservationRepository.save(savedReservation);

        // 重新查询
        Optional<ReservationEntity> updatedReservation = reservationRepository.findById(savedReservation.getId());

        // 验证更新结果
        assertThat(updatedReservation).isPresent();
        assertThat(updatedReservation.get().getStatus()).isEqualTo("CANCELLED");
        assertThat(updatedReservation.get().getRemark()).isEqualTo("取消原因：个人原因");
    }

    @Test
    public void testDeleteReservation() {
        // 创建测试实体
        ReservationEntity reservation = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("7001")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        // 保存到数据库
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        // 删除实体
        reservationRepository.deleteById(savedReservation.getId());

        // 验证已删除
        Optional<ReservationEntity> deletedReservation = reservationRepository.findById(savedReservation.getId());
        assertThat(deletedReservation).isEmpty();
    }
}
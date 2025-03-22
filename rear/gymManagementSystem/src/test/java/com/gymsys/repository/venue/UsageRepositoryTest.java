package com.gymsys.repository.venue;

import com.gymsys.entity.venue.ReservationEntity;
import com.gymsys.entity.venue.UsageEntity;
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
public class UsageRepositoryTest {

    @Autowired
    private UsageRepository usageRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private VenueEntity testVenue;
    private ReservationEntity testReservation;

    @BeforeEach
    public void setup() {
        // 清空数据
        usageRepository.deleteAll();
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

        // 创建测试预约
        testReservation = ReservationEntity.builder()
                .venue(testVenue)
                .cardNumber("1001")
                .startTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .endTime(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS))
                .reservationType("REGULAR")
                .status("BOOKED")
                .build();

        testReservation = reservationRepository.save(testReservation);
    }

    @Test
    public void testSaveUsage() {
        // 创建测试实体
        UsageEntity usage = UsageEntity.builder()
                .venue(testVenue)
                .reservation(testReservation)
                .cardNumber("1001")
                .startTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .endTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .cost(new BigDecimal("50.0"))
                .paid(false)
                .remark("测试使用记录")
                .build();

        // 保存到数据库
        UsageEntity savedUsage = usageRepository.save(usage);

        // 验证保存成功并分配了ID
        assertThat(savedUsage.getId()).isNotNull();
        assertThat(savedUsage.getVenue().getId()).isEqualTo(testVenue.getId());
        assertThat(savedUsage.getReservation().getId()).isEqualTo(testReservation.getId());
        assertThat(savedUsage.getCardNumber()).isEqualTo("1001");
    }

    @Test
    public void testFindById() {
        // 创建测试实体
        UsageEntity usage = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("1002")
                .startTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .endTime(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .cost(new BigDecimal("50.0"))
                .paid(true)
                .build();

        // 保存到数据库
        UsageEntity savedUsage = usageRepository.save(usage);

        // 根据ID查询
        Optional<UsageEntity> foundUsage = usageRepository.findById(savedUsage.getId());

        // 验证查询结果
        assertThat(foundUsage).isPresent();
        assertThat(foundUsage.get().getCardNumber()).isEqualTo("1002");
        assertThat(foundUsage.get().getPaid()).isTrue();
    }

    @Test
    public void testFindByVenueAndStartTimeBetween() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 创建测试实体
        UsageEntity usage1 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("2001")
                .startTime(now.minusHours(2))
                .endTime(now.minusHours(1))
                .cost(new BigDecimal("50.0"))
                .paid(true)
                .build();

        UsageEntity usage2 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("2002")
                .startTime(now.minusHours(1))
                .endTime(now)
                .cost(new BigDecimal("50.0"))
                .paid(false)
                .build();

        UsageEntity usage3 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("2003")
                .startTime(now.plusHours(1))
                .endTime(now.plusHours(2))
                .cost(new BigDecimal("50.0"))
                .paid(false)
                .build();

        // 保存到数据库
        usageRepository.save(usage1);
        usageRepository.save(usage2);
        usageRepository.save(usage3);

        // 查询特定时间范围的使用记录
        List<UsageEntity> foundUsages = usageRepository
                .findByVenueAndStartTimeBetween(testVenue, now.minusHours(3), now);

        // 验证结果
        assertThat(foundUsages).hasSize(2);
        assertThat(foundUsages)
                .extracting(UsageEntity::getCardNumber)
                .containsExactlyInAnyOrder("2001", "2002");
    }

    @Test
    public void testFindByCardNumberAndPaid() {
        // 定义基准时间
        LocalDateTime baseTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime fiveHoursAgo = baseTime.minusHours(5);
        LocalDateTime threeHoursAgo = baseTime.minusHours(3);
        LocalDateTime twoHoursAgo = baseTime.minusHours(2);

        // 创建测试实体 - 同一会员卡号不同支付状态的使用记录
        UsageEntity usage1 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("3001")
                .startTime(fiveHoursAgo)
                .endTime(fiveHoursAgo.plusHours(1))
                .cost(new BigDecimal("50.0"))
                .paid(true)
                .build();

        UsageEntity usage2 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("3001")
                .startTime(threeHoursAgo)
                .endTime(threeHoursAgo.plusHours(1))
                .cost(new BigDecimal("50.0"))
                .paid(false)
                .build();

        UsageEntity usage3 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("3002")
                .startTime(twoHoursAgo)
                .endTime(twoHoursAgo.plusHours(1))
                .cost(new BigDecimal("50.0"))
                .paid(false)
                .build();

        // 保存到数据库
        usageRepository.save(usage1);
        usageRepository.save(usage2);
        usageRepository.save(usage3);

        // 查询特定会员未支付的使用记录
        List<UsageEntity> unpaidUsages = usageRepository
                .findByCardNumberAndPaid("3001", false);

        // 验证结果
        assertThat(unpaidUsages).hasSize(1);
        assertThat(unpaidUsages.get(0).getStartTime()).isEqualTo(threeHoursAgo);
    }

    @Test
    public void testFindByEndTimeIsNull() {
        // 创建测试实体 - 包括未结束的使用记录
        UsageEntity usage1 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("4001")
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(null)
                .cost(null)
                .paid(false)
                .build();

        UsageEntity usage2 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("4002")
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(null)
                .cost(null)
                .paid(false)
                .build();

        UsageEntity usage3 = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("4003")
                .startTime(LocalDateTime.now().minusHours(3))
                .endTime(LocalDateTime.now().minusHours(2))
                .cost(new BigDecimal("50.0"))
                .paid(true)
                .build();

        // 保存到数据库
        usageRepository.save(usage1);
        usageRepository.save(usage2);
        usageRepository.save(usage3);

        // 查询未结束的使用记录
        List<UsageEntity> inProgressUsages = usageRepository.findByEndTimeIsNull();

        // 验证结果
        assertThat(inProgressUsages).hasSize(2);
        assertThat(inProgressUsages)
                .extracting(UsageEntity::getCardNumber)
                .containsExactlyInAnyOrder("4001", "4002");
    }

    @Test
    public void testUpdateUsage() {
        // 创建测试实体 - 开始使用但未结束
        UsageEntity usage = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("5001")
                .startTime(LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .endTime(null)
                .cost(null)
                .paid(false)
                .build();

        // 保存到数据库
        UsageEntity savedUsage = usageRepository.save(usage);

        // 更新属性 - 结束使用并计费
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        savedUsage.setEndTime(endTime);
        savedUsage.setCost(new BigDecimal("50.0"));
        savedUsage.setPaid(true);
        savedUsage.setRemark("已完成支付");

        // 保存更新
        usageRepository.save(savedUsage);

        // 重新查询
        Optional<UsageEntity> updatedUsage = usageRepository.findById(savedUsage.getId());

        // 验证更新结果
        assertThat(updatedUsage).isPresent();
        assertThat(updatedUsage.get().getEndTime()).isEqualTo(endTime);
        assertThat(updatedUsage.get().getCost()).isEqualByComparingTo(new BigDecimal("50.0"));
        assertThat(updatedUsage.get().getPaid()).isTrue();
        assertThat(updatedUsage.get().getRemark()).isEqualTo("已完成支付");
    }

    @Test
    public void testDeleteUsage() {
        // 创建测试实体
        UsageEntity usage = UsageEntity.builder()
                .venue(testVenue)
                .cardNumber("6001")
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().minusHours(1))
                .cost(new BigDecimal("50.0"))
                .paid(true)
                .build();

        // 保存到数据库
        UsageEntity savedUsage = usageRepository.save(usage);

        // 删除实体
        usageRepository.deleteById(savedUsage.getId());

        // 验证已删除
        Optional<UsageEntity> deletedUsage = usageRepository.findById(savedUsage.getId());
        assertThat(deletedUsage).isEmpty();
    }
}
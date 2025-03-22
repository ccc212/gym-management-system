package com.gymsys.repository;

import com.gymsys.entity.VenueEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    public void testSaveVenue() {
        // 创建测试实体
        VenueEntity venue = VenueEntity.builder()
                .name("测试场地")
                .type("羽毛球场")
                .pricePerHour(new BigDecimal("50.0"))
                .isAvailable(true)
                .location("一楼东侧")
                .capacity(4)
                .description("标准羽毛球场地")
                .build();

        // 保存到数据库
        VenueEntity savedVenue = venueRepository.save(venue);

        // 验证保存成功并分配了ID
        assertThat(savedVenue.getId()).isNotNull();
        assertThat(savedVenue.getName()).isEqualTo("测试场地");
        assertThat(savedVenue.getType()).isEqualTo("羽毛球场");
    }

    @Test
    public void testFindById() {
        // 创建测试实体
        VenueEntity venue = VenueEntity.builder()
                .name("测试场地2")
                .type("篮球场")
                .pricePerHour(new BigDecimal("100.0"))
                .isAvailable(true)
                .location("二楼")
                .capacity(10)
                .description("室内篮球场")
                .build();

        // 保存到数据库
        VenueEntity savedVenue = venueRepository.save(venue);

        // 根据ID查询
        Optional<VenueEntity> foundVenue = venueRepository.findById(savedVenue.getId());

        // 验证查询结果
        assertThat(foundVenue).isPresent();
        assertThat(foundVenue.get().getName()).isEqualTo("测试场地2");
    }

    @Test
    public void testFindByIsAvailable() {
        // 清空数据库
        venueRepository.deleteAll();

        // 创建可用场地
        VenueEntity availableVenue = VenueEntity.builder()
                .name("可用场地")
                .type("网球场")
                .pricePerHour(new BigDecimal("80.0"))
                .isAvailable(true)
                .location("户外")
                .build();

        // 创建不可用场地
        VenueEntity unavailableVenue = VenueEntity.builder()
                .name("不可用场地")
                .type("网球场")
                .pricePerHour(new BigDecimal("80.0"))
                .isAvailable(false)
                .location("户外")
                .build();

        // 保存到数据库
        venueRepository.save(availableVenue);
        venueRepository.save(unavailableVenue);

        // 查询可用场地
        List<VenueEntity> availableVenues = venueRepository.findByIsAvailable(true);

        // 验证结果
        assertThat(availableVenues).hasSize(1);
        assertThat(availableVenues.get(0).getName()).isEqualTo("可用场地");
    }

    @Test
    public void testFindByType() {
        // 创建不同类型的场地
        VenueEntity venue1 = VenueEntity.builder()
                .name("羽毛球场1")
                .type("羽毛球场")
                .pricePerHour(new BigDecimal("50.0"))
                .isAvailable(true)
                .build();

        VenueEntity venue2 = VenueEntity.builder()
                .name("羽毛球场2")
                .type("羽毛球场")
                .pricePerHour(new BigDecimal("60.0"))
                .isAvailable(true)
                .build();

        VenueEntity venue3 = VenueEntity.builder()
                .name("乒乓球室")
                .type("乒乓球室")
                .pricePerHour(new BigDecimal("30.0"))
                .isAvailable(true)
                .build();

        // 保存到数据库
        venueRepository.save(venue1);
        venueRepository.save(venue2);
        venueRepository.save(venue3);

        // 查询羽毛球场类型
        List<VenueEntity> badmintonVenues = venueRepository.findByType("羽毛球场");

        // 验证结果
        assertThat(badmintonVenues).hasSize(2);
        assertThat(badmintonVenues)
                .extracting(VenueEntity::getName)
                .containsExactlyInAnyOrder("羽毛球场1", "羽毛球场2");
    }

    @Test
    public void testUpdateVenue() {
        // 创建测试实体
        VenueEntity venue = VenueEntity.builder()
                .name("旧名称")
                .type("健身房")
                .pricePerHour(new BigDecimal("120.0"))
                .isAvailable(true)
                .build();

        // 保存到数据库
        VenueEntity savedVenue = venueRepository.save(venue);

        // 更新属性
        savedVenue.setName("新名称");
        savedVenue.setPricePerHour(new BigDecimal("150.0"));

        // 保存更新
        venueRepository.save(savedVenue);

        // 重新查询
        Optional<VenueEntity> updatedVenue = venueRepository.findById(savedVenue.getId());

        // 验证更新结果
        assertThat(updatedVenue).isPresent();
        assertThat(updatedVenue.get().getName()).isEqualTo("新名称");
        assertThat(updatedVenue.get().getPricePerHour()).isEqualTo(new BigDecimal("150.0"));
    }

    @Test
    public void testDeleteVenue() {
        // 创建测试实体
        VenueEntity venue = VenueEntity.builder()
                .name("要删除的场地")
                .type("瑜伽室")
                .pricePerHour(new BigDecimal("70.0"))
                .isAvailable(true)
                .build();

        // 保存到数据库
        VenueEntity savedVenue = venueRepository.save(venue);

        // 删除实体
        venueRepository.deleteById(savedVenue.getId());

        // 验证已删除
        Optional<VenueEntity> deletedVenue = venueRepository.findById(savedVenue.getId());
        assertThat(deletedVenue).isEmpty();
    }
}
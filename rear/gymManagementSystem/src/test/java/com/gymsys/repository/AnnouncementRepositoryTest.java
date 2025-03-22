package com.gymsys.repository;

import com.gymsys.entity.AnnouncementEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnnouncementRepositoryTest {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Test
    public void testSaveAnnouncement() {
        // 创建测试实体
        AnnouncementEntity announcement = AnnouncementEntity.builder()
                .title("测试公告")
                .content("这是一个测试公告内容")
                .publishTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(7))
                .active(true)
                .build();

        // 保存到数据库
        AnnouncementEntity savedAnnouncement = announcementRepository.save(announcement);

        // 验证保存成功并分配了ID
        assertThat(savedAnnouncement.getId()).isNotNull();
        assertThat(savedAnnouncement.getTitle()).isEqualTo("测试公告");
        assertThat(savedAnnouncement.getContent()).isEqualTo("这是一个测试公告内容");
    }

    @Test
    public void testFindById() {
        // 创建测试实体
        AnnouncementEntity announcement = AnnouncementEntity.builder()
                .title("测试公告2")
                .content("这是第二个测试公告")
                .publishTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(10))
                .active(true)
                .build();

        // 保存到数据库
        AnnouncementEntity savedAnnouncement = announcementRepository.save(announcement);

        // 根据ID查询
        Optional<AnnouncementEntity> foundAnnouncement = announcementRepository.findById(savedAnnouncement.getId());

        // 验证查询结果
        assertThat(foundAnnouncement).isPresent();
        assertThat(foundAnnouncement.get().getTitle()).isEqualTo("测试公告2");
    }

    @Test
    public void testFindByActiveTrue() {
        // 清空数据库
        announcementRepository.deleteAll();

        // 创建活动公告
        AnnouncementEntity activeAnnouncement = AnnouncementEntity.builder()
                .title("活动公告")
                .content("这是一个活动公告")
                .publishTime(LocalDateTime.now().minusDays(1))
                .expireTime(LocalDateTime.now().plusDays(5))
                .active(true)
                .build();

        // 创建非活动公告
        AnnouncementEntity inactiveAnnouncement = AnnouncementEntity.builder()
                .title("非活动公告")
                .content("这是一个非活动公告")
                .publishTime(LocalDateTime.now().minusDays(2))
                .expireTime(LocalDateTime.now().plusDays(3))
                .active(false)
                .build();

        // 保存到数据库
        announcementRepository.save(activeAnnouncement);
        announcementRepository.save(inactiveAnnouncement);

        // 查询活动公告
        List<AnnouncementEntity> activeAnnouncements = announcementRepository.findByActiveTrue();

        // 验证结果
        assertThat(activeAnnouncements).hasSize(1);
        assertThat(activeAnnouncements.get(0).getTitle()).isEqualTo("活动公告");
    }

    @Test
    public void testFindByActiveTrueAndPublishTimeBeforeAndExpireTimeAfter() {
        // 清空数据库
        announcementRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        // 创建有效公告
        AnnouncementEntity validAnnouncement = AnnouncementEntity.builder()
                .title("有效公告")
                .content("这是一个当前有效的公告")
                .publishTime(now.minusDays(1))
                .expireTime(now.plusDays(5))
                .active(true)
                .build();

        // 创建未发布公告
        AnnouncementEntity futureAnnouncement = AnnouncementEntity.builder()
                .title("未发布公告")
                .content("这是一个还未发布的公告")
                .publishTime(now.plusDays(1))
                .expireTime(now.plusDays(10))
                .active(true)
                .build();

        // 创建已过期公告
        AnnouncementEntity expiredAnnouncement = AnnouncementEntity.builder()
                .title("已过期公告")
                .content("这是一个已过期的公告")
                .publishTime(now.minusDays(10))
                .expireTime(now.minusDays(1))
                .active(true)
                .build();

        // 创建非活动但在有效期内的公告
        AnnouncementEntity inactiveAnnouncement = AnnouncementEntity.builder()
                .title("非活动公告")
                .content("这是一个非活动的公告")
                .publishTime(now.minusDays(2))
                .expireTime(now.plusDays(2))
                .active(false)
                .build();

        // 保存到数据库
        announcementRepository.save(validAnnouncement);
        announcementRepository.save(futureAnnouncement);
        announcementRepository.save(expiredAnnouncement);
        announcementRepository.save(inactiveAnnouncement);

        // 查询当前有效的公告
        List<AnnouncementEntity> currentAnnouncements =
                announcementRepository.findByActiveTrueAndPublishTimeBeforeAndExpireTimeAfter(now, now);

        // 验证结果
        assertThat(currentAnnouncements).hasSize(1);
        assertThat(currentAnnouncements.get(0).getTitle()).isEqualTo("有效公告");
    }

    @Test
    public void testUpdateAnnouncement() {
        // 创建测试实体
        AnnouncementEntity announcement = AnnouncementEntity.builder()
                .title("原始标题")
                .content("原始内容")
                .publishTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(7))
                .active(true)
                .build();

        // 保存到数据库
        AnnouncementEntity savedAnnouncement = announcementRepository.save(announcement);

        // 更新属性
        savedAnnouncement.setTitle("更新标题");
        savedAnnouncement.setContent("更新内容");
        savedAnnouncement.setActive(false);

        // 保存更新
        announcementRepository.save(savedAnnouncement);

        // 重新查询
        Optional<AnnouncementEntity> updatedAnnouncement = announcementRepository.findById(savedAnnouncement.getId());

        // 验证更新结果
        assertThat(updatedAnnouncement).isPresent();
        assertThat(updatedAnnouncement.get().getTitle()).isEqualTo("更新标题");
        assertThat(updatedAnnouncement.get().getContent()).isEqualTo("更新内容");
        assertThat(updatedAnnouncement.get().getActive()).isFalse();
    }

    @Test
    public void testDeleteAnnouncement() {
        // 创建测试实体
        AnnouncementEntity announcement = AnnouncementEntity.builder()
                .title("要删除的公告")
                .content("这个公告将被删除")
                .publishTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(5))
                .active(true)
                .build();

        // 保存到数据库
        AnnouncementEntity savedAnnouncement = announcementRepository.save(announcement);

        // 删除实体
        announcementRepository.deleteById(savedAnnouncement.getId());

        // 验证已删除
        Optional<AnnouncementEntity> deletedAnnouncement = announcementRepository.findById(savedAnnouncement.getId());
        assertThat(deletedAnnouncement).isEmpty();
    }
}
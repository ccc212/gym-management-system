package com.gymsys.repository.competition;

import com.gymsys.entity.competition.CompetitionVenueRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompetitionVenueRelationRepository extends JpaRepository<CompetitionVenueRelation, Long> {
    
    /**
     * 根据赛事ID查询场地关联
     * 
     * @param competitionId 赛事ID
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> findByCompetitionId(Long competitionId);
    
    /**
     * 根据场地ID查询场地关联
     *
     * @param venueId 场地ID
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> findByVenueId(Long venueId);
    
    /**
     * 根据状态查询场地关联
     *
     * @param status 状态
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> findByStatus(Integer status);
    
    /**
     * 查询时间冲突的场地预约
     * 
     * @param venueId 场地ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态
     * @return 冲突的场地关联列表
     */
    @Query("SELECT cvr FROM CompetitionVenueRelation cvr WHERE cvr.venueId = :venueId AND " +
           "((cvr.startTime BETWEEN :startTime AND :endTime) OR (cvr.endTime BETWEEN :startTime AND :endTime) OR " +
           "(cvr.startTime <= :startTime AND cvr.endTime >= :endTime)) AND cvr.status = :status")
    List<CompetitionVenueRelation> findConflictReservations(Long venueId, LocalDateTime startTime, LocalDateTime endTime, Integer status);
    
    /**
     * 查询赛事的场地关联，并按场地ID和开始时间排序
     * 
     * @param competitionId 赛事ID
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> findByCompetitionIdOrderByVenueIdAscStartTimeAsc(Long competitionId);
    
    /**
     * 根据赛事ID和状态查询场地关联
     * 
     * @param competitionId 赛事ID
     * @param status 状态
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> findByCompetitionIdAndStatus(Long competitionId, Integer status);
    
    /**
     * 根据场地ID和状态查询场地关联
     * 
     * @param venueId 场地ID
     * @param status 状态
     * @return 场地关联列表
     */
    List<CompetitionVenueRelation> findByVenueIdAndStatus(Long venueId, Integer status);
} 
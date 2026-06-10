package com.archaeology.relic.repository;

import com.archaeology.relic.entity.RelicRepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelicRepairOrderRepository extends JpaRepository<RelicRepairOrder, Long> {

    List<RelicRepairOrder> findByRelic_IdOrderByCreateTimeDesc(Long relicId);

    List<RelicRepairOrder> findByStatusOrderByCreateTimeDesc(String status);

    List<RelicRepairOrder> findByOrderNoContainingIgnoreCase(String orderNo);

    @Query("SELECT r FROM RelicRepairOrder r JOIN r.relic relic WHERE relic.relicNo LIKE %:relicNo% ORDER BY r.createTime DESC")
    List<RelicRepairOrder> findByRelicNoContainingIgnoreCase(@Param("relicNo") String relicNo);

    @Query("SELECT r FROM RelicRepairOrder r JOIN r.relic relic WHERE relic.name LIKE %:relicName% ORDER BY r.createTime DESC")
    List<RelicRepairOrder> findByRelicNameContainingIgnoreCase(@Param("relicName") String relicName);

    @Query("SELECT r.status, COUNT(r) FROM RelicRepairOrder r GROUP BY r.status")
    List<Object[]> countByStatus();

    boolean existsByOrderNo(String orderNo);
}

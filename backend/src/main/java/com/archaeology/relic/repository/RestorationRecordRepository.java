package com.archaeology.relic.repository;

import com.archaeology.relic.entity.RestorationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestorationRecordRepository extends JpaRepository<RestorationRecord, Long> {

    List<RestorationRecord> findByRelicIdOrderByRecordDateDesc(Long relicId);
}

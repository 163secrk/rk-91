package com.archaeology.relic.repository;

import com.archaeology.relic.entity.Relic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelicRepository extends JpaRepository<Relic, Long> {

    List<Relic> findByNameContainingIgnoreCase(String name);

    List<Relic> findByRelicNoContainingIgnoreCase(String relicNo);

    List<Relic> findByCategoryContainingIgnoreCase(String category);
}

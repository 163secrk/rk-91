package com.archaeology.relic.repository;

import com.archaeology.relic.entity.Relic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelicRepository extends JpaRepository<Relic, Long> {

    List<Relic> findByNameContainingIgnoreCase(String name);

    List<Relic> findByRelicNoContainingIgnoreCase(String relicNo);

    List<Relic> findByCategoryContainingIgnoreCase(String category);

    @Query("SELECT r.category, COUNT(r) FROM Relic r GROUP BY r.category")
    List<Object[]> countByCategory();

    @Query("SELECT r.era, COUNT(r) FROM Relic r GROUP BY r.era")
    List<Object[]> countByEra();

    @Query("SELECT r.material, COUNT(r) FROM Relic r GROUP BY r.material")
    List<Object[]> countByMaterial();

    @Query("SELECT r.preservationStatus, COUNT(r) FROM Relic r GROUP BY r.preservationStatus")
    List<Object[]> countByPreservationStatus();
}

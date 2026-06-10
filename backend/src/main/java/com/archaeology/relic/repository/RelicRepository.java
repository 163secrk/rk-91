package com.archaeology.relic.repository;

import com.archaeology.relic.entity.Relic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT FUNCTION('YEAR', r.excavateDate) FROM Relic r GROUP BY FUNCTION('YEAR', r.excavateDate) ORDER BY FUNCTION('YEAR', r.excavateDate) DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT FUNCTION('MONTH', r.excavateDate), COUNT(r) FROM Relic r WHERE FUNCTION('YEAR', r.excavateDate) = :year GROUP BY FUNCTION('MONTH', r.excavateDate) ORDER BY FUNCTION('MONTH', r.excavateDate)")
    List<Object[]> countByMonth(@Param("year") Integer year);

    @Query("SELECT r.category, COUNT(r) FROM Relic r WHERE FUNCTION('YEAR', r.excavateDate) = :year GROUP BY r.category")
    List<Object[]> countByCategoryByYear(@Param("year") Integer year);

    @Query("SELECT r.era, COUNT(r) FROM Relic r WHERE FUNCTION('YEAR', r.excavateDate) = :year GROUP BY r.era")
    List<Object[]> countByEraByYear(@Param("year") Integer year);

    @Query("SELECT r.material, COUNT(r) FROM Relic r WHERE FUNCTION('YEAR', r.excavateDate) = :year GROUP BY r.material")
    List<Object[]> countByMaterialByYear(@Param("year") Integer year);

    @Query("SELECT r.preservationStatus, COUNT(r) FROM Relic r WHERE FUNCTION('YEAR', r.excavateDate) = :year GROUP BY r.preservationStatus")
    List<Object[]> countByPreservationStatusByYear(@Param("year") Integer year);

    @Query("SELECT COUNT(r) FROM Relic r WHERE FUNCTION('YEAR', r.excavateDate) = :year")
    long countByYear(@Param("year") Integer year);

    List<Relic> findByExcavationUnit_Id(Long excavationUnitId);
}

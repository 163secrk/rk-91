package com.archaeology.relic.repository;

import com.archaeology.relic.entity.ExcavationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcavationUnitRepository extends JpaRepository<ExcavationUnit, Long> {

    Optional<ExcavationUnit> findByUnitNo(String unitNo);

    List<ExcavationUnit> findByUnitNoContainingIgnoreCase(String unitNo);

    List<ExcavationUnit> findByDirectorContainingIgnoreCase(String director);

    List<ExcavationUnit> findByStatus(String status);

    @Query("SELECT e.status, COUNT(e) FROM ExcavationUnit e GROUP BY e.status")
    List<Object[]> countByStatus();

    @Query("SELECT e.director, COUNT(e) FROM ExcavationUnit e GROUP BY e.director")
    List<Object[]> countByDirector();
}

package com.archaeology.relic.service;

import com.archaeology.relic.entity.ExcavationUnit;
import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.repository.ExcavationUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExcavationUnitService {

    @Autowired
    private ExcavationUnitRepository excavationUnitRepository;

    public List<ExcavationUnit> findAll() {
        return excavationUnitRepository.findAll();
    }

    public Optional<ExcavationUnit> findById(Long id) {
        return excavationUnitRepository.findById(id);
    }

    public Optional<ExcavationUnit> findByUnitNo(String unitNo) {
        return excavationUnitRepository.findByUnitNo(unitNo);
    }

    public List<ExcavationUnit> searchByUnitNo(String unitNo) {
        return excavationUnitRepository.findByUnitNoContainingIgnoreCase(unitNo);
    }

    public List<ExcavationUnit> searchByDirector(String director) {
        return excavationUnitRepository.findByDirectorContainingIgnoreCase(director);
    }

    public List<ExcavationUnit> searchByStatus(String status) {
        return excavationUnitRepository.findByStatus(status);
    }

    @Transactional
    public ExcavationUnit save(ExcavationUnit unit) {
        return excavationUnitRepository.save(unit);
    }

    @Transactional
    public ExcavationUnit update(Long id, ExcavationUnit unitDetails) {
        return excavationUnitRepository.findById(id).map(unit -> {
            unit.setUnitNo(unitDetails.getUnitNo());
            unit.setLocation(unitDetails.getLocation());
            unit.setLength(unitDetails.getLength());
            unit.setWidth(unitDetails.getWidth());
            unit.setDepth(unitDetails.getDepth());
            unit.setStatus(unitDetails.getStatus());
            unit.setDirector(unitDetails.getDirector());
            unit.setRemark(unitDetails.getRemark());
            return excavationUnitRepository.save(unit);
        }).orElseThrow(() -> new RuntimeException("ExcavationUnit not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        excavationUnitRepository.findById(id).ifPresent(unit -> {
            for (Relic relic : unit.getRelics()) {
                relic.setExcavationUnit(null);
            }
            excavationUnitRepository.deleteById(id);
        });
    }

    public List<Relic> getRelics(Long unitId) {
        return excavationUnitRepository.findById(unitId)
                .map(ExcavationUnit::getRelics)
                .orElseThrow(() -> new RuntimeException("ExcavationUnit not found with id: " + unitId));
    }

    public List<Object[]> countByStatus() {
        return excavationUnitRepository.countByStatus();
    }

    public List<Object[]> countByDirector() {
        return excavationUnitRepository.countByDirector();
    }
}

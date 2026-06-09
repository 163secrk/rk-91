package com.archaeology.relic.service;

import com.archaeology.relic.entity.Coordinate3D;
import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.entity.RestorationRecord;
import com.archaeology.relic.repository.RelicRepository;
import com.archaeology.relic.repository.RestorationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RelicService {

    @Autowired
    private RelicRepository relicRepository;

    @Autowired
    private RestorationRecordRepository restorationRecordRepository;

    public List<Relic> findAll() {
        return relicRepository.findAll();
    }

    public Optional<Relic> findById(Long id) {
        return relicRepository.findById(id);
    }

    public List<Relic> searchByName(String name) {
        return relicRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Relic> searchByRelicNo(String relicNo) {
        return relicRepository.findByRelicNoContainingIgnoreCase(relicNo);
    }

    public List<Relic> searchByCategory(String category) {
        return relicRepository.findByCategoryContainingIgnoreCase(category);
    }

    @Transactional
    public Relic save(Relic relic) {
        if (relic.getCoordinate() == null) {
            Coordinate3D coord = new Coordinate3D();
            coord.setX(0.0);
            coord.setY(0.0);
            coord.setZ(0.0);
            relic.setCoordinate(coord);
        }
        return relicRepository.save(relic);
    }

    @Transactional
    public Relic update(Long id, Relic relicDetails) {
        return relicRepository.findById(id).map(relic -> {
            relic.setRelicNo(relicDetails.getRelicNo());
            relic.setName(relicDetails.getName());
            relic.setCategory(relicDetails.getCategory());
            relic.setDescription(relicDetails.getDescription());
            relic.setMaterial(relicDetails.getMaterial());
            relic.setEra(relicDetails.getEra());
            relic.setExcavateDate(relicDetails.getExcavateDate());
            relic.setExcavator(relicDetails.getExcavator());
            relic.setExcavationSite(relicDetails.getExcavationSite());
            relic.setStratum(relicDetails.getStratum());
            relic.setPreservationStatus(relicDetails.getPreservationStatus());
            relic.setRemark(relicDetails.getRemark());

            if (relicDetails.getCoordinate() != null) {
                if (relic.getCoordinate() != null) {
                    relic.getCoordinate().setX(relicDetails.getCoordinate().getX());
                    relic.getCoordinate().setY(relicDetails.getCoordinate().getY());
                    relic.getCoordinate().setZ(relicDetails.getCoordinate().getZ());
                    relic.getCoordinate().setCoordinateSystem(relicDetails.getCoordinate().getCoordinateSystem());
                    relic.getCoordinate().setRemark(relicDetails.getCoordinate().getRemark());
                } else {
                    relic.setCoordinate(relicDetails.getCoordinate());
                }
            }

            return relicRepository.save(relic);
        }).orElseThrow(() -> new RuntimeException("Relic not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        relicRepository.deleteById(id);
    }

    @Transactional
    public RestorationRecord addRestorationRecord(Long relicId, RestorationRecord record) {
        return relicRepository.findById(relicId).map(relic -> {
            record.setRelic(relic);
            return restorationRecordRepository.save(record);
        }).orElseThrow(() -> new RuntimeException("Relic not found with id: " + relicId));
    }

    public List<RestorationRecord> getRestorationRecords(Long relicId) {
        return restorationRecordRepository.findByRelicIdOrderByRecordDateDesc(relicId);
    }

    @Transactional
    public void deleteRestorationRecord(Long recordId) {
        restorationRecordRepository.deleteById(recordId);
    }

    public List<Object[]> countByCategory() {
        return relicRepository.countByCategory();
    }

    public List<Object[]> countByEra() {
        return relicRepository.countByEra();
    }

    public List<Object[]> countByMaterial() {
        return relicRepository.countByMaterial();
    }

    public List<Object[]> countByPreservationStatus() {
        return relicRepository.countByPreservationStatus();
    }
}

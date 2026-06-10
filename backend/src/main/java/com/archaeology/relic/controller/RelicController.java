package com.archaeology.relic.controller;

import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.entity.RestorationRecord;
import com.archaeology.relic.service.RelicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relics")
@CrossOrigin(origins = "*")
public class RelicController {

    @Autowired
    private RelicService relicService;

    @GetMapping
    public List<Relic> getAllRelics() {
        return relicService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Relic> getRelicById(@PathVariable Long id) {
        return relicService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Relic> searchRelics(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String relicNo,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long excavationUnitId) {
        if (excavationUnitId != null) {
            return relicService.searchByExcavationUnitId(excavationUnitId);
        } else if (name != null && !name.isEmpty()) {
            return relicService.searchByName(name);
        } else if (relicNo != null && !relicNo.isEmpty()) {
            return relicService.searchByRelicNo(relicNo);
        } else if (category != null && !category.isEmpty()) {
            return relicService.searchByCategory(category);
        }
        return relicService.findAll();
    }

    @PostMapping
    public Relic createRelic(@RequestBody Relic relic) {
        return relicService.save(relic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Relic> updateRelic(@PathVariable Long id, @RequestBody Relic relicDetails) {
        try {
            Relic updatedRelic = relicService.update(id, relicDetails);
            return ResponseEntity.ok(updatedRelic);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelic(@PathVariable Long id) {
        relicService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/restoration-records")
    public List<RestorationRecord> getRestorationRecords(@PathVariable Long id) {
        return relicService.getRestorationRecords(id);
    }

    @PostMapping("/{id}/restoration-records")
    public ResponseEntity<RestorationRecord> addRestorationRecord(
            @PathVariable Long id,
            @RequestBody RestorationRecord record) {
        try {
            RestorationRecord savedRecord = relicService.addRestorationRecord(id, record);
            return ResponseEntity.ok(savedRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/restoration-records/{recordId}")
    public ResponseEntity<Void> deleteRestorationRecord(@PathVariable Long recordId) {
        relicService.deleteRestorationRecord(recordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam(required = false) Integer year) {

        Map<String, Object> result = new HashMap<>();

        List<Object[]> categoryData = (year != null)
                ? relicService.countByCategoryByYear(year) : relicService.countByCategory();
        List<Object[]> eraData = (year != null)
                ? relicService.countByEraByYear(year) : relicService.countByEra();
        List<Object[]> materialData = (year != null)
                ? relicService.countByMaterialByYear(year) : relicService.countByMaterial();
        List<Object[]> statusData = (year != null)
                ? relicService.countByPreservationStatusByYear(year) : relicService.countByPreservationStatus();

        List<Map<String, Object>> categoryStats = categoryData.stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", arr[0] != null ? arr[0].toString() : "未分类");
                    map.put("value", ((Number) arr[1]).longValue());
                    return map;
                })
                .toList();

        List<Map<String, Object>> eraStats = eraData.stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", arr[0] != null ? arr[0].toString() : "未知年代");
                    map.put("value", ((Number) arr[1]).longValue());
                    return map;
                })
                .toList();

        List<Map<String, Object>> materialStats = materialData.stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", arr[0] != null ? arr[0].toString() : "未知材质");
                    map.put("value", ((Number) arr[1]).longValue());
                    return map;
                })
                .toList();

        List<Map<String, Object>> statusStats = statusData.stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", arr[0] != null ? arr[0].toString() : "未知状态");
                    map.put("value", ((Number) arr[1]).longValue());
                    return map;
                })
                .toList();

        long total = (year != null)
                ? relicService.countByYear(year)
                : categoryStats.stream().mapToLong(m -> (Long) m.get("value")).sum();

        List<Map<String, Object>> monthStats = new java.util.ArrayList<>();
        if (year != null) {
            List<Object[]> monthData = relicService.countByMonth(year);
            for (int m = 1; m <= 12; m++) {
                Map<String, Object> map = new HashMap<>();
                map.put("month", m);
                map.put("name", m + "月");
                map.put("value", 0L);
                monthStats.add(map);
            }
            for (Object[] arr : monthData) {
                int month = ((Number) arr[0]).intValue();
                long count = ((Number) arr[1]).longValue();
                monthStats.get(month - 1).put("value", count);
            }
        }

        result.put("total", total);
        result.put("byCategory", categoryStats);
        result.put("byEra", eraStats);
        result.put("byMaterial", materialStats);
        result.put("byPreservationStatus", statusStats);
        if (year != null) {
            result.put("byMonth", monthStats);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics/years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        List<Integer> years = relicService.getAvailableYears();
        return ResponseEntity.ok(years);
    }
}

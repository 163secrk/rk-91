package com.archaeology.relic.controller;

import com.archaeology.relic.entity.ExcavationUnit;
import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.service.ExcavationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excavation-units")
@CrossOrigin(origins = "*")
public class ExcavationUnitController {

    @Autowired
    private ExcavationUnitService excavationUnitService;

    @GetMapping
    public List<ExcavationUnit> getAllUnits() {
        return excavationUnitService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcavationUnit> getUnitById(@PathVariable Long id) {
        return excavationUnitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<ExcavationUnit> searchUnits(
            @RequestParam(required = false) String unitNo,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String status) {
        if (unitNo != null && !unitNo.isEmpty()) {
            return excavationUnitService.searchByUnitNo(unitNo);
        } else if (director != null && !director.isEmpty()) {
            return excavationUnitService.searchByDirector(director);
        } else if (status != null && !status.isEmpty()) {
            return excavationUnitService.searchByStatus(status);
        }
        return excavationUnitService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createUnit(@RequestBody ExcavationUnit unit) {
        try {
            ExcavationUnit saved = excavationUnitService.save(unit);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "探方编号已存在");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcavationUnit> updateUnit(@PathVariable Long id, @RequestBody ExcavationUnit unitDetails) {
        try {
            ExcavationUnit updated = excavationUnitService.update(id, unitDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        excavationUnitService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/relics")
    public ResponseEntity<List<Relic>> getUnitRelics(@PathVariable Long id) {
        try {
            List<Relic> relics = excavationUnitService.getRelics(id);
            return ResponseEntity.ok(relics);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> statusStats = excavationUnitService.countByStatus().stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", arr[0] != null ? arr[0].toString() : "未知状态");
                    map.put("value", ((Number) arr[1]).longValue());
                    return map;
                })
                .toList();

        List<Map<String, Object>> directorStats = excavationUnitService.countByDirector().stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", arr[0] != null ? arr[0].toString() : "未分配");
                    map.put("value", ((Number) arr[1]).longValue());
                    return map;
                })
                .toList();

        long total = statusStats.stream().mapToLong(m -> (Long) m.get("value")).sum();

        result.put("total", total);
        result.put("byStatus", statusStats);
        result.put("byDirector", directorStats);

        return ResponseEntity.ok(result);
    }
}

package com.archaeology.relic.controller;

import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.entity.RestorationRecord;
import com.archaeology.relic.service.RelicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(required = false) String category) {
        if (name != null && !name.isEmpty()) {
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
}

package com.archaeology.relic.controller;

import com.archaeology.relic.entity.RelicRepairOrder;
import com.archaeology.relic.service.RelicRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repair-orders")
@CrossOrigin(origins = "*")
public class RelicRepairOrderController {

    @Autowired
    private RelicRepairOrderService repairOrderService;

    @GetMapping
    public List<RelicRepairOrder> getAllOrders() {
        return repairOrderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelicRepairOrder> getOrderById(@PathVariable Long id) {
        return repairOrderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<RelicRepairOrder> searchOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String relicNo,
            @RequestParam(required = false) String relicName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long relicId) {
        if (relicId != null) {
            return repairOrderService.findByRelicId(relicId);
        } else if (status != null && !status.isEmpty()) {
            return repairOrderService.findByStatus(status);
        } else if (orderNo != null && !orderNo.isEmpty()) {
            return repairOrderService.searchByOrderNo(orderNo);
        } else if (relicNo != null && !relicNo.isEmpty()) {
            return repairOrderService.searchByRelicNo(relicNo);
        } else if (relicName != null && !relicName.isEmpty()) {
            return repairOrderService.searchByRelicName(relicName);
        }
        return repairOrderService.findAll();
    }

    @PostMapping
    public RelicRepairOrder createOrder(@RequestBody RelicRepairOrder order) {
        return repairOrderService.save(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelicRepairOrder> updateOrder(@PathVariable Long id, @RequestBody RelicRepairOrder orderDetails) {
        try {
            RelicRepairOrder updatedOrder = repairOrderService.update(id, orderDetails);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        repairOrderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<RelicRepairOrder> startRepair(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String repairer = requestBody != null ? requestBody.get("repairer") : null;
            RelicRepairOrder updatedOrder = repairOrderService.startRepair(id, repairer);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RelicRepairOrder> completeRepair(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String repairResult = requestBody != null ? requestBody.get("repairResult") : null;
            RelicRepairOrder updatedOrder = repairOrderService.completeRepair(id, repairResult);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getStatusStatistics() {
        List<Map<String, Object>> statistics = repairOrderService.getStatusStatistics();
        return ResponseEntity.ok(statistics);
    }
}

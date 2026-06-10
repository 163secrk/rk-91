package com.archaeology.relic.service;

import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.entity.RelicRepairOrder;
import com.archaeology.relic.repository.RelicRepository;
import com.archaeology.relic.repository.RelicRepairOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelicRepairOrderService {

    @Autowired
    private RelicRepairOrderRepository repairOrderRepository;

    @Autowired
    private RelicRepository relicRepository;

    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<RelicRepairOrder> findAll() {
        return repairOrderRepository.findAll().stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .peek(this::setRelicInfo)
                .collect(Collectors.toList());
    }

    public Optional<RelicRepairOrder> findById(Long id) {
        return repairOrderRepository.findById(id).map(order -> {
            setRelicInfo(order);
            return order;
        });
    }

    private void setRelicInfo(RelicRepairOrder order) {
        if (order.getRelic() != null) {
            order.setRelicId(order.getRelic().getId());
            order.setRelicNo(order.getRelic().getRelicNo());
            order.setRelicName(order.getRelic().getName());
        }
    }

    public List<RelicRepairOrder> findByRelicId(Long relicId) {
        return repairOrderRepository.findByRelic_IdOrderByCreateTimeDesc(relicId).stream()
                .peek(this::setRelicInfo)
                .collect(Collectors.toList());
    }

    public List<RelicRepairOrder> findByStatus(String status) {
        return repairOrderRepository.findByStatusOrderByCreateTimeDesc(status).stream()
                .peek(this::setRelicInfo)
                .collect(Collectors.toList());
    }

    public List<RelicRepairOrder> searchByOrderNo(String orderNo) {
        return repairOrderRepository.findByOrderNoContainingIgnoreCase(orderNo).stream()
                .peek(this::setRelicInfo)
                .collect(Collectors.toList());
    }

    public List<RelicRepairOrder> searchByRelicNo(String relicNo) {
        return repairOrderRepository.findByRelicNoContainingIgnoreCase(relicNo).stream()
                .peek(this::setRelicInfo)
                .collect(Collectors.toList());
    }

    public List<RelicRepairOrder> searchByRelicName(String relicName) {
        return repairOrderRepository.findByRelicNameContainingIgnoreCase(relicName).stream()
                .peek(this::setRelicInfo)
                .collect(Collectors.toList());
    }

    @Transactional
    public RelicRepairOrder save(RelicRepairOrder order) {
        if (order.getRelicId() != null) {
            Relic relic = relicRepository.findById(order.getRelicId())
                    .orElseThrow(() -> new RuntimeException("Relic not found with id: " + order.getRelicId()));
            order.setRelic(relic);
        }

        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo(generateOrderNo());
        }

        if (order.getApplyDate() == null) {
            order.setApplyDate(LocalDate.now());
        }

        RelicRepairOrder saved = repairOrderRepository.save(order);
        setRelicInfo(saved);
        return saved;
    }

    @Transactional
    public RelicRepairOrder update(Long id, RelicRepairOrder orderDetails) {
        return repairOrderRepository.findById(id).map(order -> {
            order.setDamageDescription(orderDetails.getDamageDescription());
            order.setRepairRequirement(orderDetails.getRepairRequirement());
            order.setApplicant(orderDetails.getApplicant());
            order.setApplyDate(orderDetails.getApplyDate());
            order.setRepairer(orderDetails.getRepairer());
            order.setExpectedCompleteDate(orderDetails.getExpectedCompleteDate());
            order.setRepairProcess(orderDetails.getRepairProcess());
            order.setRepairResult(orderDetails.getRepairResult());
            order.setRemark(orderDetails.getRemark());

            if (orderDetails.getRelicId() != null) {
                Relic relic = relicRepository.findById(orderDetails.getRelicId())
                        .orElseThrow(() -> new RuntimeException("Relic not found with id: " + orderDetails.getRelicId()));
                order.setRelic(relic);
            }

            RelicRepairOrder updated = repairOrderRepository.save(order);
            setRelicInfo(updated);
            return updated;
        }).orElseThrow(() -> new RuntimeException("Repair order not found with id: " + id));
    }

    @Transactional
    public RelicRepairOrder startRepair(Long id, String repairer) {
        return repairOrderRepository.findById(id).map(order -> {
            if (!"PENDING".equals(order.getStatus())) {
                throw new RuntimeException("Only pending orders can be started");
            }
            order.setStatus("IN_PROGRESS");
            order.setStartDate(LocalDate.now());
            if (repairer != null && !repairer.isEmpty()) {
                order.setRepairer(repairer);
            }
            RelicRepairOrder updated = repairOrderRepository.save(order);
            setRelicInfo(updated);
            return updated;
        }).orElseThrow(() -> new RuntimeException("Repair order not found with id: " + id));
    }

    @Transactional
    public RelicRepairOrder completeRepair(Long id, String repairResult) {
        return repairOrderRepository.findById(id).map(order -> {
            if (!"IN_PROGRESS".equals(order.getStatus())) {
                throw new RuntimeException("Only in-progress orders can be completed");
            }
            order.setStatus("COMPLETED");
            order.setActualCompleteDate(LocalDate.now());
            if (repairResult != null && !repairResult.isEmpty()) {
                order.setRepairResult(repairResult);
            }
            RelicRepairOrder updated = repairOrderRepository.save(order);
            setRelicInfo(updated);
            return updated;
        }).orElseThrow(() -> new RuntimeException("Repair order not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        repairOrderRepository.deleteById(id);
    }

    public List<Map<String, Object>> getStatusStatistics() {
        List<Object[]> data = repairOrderRepository.countByStatus();
        return data.stream()
                .map(arr -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    String status = arr[0] != null ? arr[0].toString() : "UNKNOWN";
                    map.put("status", status);
                    map.put("statusName", RelicRepairOrder.Status.valueOf(status).getDisplayName());
                    map.put("count", ((Number) arr[1]).longValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private String generateOrderNo() {
        String prefix = "RR" + LocalDate.now().format(ORDER_NO_FORMATTER);
        int sequence = 1;
        String orderNo;
        do {
            orderNo = prefix + String.format("%04d", sequence++);
        } while (repairOrderRepository.existsByOrderNo(orderNo));
        return orderNo;
    }
}

package com.archaeology.relic.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "relic_repair_order")
public class RelicRepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relic_id", nullable = false)
    @JsonBackReference
    private Relic relic;

    @Transient
    private Long relicId;

    @Transient
    private String relicNo;

    @Transient
    private String relicName;

    @Column(length = 50, nullable = false)
    private String status;

    @Column(length = 2000, nullable = false)
    private String damageDescription;

    @Column(length = 500)
    private String repairRequirement;

    @Column(length = 200, nullable = false)
    private String applicant;

    @Column(nullable = false)
    private LocalDate applyDate;

    @Column(length = 200)
    private String repairer;

    private LocalDate startDate;

    private LocalDate expectedCompleteDate;

    private LocalDate actualCompleteDate;

    @Column(length = 2000)
    private String repairProcess;

    @Column(length = 2000)
    private String repairResult;

    @Column(length = 2000)
    private String remark;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public enum Status {
        PENDING("待修复"),
        IN_PROGRESS("修复中"),
        COMPLETED("已完成");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

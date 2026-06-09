package com.archaeology.relic.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "restoration_record")
public class RestorationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relic_id", nullable = false)
    @JsonBackReference
    private Relic relic;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(length = 200, nullable = false)
    private String operator;

    @Column(length = 500, nullable = false)
    private String damageDescription;

    @Column(length = 2000, nullable = false)
    private String restorationMethod;

    @Column(length = 500)
    private String materialsUsed;

    @Column(length = 2000)
    private String restorationEffect;

    @Column(length = 500)
    private String remark;

    @Column(nullable = false)
    private LocalDate createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDate.now();
    }
}

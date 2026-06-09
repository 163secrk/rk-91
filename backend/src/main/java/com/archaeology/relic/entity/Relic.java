package com.archaeology.relic.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "relic")
public class Relic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String relicNo;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 500)
    private String category;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String material;

    @Column(length = 200)
    private String era;

    @Column(nullable = false)
    private LocalDate excavateDate;

    @Column(length = 200, nullable = false)
    private String excavator;

    @Column(length = 500)
    private String excavationSite;

    @Column(length = 200)
    private String stratum;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coordinate_id", referencedColumnName = "id")
    private Coordinate3D coordinate;

    @Column(length = 50)
    private String preservationStatus;

    @OneToMany(mappedBy = "relic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RestorationRecord> restorationRecords = new ArrayList<>();

    @Column(length = 2000)
    private String remark;

    @Column(nullable = false)
    private LocalDate createTime;

    @Column(nullable = false)
    private LocalDate updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDate.now();
        updateTime = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDate.now();
    }
}

package com.archaeology.relic.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "excavation_unit")
public class ExcavationUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String unitNo;

    @Column(length = 500)
    private String location;

    @Column(nullable = false)
    private Double length;

    @Column(nullable = false)
    private Double width;

    @Column
    private Double depth;

    @Column(length = 50, nullable = false)
    private String status;

    @Column(length = 200, nullable = false)
    private String director;

    @Column(length = 2000)
    private String remark;

    @OneToMany(mappedBy = "excavationUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Relic> relics = new ArrayList<>();

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

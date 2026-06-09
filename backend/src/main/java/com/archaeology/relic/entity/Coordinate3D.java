package com.archaeology.relic.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "coordinate_3d")
public class Coordinate3D {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @Column(nullable = false)
    private Double z;

    @Column(length = 100)
    private String coordinateSystem;

    @Column(length = 500)
    private String remark;
}

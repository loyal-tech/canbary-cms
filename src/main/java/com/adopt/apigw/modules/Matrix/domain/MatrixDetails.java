package com.adopt.apigw.modules.Matrix.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tblmtatmatrixdetails")
public class MatrixDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Column(name = "order_no")
    Long orderNo;

    @Column(name = "level")
    String level;

    @Column(name = "mtime")
    String mtime;

    @Column(name = "munit")
    String munit;

    @Column(name = "action")
    String action;

    @Column(name = "tat_management_id")
    Long tatManagementId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;



}

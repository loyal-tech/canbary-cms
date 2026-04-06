package com.adopt.apigw.modules.TicketTatMatrix.Domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblttickettatmatrixmapping")
public class TicketTatMatrixMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_no")
    Long orderNo;

    @Column(name = "level")
    String level;

    @Column(name = "time_p1")
    Long mtime1;

    @Column(name = "time_p2")
    Long mtime2;

    @Column(name = "time_p3")
    Long mtime3;

    @Column(name = "munit")
    String munit;

    @Column(name = "action")
    String action;

    @Column(name = "tat_mapping_id")
    Long tatMappingtId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

}

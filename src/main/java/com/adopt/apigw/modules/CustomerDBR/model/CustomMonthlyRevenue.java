package com.adopt.apigw.modules.CustomerDBR.model;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblmonthwiserevenue")
public class CustomMonthlyRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",length = 20)
    Long id;
    @Column(name = "month",length = 40)
    Integer month;
    @Column(name = "year",length = 40)
    String year;
    @Column(name = "revenue",length = 20)
    Double revenue;
    @Column(name = "outstanding",length = 50)
    Double outstanding;
    @Column(name = "mvnoid",length = 50)
    Integer mvnoId;
    @Column(name = "buid",length = 50)
    Long buId;
    @Column(name = "service_area_id",length = 50)
    Long serviceAreaId;
}

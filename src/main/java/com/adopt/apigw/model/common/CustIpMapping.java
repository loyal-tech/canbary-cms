package com.adopt.apigw.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblcustipmapping")
public class CustIpMapping extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="custid",nullable = false)
    private Integer custid;

    @Column(name="ip_address", nullable = false)
    private String ipAddress;

    @Column(name="ip_type",nullable = false)
    private String ipType;

    @Column(name="custsermappingid",nullable = false)
    private Integer custsermappingid;

    @Column(name="service")
    private String service;



}

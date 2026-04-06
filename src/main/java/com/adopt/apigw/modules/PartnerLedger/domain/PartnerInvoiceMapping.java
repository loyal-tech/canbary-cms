package com.adopt.apigw.modules.PartnerLedger.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tbltpartnerinvoicemapping")
public class PartnerInvoiceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    private Long partnerLedgerId;
    private Long debitdocId;
    private Long cprId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;





}

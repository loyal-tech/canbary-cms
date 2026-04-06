package com.adopt.apigw.modules.PartnerLedger.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PartnerLedgerBalanceDTO {
    private Double amount = 0.0;
    private Integer credit = 0;
    private Integer partner_id;
    private String description;
    private String paymentmode;
    private String chequenumber;
    private LocalDate chequedate;
    private String refno;
    private LocalDate paymentdate;
    private String bank_name;
    private String branch_name;

    private String onlinesource;
    private Long sourceBank;
    private Long destinationBank;
    public PartnerLedgerBalanceDTO(Double amount, Integer partner_id, String description, String paymentmode, String refno, LocalDate paymentdate) {
        this.amount = amount;
        this.partner_id = partner_id;
        this.description = description;
        this.paymentmode = paymentmode;
        this.refno = refno;
        this.paymentdate = paymentdate;
    }

    public PartnerLedgerBalanceDTO(Double amount, Integer partner_id, LocalDate paymentdate) {
        this.amount = amount;
        this.partner_id = partner_id;
        this.paymentdate = paymentdate;
    }

    public PartnerLedgerBalanceDTO() {
    }
}

package com.adopt.apigw.modules.PartnerLedger.model;

import lombok.Data;

import java.util.List;

@Data
public class PartnerLedgerInfoPojo {
    private Double openingAmount;
    private List<PartnerLedgerDetailsDTO> debitCreditDetail;
    private Double closingBalance;
}

package com.adopt.apigw.modules.purchaseDetails.model;

import lombok.Data;

@Data
public class PGResponseModel {
    String pgStatus = null;
    String pgPayId = null;
    Double pgAmount = null;
    String sysTxnId = null;
    String sysStatus = null;

    public PGResponseModel(String pgStatus, String pgPayId, Double pgAmount, String sysTxnId) {
        this.pgStatus = pgStatus;
        this.pgPayId = pgPayId;
        this.pgAmount = pgAmount;
        this.sysTxnId = sysTxnId;
    }
}



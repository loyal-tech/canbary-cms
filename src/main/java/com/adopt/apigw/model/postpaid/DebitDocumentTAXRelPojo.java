package com.adopt.apigw.model.postpaid;

import lombok.Data;

@Data
public class DebitDocumentTAXRelPojo {

    String taxname;
    Double percentage;
    Double amount;
    int chargeId;

    public DebitDocumentTAXRelPojo(String taxname, Double percentage, Double amount,int chargeId) {
        this.taxname=taxname;
        this.percentage=percentage;
        this.amount=amount;
        this.chargeId=chargeId;

    }

}

package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitDocumentDTOForAdjustment {

    Integer id;

    Double totalAmount;

    Double adjustedAmount;

    Integer custId;

    String status;

    public DebitDocumentDTOForAdjustment(Integer id, Double totalAmount, Double adjustedAmount, Integer custId) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.adjustedAmount = adjustedAmount;
        this.custId = custId;
    }


}

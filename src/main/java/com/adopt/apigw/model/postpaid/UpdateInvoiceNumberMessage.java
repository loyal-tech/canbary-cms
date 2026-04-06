package com.adopt.apigw.model.postpaid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInvoiceNumberMessage {
    private Integer debitDocumentId;

    private String debitDocumentNumber;
}

package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.DebitDocDetails;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.DebitDocumentTAXRel;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class CancelRegenerateInvoice {

    private Integer id;
    public CancelRegenerateInvoice(Integer obj){
        this.id=obj;
    }
}

package com.adopt.apigw.model.postpaid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
public class DebitDocumentSummary {
    private DebitDocument debitDocument; // Example field for the entity
    private Double totalAmount;

    public DebitDocumentSummary(DebitDocument debitDocument, Double totalAmount){
        this.debitDocument=debitDocument;
        this.totalAmount=totalAmount;

    }
}

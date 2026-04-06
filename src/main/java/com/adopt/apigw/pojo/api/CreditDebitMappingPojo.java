package com.adopt.apigw.pojo.api;


import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitMappingPojo {

    private Integer invoiceId;

    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    @JsonBackReference
    private List<CreditDebitDataPojo> creditDocumentList;
}

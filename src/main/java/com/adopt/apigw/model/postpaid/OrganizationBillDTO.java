package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Customers;
import lombok.Data;

@Data
public class OrganizationBillDTO {
    DebitDocument debitDocument;
    Customers actualCustomers;
}

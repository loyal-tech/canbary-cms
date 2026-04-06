package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.model.postpaid.CreditDebitDocMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditDebitDocMessage {
    List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
}

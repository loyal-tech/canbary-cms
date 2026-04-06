package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

import java.util.List;

@Data
public class ListOfCreditDocForBatch {

    List<CreditDocMessage> creditDocMessageList;
}

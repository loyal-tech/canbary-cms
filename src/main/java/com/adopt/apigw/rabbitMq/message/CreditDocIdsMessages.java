package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditDocIdsMessages {

    List<Integer> creditDocumentIds =new ArrayList<>();

    String action;

}

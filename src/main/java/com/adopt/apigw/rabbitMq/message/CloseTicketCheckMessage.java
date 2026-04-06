package com.adopt.apigw.rabbitMq.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseTicketCheckMessage {

    HashMap<String, Object> customerMessage = new HashMap<>();

    Integer customerId ;
    Integer caseId;
    String caseNumber;
    String status;
    Integer staffId;


}

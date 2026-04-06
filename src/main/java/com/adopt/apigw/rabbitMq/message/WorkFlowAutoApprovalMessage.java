package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkFlowAutoApprovalMessage {

    Integer customerId ;
    String triggeredAction;
    Integer mvnoId;
    Integer buId;

}

package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustPlanMappingUpdateMessage {
    List<Integer> customerPlanMappingIds;
    Integer debitDocumentId;
}

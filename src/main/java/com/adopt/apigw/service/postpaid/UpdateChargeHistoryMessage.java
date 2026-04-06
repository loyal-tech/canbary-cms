package com.adopt.apigw.service.postpaid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChargeHistoryMessage {

    private List<Integer> custCharhistoryIds;

    private Double newAmount;

    private Double taxAmount;


}

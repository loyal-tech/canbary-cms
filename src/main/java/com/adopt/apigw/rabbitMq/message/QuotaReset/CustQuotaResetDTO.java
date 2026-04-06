package com.adopt.apigw.rabbitMq.message.QuotaReset;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class CustQuotaResetDTO {

    private HashMap<Integer, String> custNextQuotaResetDate;
    private HashMap<Integer, String> custNextBillDate;
    private HashMap<Long, String> cprLastQuotaResetDate;
    private HashMap<Long, String> cprEndDate;


    public CustQuotaResetDTO(HashMap<Integer, String> custNextQuotaResetDate, HashMap<Integer, String> custNextBillDate, HashMap<Long, String> cprLastQuotaResetDate, HashMap<Long, String> cprEndDate) {
        this.custNextQuotaResetDate = custNextQuotaResetDate;
        this.custNextBillDate = custNextBillDate;
        this.cprLastQuotaResetDate = cprLastQuotaResetDate;
        this.cprEndDate = cprEndDate;
    }
}

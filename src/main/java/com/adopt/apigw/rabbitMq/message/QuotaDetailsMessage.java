package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = QuotaDetailsMessage.class)
public class QuotaDetailsMessage {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private List data;

    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";
    private static final String ID = "id";
    private static final String PLAN_ID = "planId";
    private static final String QUOTA_TYPE = "quotaType";
    private static final String TOTAL_QUOTA = "totalQuota";
    private static final String USED_QUOTA = "usedQuota";
    private static final String QUOTA_UNIT = "quotaUnit";
    private static final String TIME_TOTAL_QUOTA = "timeTotalQuota";
    private static final String TIME_QUOTA_USED = "timeQuotaUsed";
    private static final String TIME_QUOTA_UNIT = "timeQuotaUnit";
    private static final String IS_DELETE = "isDelete";
    private static final String TOTAL_QUOTA_KB = "totalQuotaKB";
    private static final String USED_QUOTA_KB = "usedQuotaKB";
    private static final String TIME_USED_QUOTA_SEC = "timeUsedQuotaSec";
    private static final String TIME_TOTAL_QUOTA_SEC = "timeTotalQuotaSec";
    private static final String DID_TOTAL_QUOTA = "didtotalquota";
    private static final String DID_USED_QUOTA = "didusedquota";
    private static final String INTERCOM_TOTAL_QUOTA = "intercomtotalquota";
    private static final String INTERCOM_USED_QUOTA = "intercomusedquota";
    private static final String DID_QUOTA_UNIT = "didQuotaUnit";
    private static final String INTERCOM_QUOTA_UNIT = "intercomQuotaUnit";
    private static final String PLAN_NAME = "planName";
    private static final String CUST_PLAN_MAPPPING = "custPlanMappping";
    private static final String CUSTOMER = "customer";

    public QuotaDetailsMessage(){}

    public QuotaDetailsMessage(List<CustQuotaDtlsPojo> custQuotaDtlsPojoList){
        List<Map> list = new ArrayList<>();
        for(CustQuotaDtlsPojo custQuotaDtlsPojo : custQuotaDtlsPojoList) {
            Map<String, Object> map = new HashMap<>();
            map.put(ID, custQuotaDtlsPojo.getId());
            map.put(PLAN_ID, custQuotaDtlsPojo.getId());
            map.put(QUOTA_TYPE, custQuotaDtlsPojo.getQuotaType());
            map.put(TOTAL_QUOTA, custQuotaDtlsPojo.getTotalQuota());
            map.put(USED_QUOTA, custQuotaDtlsPojo.getUsedQuota());
            map.put(QUOTA_UNIT, custQuotaDtlsPojo.getQuotaUnit());
            map.put(TIME_TOTAL_QUOTA, custQuotaDtlsPojo.getTotalQuota());
            map.put(TIME_QUOTA_USED, custQuotaDtlsPojo.getTimeQuotaUsed());
            map.put(TIME_QUOTA_UNIT, custQuotaDtlsPojo.getTimeQuotaUnit());
            map.put(IS_DELETE, custQuotaDtlsPojo.getIsDelete());
            map.put(TOTAL_QUOTA_KB, custQuotaDtlsPojo.getTotalQuotaKB());
            map.put(USED_QUOTA_KB, custQuotaDtlsPojo.getUsedQuotaKB());
            map.put(TIME_TOTAL_QUOTA_SEC, custQuotaDtlsPojo.getTimeTotalQuotaSec());
            map.put(TIME_USED_QUOTA_SEC, custQuotaDtlsPojo.getTimeUsedQuotaSec());
            map.put(DID_TOTAL_QUOTA, custQuotaDtlsPojo.getDidtotalquota());
            map.put(DID_USED_QUOTA, custQuotaDtlsPojo.getDidusedquota());
            map.put(INTERCOM_TOTAL_QUOTA, custQuotaDtlsPojo.getIntercomtotalquota());
            map.put(INTERCOM_USED_QUOTA, custQuotaDtlsPojo.getIntercomusedquota());
            map.put(DID_QUOTA_UNIT, custQuotaDtlsPojo.getDidQuotaUnit());
            map.put(INTERCOM_QUOTA_UNIT, custQuotaDtlsPojo.getIntercomQuotaUnit());
            map.put(PLAN_NAME, custQuotaDtlsPojo.getPlanName());
            map.put(CUST_PLAN_MAPPPING, custQuotaDtlsPojo.getCustPlanMappping());
            map.put(CUSTOMER, custQuotaDtlsPojo.getCustomer());
            list.add(map);
        }
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Quota Details from Api Gateway";
        this.data = list;
        this.sourceName = ADOPT_API_GATEWAY;
    }
}

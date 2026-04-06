package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustMacMappingMessage.class)
public class CustMacMappingMessage {

    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String MAC_ADDRESS = "macAddress";
    private static final String IS_DELETE = "isDelete";
    private static final String IS_MULTIPLE_DELETE = "isMultipleDelete";
    private static final String MVNO_ID = "mvnoId";
    private static final String USER_NAME = "userName";
    private static final String CUST_SERV_MAPID = "custsermapid";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

//    private

    public CustMacMappingMessage() {
    }

    public CustMacMappingMessage(CustMacMappping custPlanMappping, Integer mvnoId, String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, custPlanMappping.getId());
        map.put(MAC_ADDRESS, custPlanMappping.getMacAddress());
        map.put(CUST_ID, custPlanMappping.getCustomer() != null ? custPlanMappping.getCustomer().getId() : null);
        map.put(IS_DELETE, custPlanMappping.getIsDeleted());
        map.put(MVNO_ID, mvnoId);
        map.put(USER_NAME, userName);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Mac Mapping from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }

    public CustMacMappingMessage(CustMacMappping custMacMappping, Integer mvnoId, String userName,Integer customerServiceMappingId) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, custMacMappping.getId());
        map.put(MAC_ADDRESS, custMacMappping.getMacAddress());
        map.put(CUST_ID, custMacMappping.getCustomer() != null ? custMacMappping.getCustomer().getId() : null);
        map.put(IS_DELETE, custMacMappping.getIsDeleted());
        map.put(MVNO_ID, mvnoId);
        map.put(USER_NAME, userName);
        map.put(CUST_SERV_MAPID,custMacMappping.getCustsermappingid());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Mac Mapping from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }

    public CustMacMappingMessage(Customers customer) {
        Map<String, Object> map = new HashMap<>();
//        map.put(ID, custMacMappping.getId());
//        map.put(MAC_ADDRESS, custMacMappping.getMacAddress());
        map.put(CUST_ID, customer.getId());
        map.put(IS_DELETE, true);
        map.put(IS_MULTIPLE_DELETE, true);
        map.put(MVNO_ID, customer.getMvnoId());
        map.put(USER_NAME, customer.getUsername());
//        map.put(CUST_SERV_MAPID,custMacMappping.getCustsermappingid());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Mac Mapping from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }
}

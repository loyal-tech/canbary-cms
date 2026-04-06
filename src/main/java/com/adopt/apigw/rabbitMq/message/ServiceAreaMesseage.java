package com.adopt.apigw.rabbitMq.message;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = ServiceAreaMesseage.class)
public class ServiceAreaMesseage {

    private static final String ID = "id";
    private static final String AREA = "areaid";
    private static final String AREANAME = "areaname";
    private static final String STATUSSERVICEAREA = "statusservicearea";
    private static final String  ISDELETED = "isdeleted";
    private static final String  LATITUDE = "latitude";
    private static final String  LONGITUDE = "longitude";
    private static final String  MVNOID = "MvnoId";
    private static final String CREATED_DATE = "createdate";
    private static final String LAST_MODIFIED_DATE = "lastmodifieddate";
    private static final String CREATEDBYNAMESERVICEAREA = "createdByName";
    private static final String LASTMODIFIEDBYNAMESERVICEAREA = "lastModifiedByName";
    private static final String CREATED_BY_ID_SERVICEAREA= "createdById";
    private static final String LASTMODIFIED_BY_ID_SERVICEAREA = "lastModifiedById";
    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";


    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Object> customerData;


    public ServiceAreaMesseage(ServiceAreaDTO serviceArea){
        Map<String, Object> map = new HashMap<>();
        if(serviceArea.getId()!=null){
        map.put(ID,serviceArea.getId());}
        map.put(AREANAME,serviceArea.getName());
        map.put(STATUSSERVICEAREA,serviceArea.getStatus());
        map.put(ISDELETED,serviceArea.getIsDeleted());
        if(serviceArea.getLatitude()!=null){
        map.put(LATITUDE,serviceArea.getLatitude());}
       if(serviceArea.getLongitude()!=null){
        map.put(LONGITUDE,serviceArea.getLongitude());}
        if(serviceArea.getMvnoId()!=null){
        map.put(MVNOID,serviceArea.getMvnoId());}
        if (serviceArea.getCreatedate() != null) {
            map.put(CREATED_DATE,serviceArea.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (serviceArea.getUpdatedate() != null) {
            map.put(LAST_MODIFIED_DATE,serviceArea.getUpdatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(serviceArea.getCreatedByName()!=null){
        map.put(CREATEDBYNAMESERVICEAREA,serviceArea.getCreatedByName());}
        if (serviceArea.getLastModifiedByName() != null) {
            map.put(LASTMODIFIEDBYNAMESERVICEAREA,serviceArea.getLastModifiedByName());
        }
        map.put(CREATED_BY_ID_SERVICEAREA,serviceArea.getCreatedById());
        map.put(LASTMODIFIED_BY_ID_SERVICEAREA,serviceArea.getLastModifiedById());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "New serviceArea created from Api Gateway";
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }
}

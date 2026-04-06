package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = QosPolicyMessage.class)
public class QosPolicyMessage {

    //Qos Policy
    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String BASEUPLOADSPEED = "baseuploadspeed";
    private static final String BASEDOWNLOADSPEED = "basedownloadspeed";
    private static final String BASEPOLICYNAME = "basepolicyname";
    private static final String THUPLOADSPEED = "thuploadspeed";
    private static final String THDOWNLOADSPEED = "thdownloadspeed";
    private static final String THPOLICYNAME = "thpolicyname";
    private static final String BASEPARAM1 = "baseparam1";
    private static final String BASEPARAM2 = "baseparam2";
    private static final String BASEPARAM3 = "baseparam3";
    private static final String THPARAM1 = "thparam1";
    private static final String THPARAM2 = "thparam2";
    private static final String THPARAM3 = "thparam3";
    private static final String ISDELETED = "isDeleted";
    private static final String MVNOID = "mvnoId";

    private static final String TYPE = "type";

    private static final String QOSSPEED = "qosspeed";
    private static final String UPSTREAMPROFILEUID = "upstreamprofileuid";
    private static final String DOWNSTREAMPROFILEUID = "downstreamprofileuid";


    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

    private static final String qosPolicyGatewayMappingList = "qosPolicyGatewayMappingList";

    public QosPolicyMessage(){}

    public QosPolicyMessage(QOSPolicyDTO qosPolicyDTO){
        Map<String, Object> map = new HashMap<>();
        map.put(ID, qosPolicyDTO.getId());
        map.put(NAME, qosPolicyDTO.getName());
        map.put(DESCRIPTION, qosPolicyDTO.getDescription());
        map.put(BASEPOLICYNAME, qosPolicyDTO.getBasepolicyname());
        map.put(THPOLICYNAME, qosPolicyDTO.getThpolicyname());
        map.put(BASEPARAM1, qosPolicyDTO.getBaseparam1());
        map.put(BASEPARAM2, qosPolicyDTO.getBaseparam2());
        map.put(BASEPARAM3, qosPolicyDTO.getBaseparam3());
        map.put(THPARAM1, qosPolicyDTO.getThparam1());
        map.put(THPARAM2, qosPolicyDTO.getThparam2());
        map.put(THPARAM3, qosPolicyDTO.getThparam3());
        map.put(ISDELETED, qosPolicyDTO.getIsDeleted());
        map.put(MVNOID, qosPolicyDTO.getMvnoId());
        map.put(TYPE , qosPolicyDTO.getType());
        map.put(QOSSPEED , qosPolicyDTO.getQosspeed());
        map.put(UPSTREAMPROFILEUID , qosPolicyDTO.getUpstreamprofileuid());
        map.put(DOWNSTREAMPROFILEUID , qosPolicyDTO.getDownstreamprofileuid());
        map.put(qosPolicyGatewayMappingList, qosPolicyDTO.getQosPolicyGatewayMappingList());

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Qos policy from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }
}

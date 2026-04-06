package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = PostpaidPlanMessage.class)
public class PostpaidPlanMessage {

    //Plan
    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DISPLAYNAME = "displayName";
    private static final String CODE = "code";
    private static final String DESC = "desc";
    private static final String CATEGORY = "category";
    private static final String STARTDATE = "startDate";
    private static final String ENDDATE = "endDate";
    private static final String UPLOADQOS = "uploadQOS";
    private static final String DOWNLOADQOS = "downloadQOS";
    private static final String UPLOADTS = "uploadTs";
    private static final String DOWNLOADTS = "downloadTs";
    private static final String ALLOWOVERUSAGE = "allowOverUsage";
    private static final String QUOTAUNIT = "quotaUnit";
    private static final String QUOTA = "quota";
    private static final String PLANSTATUS = "planStatus";
    private static final String CHILDQUOTA = "childQuota";
    private static final String CHILDQUOTAUNIT = "childQuotaUnit";
    private static final String SLICE = "slice";
    private static final String SLICEUNIT = "sliceUnit";
    private static final String ATTACHEDTOALLHOTSPOTS = "attachedToAllHotSpots";
    private static final String PARAM1 = "param1";
    private static final String PARAM2 = "param2";
    private static final String PARAM3 = "param3";
    private static final String MVNOID = "mvnoId";
    private static final String STATUS = "status";
    private static final String TAXID = "taxId";
    private static final String SERVICEID = "serviceId";
    private static final String SERVICENAME = "serviceName";
    private static final String PLANTYPE = "plantype";
    private static final String MAXCHILD = "maxChild";
    private static final String CHARGELIST = "chargeList";
    private static final String DBR = "dbr";
    private static final String PLANGROUP = "planGroup";
    private static final String VALIDITY = "validity";
    private static final String SACCODE = "saccode";
    private static final String MAXCONCURRENTSESSION = "maxconcurrentsession";
    private static final String QUOTAUNITTIME = "quotaunittime";
    private static final String QUOTATIME = "quotatime";
    private static final String QUOTATYPE = "quotatype";
    private static final String OFFERPRICE = "offerprice";
    private static final String QOSPOLICYID = "qospolicyid";
    private static final String TIMEBASEPOLICYID = "timebasepolicyId";
    private static final String RADIUSPROFILEIDS = "radiusprofileIds";
    private static final String ISDELETE = "isDelete";
    private static final String CREATEDATESTRING = "createDateString";
    private static final String UPDATEDATESTRING = "updateDateString";
    private static final String QUOTADID = "quotadid";
    private static final String QUOTAINTERCOM = "quotaintercom";
    private static final String QUOTAUNITDID = "quotaunitdid";
    private static final String QUOTAUNITINTERCOM = "quotaunitintercom";
    private static final String DATACATEGORY = "dataCategory";
    private static final String TAXAMOUNT = "taxamount";
    private static final String SERVICEAREAIDS = "serviceAreaIds";
    private static final String SERVICEAREANAMELIST = "serviceAreaNameList";
    private static final String  QUOTARESETINTERVAL = "quotaresetInterval";
    private static final String  UNITSOFVALIDITY = "unitsOfValidity";

    private static  final String PLANQOSMAPPINGLIST = "planQosMappingList";
    private static  final String CHUNK = "chunk";
    private static  final String USEQUOTA = "useQuota";
    private static  final String USAGEQUOTATYPE = "usageQuotaType";
    private static final String ADDONTOBASE = "addonToBase";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;
    private String planData;
    private boolean isTriggerCoaDm;
    private boolean updateAllCustPlan;
    List<PostpaidPlanMessage> postpaidPlanMessages = new ArrayList<>();

    public PostpaidPlanMessage(){}

    public PostpaidPlanMessage(PostpaidPlanPojo postpaidPlanPojo){
        Map<String, Object> map = new HashMap<>();
        map.put(ID, postpaidPlanPojo.getId());
        map.put(NAME, postpaidPlanPojo.getName());
        map.put(DISPLAYNAME, postpaidPlanPojo.getDisplayName());
        map.put(CODE, postpaidPlanPojo.getCode());
        map.put(DESC, postpaidPlanPojo.getDesc());
        map.put(CATEGORY, postpaidPlanPojo.getCategory());
        if(postpaidPlanPojo.getStartDate() != null)
            map.put(STARTDATE, postpaidPlanPojo.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if(postpaidPlanPojo.getEndDate() != null)
            map.put(ENDDATE, postpaidPlanPojo.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.put(UPLOADQOS, postpaidPlanPojo.getUploadQOS());
        map.put(DOWNLOADQOS, postpaidPlanPojo.getDownloadQOS());
        map.put(UPLOADTS, postpaidPlanPojo.getUploadTs());
        map.put(DOWNLOADTS, postpaidPlanPojo.getDownloadTs());
        map.put(ALLOWOVERUSAGE, postpaidPlanPojo.getAllowOverUsage());
        map.put(QUOTAUNIT, postpaidPlanPojo.getQuotaUnit());
        map.put(QUOTA, postpaidPlanPojo.getQuota());
        map.put(PLANSTATUS, postpaidPlanPojo.getPlanStatus());
        map.put(CHILDQUOTA, postpaidPlanPojo.getChildQuota());
        map.put(CHILDQUOTAUNIT, postpaidPlanPojo.getChildQuotaUnit());
        map.put(SLICE, postpaidPlanPojo.getSlice());
        map.put(SLICEUNIT, postpaidPlanPojo.getSliceUnit());
        map.put(ATTACHEDTOALLHOTSPOTS, postpaidPlanPojo.getAttachedToAllHotSpots());
        map.put(PARAM1, postpaidPlanPojo.getParam1());
        map.put(PARAM2, postpaidPlanPojo.getParam2());
        map.put(PARAM3, postpaidPlanPojo.getParam3());
        map.put(MVNOID, postpaidPlanPojo.getMvnoId());
        map.put(STATUS, postpaidPlanPojo.getStatus());
        map.put(TAXID, postpaidPlanPojo.getTaxId());
        map.put(SERVICEID, postpaidPlanPojo.getServiceId());
        map.put(SERVICENAME, postpaidPlanPojo.getServiceName());
        map.put(PLANTYPE, postpaidPlanPojo.getPlantype());
        map.put(MAXCHILD, postpaidPlanPojo.getMaxChild());
        map.put(CHARGELIST, postpaidPlanPojo.getChargeList());
        map.put(DBR, postpaidPlanPojo.getDbr());
        map.put(PLANGROUP, postpaidPlanPojo.getPlanGroup());
        map.put(VALIDITY, postpaidPlanPojo.getValidity());
        map.put(SACCODE, postpaidPlanPojo.getSaccode());
        map.put(MAXCONCURRENTSESSION, postpaidPlanPojo.getMaxconcurrentsession());
        map.put(QUOTAUNITTIME, postpaidPlanPojo.getQuotaunittime());
        map.put(QUOTATIME, postpaidPlanPojo.getQuotatime());
        map.put(QUOTATYPE, postpaidPlanPojo.getQuotatype());
        map.put(OFFERPRICE, postpaidPlanPojo.getOfferprice());
        map.put(QOSPOLICYID, postpaidPlanPojo.getQospolicyid());
        map.put(TIMEBASEPOLICYID,postpaidPlanPojo.getTimebasepolicyId());
        map.put(RADIUSPROFILEIDS, postpaidPlanPojo.getRadiusprofileIds());
        map.put(ISDELETE, postpaidPlanPojo.getIsDelete());
        map.put(CREATEDATESTRING, postpaidPlanPojo.getCreateDateString());
        map.put(UPDATEDATESTRING, postpaidPlanPojo.getUpdateDateString());
        map.put(QUOTADID, postpaidPlanPojo.getQuotadid());
        map.put(QUOTAINTERCOM, postpaidPlanPojo.getQuotaintercom());
        map.put(QUOTAUNITDID, postpaidPlanPojo.getQuotaunitdid());
        map.put(QUOTAUNITINTERCOM, postpaidPlanPojo.getQuotaunitintercom());
        map.put(DATACATEGORY, postpaidPlanPojo.getDataCategory());
        map.put(TAXAMOUNT, postpaidPlanPojo.getTaxamount());
        map.put(SERVICEAREAIDS, postpaidPlanPojo.getServiceAreaIds());
        map.put(SERVICEAREANAMELIST, postpaidPlanPojo.getServiceAreaNameList());
        map.put(QUOTARESETINTERVAL,postpaidPlanPojo.getQuotaResetInterval());
        map.put(UNITSOFVALIDITY, postpaidPlanPojo.getUnitsOfValidity());
        map.put(CHUNK, postpaidPlanPojo.getChunk());
        map.put(USEQUOTA, postpaidPlanPojo.isUseQuota());
        map.put(USAGEQUOTATYPE, postpaidPlanPojo.getUsageQuotaType());
        map.put(PLANQOSMAPPINGLIST,postpaidPlanPojo.getPlanQosMappingEntityList());
        map.put(ADDONTOBASE,postpaidPlanPojo.getAddonToBase());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Plan from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
        this.planData = postpaidPlanPojo.toString();
    }

    public PostpaidPlanMessage(boolean isTriggerCoaDm, boolean updateAllCustPlan, List<PostpaidPlanPojo> postpaidPlanDTOS) {
        this.isTriggerCoaDm = isTriggerCoaDm;
        this.updateAllCustPlan = updateAllCustPlan;
        List<PostpaidPlanMessage> postpaidPlanMessages = new ArrayList<>();
        for (PostpaidPlanPojo plan: postpaidPlanDTOS) {
            PostpaidPlanMessage msg = new PostpaidPlanMessage(plan);
            postpaidPlanMessages.add(msg);
        }
        this.postpaidPlanMessages = postpaidPlanMessages;
    }
}

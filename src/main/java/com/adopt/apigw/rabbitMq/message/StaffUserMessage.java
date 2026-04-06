package com.adopt.apigw.rabbitMq.message;
import com.adopt.apigw.model.common.StaffUserBusinessUnitMapping;
import com.adopt.apigw.model.common.StaffUserServiceAreaMapping;
//import com.adopt.apigw.modules.BusinessUnit.model.BusinessUnitDTO;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = StaffUserMessage.class)
public class StaffUserMessage {

    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";
    private static final String STAFF_USER_SEND_RADIUS = "staff user send radius";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String EMAIL = "email";
    private static final String PHONE_NO = "phone";
    private static final String FAIL_COUNT = "failcount";
    private static final String STATUS = "status";
    private static final String LAST_LOGIN_TIME = "last_login_time";
    private static final String CREATEDATE = "createdate";
    private static final String UPDATEDATE = "updatedate";
    private static final String PARTNER_ID = "partnerid";
    private static final String NEWPASSWORD = "newpassword";
    private static final String ROLEIDS = "roleIds";
    private static final String TEAMIDS = "teamIds";
    private static final String TEAMNAMELIST = "teamNameList";
    private static final String ISDELETE = "isDelete";
    private static final String FULL_NAME = "fullName";
    private static final String SYS_STAFF = "sysstaff";
    private static final String OTP = "otp";
    private static final String OTP_VALIDATE = "otpvalidate";
    private static final String SERVICE_AREA = "service_area_id";

    //private static final String BUSINESS_UNIT = "businessunitid";
    private static final String STAFF_USER_PARENT = "parent_staff_id";
    private static final String MVNO_ID = "mvnoId";
    private static final String SERIVICE_AREA_NAME_LIST = "serviceAreaNameList";
    //private static final String BUSINESS_UNIT_NAME_LIST = "businessUnitNameList";
    private static final String CREATEDBYID = "createdbyid";
    private static final String LASTMODIFIEDBYID = "lastmodifiedbyid";
    private static final String CREATEDBYNAME = "createdByName";
    private static final String LASTMODIFIEDBYNAME = "lastmodifiedbyname";

    //ServiceAreaMapping
    private static final String SERIVEAREAMAPPINGID = "seriveareamappingid";
    private static final String SERVICEAREAID = "serviceareaid";
    private static final String STAFFID = "staffid";
    private static final String CREATED_ON = "created_on";
    private static final String LASTMODIFIED_ON = "lastmodified_on";
    private static final String CREATED_DATE = "createdate";
    private static final String UPDATE_DATE = "updatedate";
    private static final String CREATEDBYNAMESERVICEAREA = "createdByName";
    private static final String LASTMODIFIEDBYNAMESERVICEAREA = "lastModifiedByName";
    private static final String CREATED_BY_ID_SERVICEAREA= "createdById";
    private static final String LASTMODIFIED_BY_ID_SERVICEAREA = "lastModifiedById";

    //Business Unit Mapping
//    private static final String BUSINESSUNITMAPPINGID = "businessunitmappingid";
//    private static final String BUSINESSUNITID = "businessunitid";
//    private static final String CREATEDBYNAMEBUSINESSUNIT = "createdByName";
//    private static final String LASTMODIFIEDBYNAMEBUSINESSUNIT = "lastModifiedByName";
//    private static final String CREATED_BY_ID_BUSINESSUNIT= "createdById";
//    private static final String LASTMODIFIED_BY_ID_BUSINESSUNIT = "lastModifiedById";
    //ServiceAre

    //  private  List<StaffUserServiceAreaMapping> serviceAreaMapping;

    private static final String AREA = "area";
    private static final String AREANAME = "areaname";
    private static final String STATUSSERVICEAREA = "statusservicearea";
    private static final String STATUSBUSINESSUNIT = "statusbusinessunit";
    private static final String  IDENTITYKEY = "identityKey";
    private static final String  ISDELETED = "isdeleted";
    private static final String  LATITUDE = "latitude";
    private static final String  LONGITUDE = "longitude";
    private static final String  MVNOID = "MvnoId";

    private static final String COUNTRY_CODE = "countryCode";





    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Object> customerData;

    public StaffUserMessage(StaffUserPojo user, List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings, List<ServiceAreaDTO> serviceAreaDTOS) {
        Map<String, Object> map = new HashMap<>();
        map.put(ID, user.getId());
        map.put(USERNAME, user.getUsername());
        map.put(PASSWORD, user.getPassword());
        map.put(FIRSTNAME, user.getFirstname());
        map.put(LASTNAME, user.getLastname());
        map.put(EMAIL, user.getEmail());
        map.put(COUNTRY_CODE,user.getCountryCode());
        map.put(PHONE_NO, user.getPhone());
        map.put(FAIL_COUNT, user.getFailcount());
        map.put(STATUS, user.getStatus());
        if (user.getLast_login_time() != null) {
            map.put(LAST_LOGIN_TIME, user.getLast_login_time().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (user.getCreatedate() != null) {
            map.put(CREATEDATE, user.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (user.getUpdatedate() != null) {
            map.put(UPDATEDATE, user.getUpdatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        map.put(PARTNER_ID, user.getPartnerid());
        map.put(NEWPASSWORD, user.getNewpassword());
        map.put(ROLEIDS, user.getRoleIds());
        map.put(TEAMIDS, user.getTeamIds());
        map.put(ISDELETE, user.getIsDelete());
        map.put(FULL_NAME, user.getFullName());
        map.put(SYS_STAFF, user.getSysstaff());
        map.put(SERVICE_AREA, user.getServicearea());
        //map.put(BUSINESS_UNIT, user.getBusinessUnit());
        map.put(STAFF_USER_PARENT, user.getParentStaffId());
        map.put(MVNO_ID, user.getMvnoId());
        map.put(CREATEDBYID, user.getCreatedById());
        map.put(LASTMODIFIEDBYID, user.getLastModifiedById());
        map.put(CREATEDBYNAME, user.getCreatedByName());
        map.put(LASTMODIFIEDBYNAME, user.getLastModifiedByName());

        List<Map> serviceAreaList= new ArrayList<>();
        //List<Map> businessUnitList = new ArrayList<>();

        for(ServiceAreaDTO serviceAreaDTO : serviceAreaDTOS){
            Map<String, Object> serviceArea = new HashMap<>();
            serviceArea.put(SERIVEAREAMAPPINGID, serviceAreaDTO.getId());
            serviceArea.put(SERVICEAREAID,serviceAreaDTO.getAreaid());
            serviceArea.put(STATUSSERVICEAREA,serviceAreaDTO.getStatus());
            serviceArea.put(IDENTITYKEY,serviceAreaDTO.getIdentityKey());
            serviceArea.put(ISDELETE,serviceAreaDTO.getIsDeleted());
            serviceArea.put(LATITUDE,serviceAreaDTO.getLatitude());
            serviceArea.put(LONGITUDE,serviceAreaDTO.getLongitude());
            serviceArea.put(MVNOID,serviceAreaDTO.getMvnoId());
            if(serviceAreaDTO.getCreatedate() != null ) {
                serviceArea.put(CREATEDATE, serviceAreaDTO.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            if(serviceAreaDTO.getCreatedById() != null) {
                serviceArea.put(CREATEDBYID, serviceAreaDTO.getCreatedById());
            }
            if(serviceAreaDTO.getCreatedByName() != null) {
                serviceArea.put(CREATEDBYNAME, serviceAreaDTO.getCreatedByName());
            }
            if(serviceAreaDTO.getLastModifiedById() != null) {
                serviceArea.put(LASTMODIFIEDBYID, serviceAreaDTO.getLastModifiedById());
            }
            if(serviceAreaDTO.getUpdatedate() != null) {
                serviceArea.put(UPDATEDATE, serviceAreaDTO.getUpdatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            if(serviceAreaDTO.getLastModifiedByName() != null) {
                serviceArea.put(LASTMODIFIEDBYNAME, serviceAreaDTO.getLastModifiedByName());
            }
            serviceAreaList.add(serviceArea);
        }

//        for(BusinessUnitDTO businessUnitDTO : businessUnitDTOS){
//            Map<String, Object> businessUnit = new HashMap<>();
//            businessUnit.put(BUSINESSUNITMAPPINGID, businessUnitDTO.getId());
//            businessUnit.put(STATUSBUSINESSUNIT,businessUnitDTO.getStatus());
//            businessUnit.put(IDENTITYKEY,businessUnitDTO.getIdentityKey());
//            businessUnit.put(ISDELETE,businessUnitDTO.getIsDeleted());
//            businessUnit.put(MVNOID,businessUnitDTO.getMvnoId());
//            businessUnit.put(CREATEDATE,businessUnitDTO.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            businessUnit.put(CREATEDBYID,businessUnitDTO.getCreatedById());
//            businessUnit.put(CREATEDBYNAME,businessUnitDTO.getCreatedByName());
//            businessUnit.put(LASTMODIFIEDBYID,businessUnitDTO.getLastModifiedById());
//            businessUnit.put(UPDATEDATE,businessUnitDTO.getUpdatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            businessUnit.put(LASTMODIFIEDBYNAME,businessUnitDTO.getLastModifiedByName());
//            serviceAreaList.add(businessUnit);
//        }
        map.put(SERIVICE_AREA_NAME_LIST, serviceAreaList);
        //map.put(BUSINESS_UNIT_NAME_LIST, businessUnitList);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "New staff created from Api Gateway";
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;

    }

}


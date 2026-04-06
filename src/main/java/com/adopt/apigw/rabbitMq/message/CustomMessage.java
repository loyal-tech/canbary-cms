package com.adopt.apigw.rabbitMq.message;


import com.adopt.apigw.mapper.postpaid.CustMacMapper;
import com.adopt.apigw.model.common.CustIpMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import com.adopt.apigw.pojo.UpdatePasswordResetDto;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.adopt.apigw.pojo.api.CustomerLocationMappingDto;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.repository.postpaid.CustMacMapppingRepository;
import com.adopt.apigw.schedulers.UpdateCustomerQuotaDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class CustomMessage {

    @Autowired
    private CustMacMapper macMapper;

    @Autowired
    private CustMacMapppingRepository custMacMapppingRepository;
    
    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String CUSTNAME = "custname";
    private static final String CONTACTPERSON = "contactperson";
    private static final String CAFNO = "cafno";
    private static final String EMAIL="email";
    private static final String MACTELFLAG="mactelflag";
    private static final String MOBILE="mobile";
    private static final String VOICESRVTYPE="voicesrvtype";
    private static final String VOICEPROVISION = "voiceprovision";
    private static final String INTERCOMNO="intercomno";
    private static final String INTERCOMGRP="intercomgrp";
    private static final String ONLINERENEWALFLAG = "onlinerenewalflag";
    private static final String VOIPENABLEFLAG = "voipenableflag";
    private static final String CUSTCATEGORY="custcategory";
    private static final String NETWORKTYPE="networktype";
    private static final String DEFAULTPOOLID="defaultpoolid";
    private static final String SERVICEAREA="servicearea";
    private static final String STATUS="status";
    private static final String INVOICEOPTION="invoiceOption";
    private static final String FAILCOUNT="failcount";
    private static final String LAST_PASSWORD_CHANGE="last_password_change";
    private static final String ACCTNO="acctno";
    private static final String CUSTTYPE="custtype"; //Postpaid,Prepaid
    private static final String PHONE="phone";
    private static final String LASTSTATUSCHANGEDATE="lastStatusChangeDate";
    private static final String BILLDAY="billday";
    private static final String PARTNER="partner";
    private static final String CUSTOMERPAYMENTS="customerPayments";
    private static final String NEXTBILLDATE="nextBillDate";
    private static final String NEXTQUOTARESETDATE="nextQuotaResetDate";
    private static final String LASTBILLDATE="lastBillDate";
    private static final String RADIUSPROFILES = "radiusProfiles";
    private static final String PLANMAPPINGLIST = "planMappingList";
    private static final String CUSTMACMAPPPINGLIST = "custMacMapppingList";
    private static final String CUSTLOCATIONMAPPPINGLIST = "custLocationMappingList";
    private static final String NEWPASSWORD="newpassword";
    private static final String OLDBNGROUTERINTERFACE="OldBNGRouterinterface";
    private static final String OLDVSINAME="OldVSIName";
    private static final String BNGROUTERINTERFACE="BNGRouterinterface";
    private static final String BNGROUTERNAME="BNGRoutername";
    private static final String IPPREFIXES="IPPrefixes";
    private static final String IPV6PREFIXES="IPV6Prefixes";
    private static final String LANIP="LANIP";
    private static final String LANIPV6="LANIPV6";
    private static final String LLACCOUNTID="LLAccountid";
    private static final String LLCONNECTIONTYPE="LLConnectiontype";
    private static final String LLEXPIRYDATE="LLExpirydate";
    private static final String LLMEDIUM="LLMedium";
    private static final String LLSERVICEID="LLServiceid";
    private static final String MACADDRESS="MACADDRESS";
    private static final String PEERIP="Peerip";
    private static final String POOLIP="POOLIP";
    private static final String QOS="QOS";
    private static final String RDEXPORT="RDExport";
    private static final String RDVALUE="RDValue";
    private static final String VLANID="VLANID";
    private static final String vlanId="vlanId";
    private static final String VRFNAME="VRFName";
    private static final String VSIID="VSIID";
    private static final String VSINAME="VSIName";
    private static final String WANIP="WANIP";
    private static final String WANIPV6="WANIPV6";
    private static final String BILLENTITYNAME="billentityname";
    private static final String ADDPARAM1="addparam1";
    private static final String ADDPARAM2="addparam2";
    private static final String ADDPARAM3="addparam3";
    private static final String ADDPARAM4="addparam4";
    private static final String PURCHASEORDER="purchaseorder";
    private static final String REMARKS="remarks";
    private static final String OLDPASSWORD1="oldpassword1";
    private static final String OLDPASSWORD2="oldpassword2";
    private static final String OLDPASSWORD3="oldpassword3";
    private static final String SELFCAREPWD="selfcarepwd";
    private static final String ALLOWEDIPADDRESS="allowedIPAddress";
    private static final String PARENTCUSTOMERSID="parentCustomersId";
    private static final String OLDWANIP="OldWANIP";
    private static final String ISDELETED = "isDeleted";
    private static final String OLDLLACCOUNTID="OldLLAccountid";
    private static final String FIRSTACTIVATIONDATE="firstActivationDate";
    private static final String ACTIVATIONBYNAME="activationByName";
    private static final String OTP="otp";
    private static final String OTPVALIDATE="otpvalidate";
    private static final String LATITUDE="latitude";
    private static final String LONGITUDE="longitude";
    private static final String URL="url";
    private static final String GIS_CODE="gis_code";
    private static final String SALESREMARK="salesremark";
    private static final String SERVICETYPE="servicetype";
    private static final String PREVIOUSCAFAPPROVER="previousCafApprover";
    private static final String NEXTCAFAPPROVER="nextCafApprover";
    private static final String CAFAPPROVESTATUS="cafApproveStatus";
    private static final String BILLRUNCUSTPACKAGERELID="billRunCustPackageRelId";
    private static final String MVNOID="mvnoId";
    private static final String FULLNAME="fullName";
    private static final String CREATED_BY_STAFF_ID="createdByStaffId";
    private static final String LAST_MODIFIED_BY_STAFF_ID="lastModifiedByStaffId";
    private static final String CREATED_BY_NAME="createdByName";
    private static final String UPDATED_BY_NAME="updatedByName";
    private static final String BUID="buId";
    private static final String BLOCK_NO = "blockNo";

    //Quota Details
    private static final String QUOTA_TYPE = "quotaType";
    private static final String TOTAL_QUOTA = "totalQuota";
    private static final String USED_QUOTA = "usedQuota";
    private static final String QUOTA_UNIT = "quotaUnit";
    private static final String TIME_TOTAL_QUOTA = "timeTotalQuota";
    private static final String TIME_QUOTA_USED = "timeQuotaUsed";
    private static final String USED_TIME_QUOTA = "usedTimeQuota";
    private static final String TIME_QUOTA_UNIT = "timeQuotaUnit";
    private static final String TOTAL_QUOTA_KB = "totalQuotaKB";
    private static final String USED_QUOTA_KB = "usedQuotaKB";
    private static final String USED_TIME_QUOTA_SEC = "usedTimeQuotaSec";
    private static final String TIME_USED_QUOTA_SEC = "timeUsedQuotaSec";
    private static final String TIME_TOTAL_QUOTA_SEC = "timeTotalQuotaSec";
    private static final String DID_TOTAL_QUOTA = "didtotalquota";
    private static final String DID_USED_QUOTA = "didusedquota";
    private static final String INTERCOM_TOTAL_QUOTA = "intercomtotalquota";
    private static final String INTERCOM_USED_QUOTA = "intercomusedquota";
    private static final String DID_QUOTA_UNIT = "didQuotaUnit";
    private static final String INTERCOM_QUOTA_UNIT = "intercomQuotaUnit";
    private static final String PLAN_NAME = "planName";
    private static final String PARENT_QUOTA_TYPE = "parentQuotaType";
    private static final String SKIP_QUOTA_UPDATE = "skipQuotaUpdate";
    private static final String USAGE_QUOTA_TYPE = "usageQuotaType";
    private static final String CUST_PLAN_MAPPPING = "custPlanMappping";
    private static final String CUST_PACKAGE_ID = "custpackageid";
    private static final String PLAN_ID = "planId";
    private static final String QUOTA_DTLS = "quotaDtls";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String CALENDAR_TYPE = "calendarType";
    private static final String CUST_IP_MAPPING_LIST = "custIpMappingList";
    private static final String MAXCONCURRENTSESSION = "maxconcurrentsession";


    //Mac Address
//    private static final String ID = "id";
    private static final String CUSTOMER = "customer";
    private static final String CUST_ID = "custid";
    private static final String MAC_ADDRESS = "macAddress";
    private static final String IS_DELETE = "isDelete";

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String EXPIRY_DATE = "expiryDate";

    private static final String PARENTCUSTID = "parentcustid";
    private static final String INVOICE_TYPE = "invoice_type";
    private static final String USER_NAME = "userName";
    private static final String IPV4 ="ipv4";
    private static final String IPV6 ="ipv6";
    private static final String VLAN ="vlan";
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String traceId;
    private String spanId;
    private String currentUser;
    private static final String FRAMED_IP_NETMASK ="framedIPNetmask";
    private static final String FRAMED_IPV6_PREFIX ="framedIPv6Prefix";
    private static final String GATEWAYIP ="gatewayIP";
    private static final String PRIMARY_DNS ="primaryDNS";
    private static final String PRIMARY_IPV6_DNS ="primaryIPv6DNS";
    private static final String SECONDORY_DNS ="secondaryDNS";
    private static final String SECONDARY_IPV6_DNS ="secondaryIPv6DNS";
    private Map<String, Object> customerData;

    private static final String nasPort = "nasPort";
    private static final String framedIp = "framedIp";

    private static final String framedIpBind = "framedIpBind";

    private static final String ipPoolNameBind = "ipPoolNameBind";

    private static final String RegistrationDate = "registrationDate";

    private static final String ISNOTIFICATIONENABLE = "isNotificationEnable";
    private static final String NAS_PORT_ID = "NASPORTID";
    private static final String NAS_IP_ADDRESS = "NASIPADDRESS";
    private static final String FRAMED_IPV6_ADDRESS = "FRAMEDIPV6ADDRESS";
    public static final String IS_CUSTOMER_CREATED = "isCustomerCreated";

    private static final String EARLY_BILL_DAYS=  "earlybilldays";
    private static final String EARLY_BILL_DAY=  "earlybillday";
    private static final String MAC_PEOVISION = "mac_provision";
    private static final String MAC_AUTH_ENABLE=  "mac_auth_enable";
    private static final String EARLY_BILL_DATE ="earlybilldate";
    private static final String MAC_RETENTION_UNIT ="macRetentionUnit";
    private static final String MAC_RETENTION_PERIOD ="macRetentionPeriod";
    private static final String DELEGATEDPREFIX ="delegatedprefix";
    private static final String FRAMEDROUTE ="framedroute";

    public CustomMessage(CustomersPojo customers) {
        Map<String, Object> map = new HashMap<>();
        if(customers.getParentCustomers()!=null) {
            Integer parentCustomerId=customers.getParentCustomers().getId();
            map.put(PARENTCUSTID, parentCustomerId);
        }
        map.put(INVOICE_TYPE,customers.getInvoiceType());
        map.put(ID, customers.getId());
        map.put(TITLE, customers.getTitle());
        map.put(USERNAME, customers.getUsername());
        map.put(RegistrationDate, customers.getRegistrationDate());
        map.put(PASSWORD, customers.getPassword());
        map.put(FIRSTNAME, customers.getFirstname());
        map.put(LASTNAME, customers.getLastname());
        map.put(CUSTNAME, customers.getCustname());
        map.put(CONTACTPERSON, customers.getContactperson());
        map.put(CAFNO, customers.getCafno());
        map.put(EMAIL, customers.getEmail());
        map.put(MACTELFLAG, customers.getMactelflag());
        map.put(MOBILE, customers.getMobile());
        map.put(COUNTRY_CODE,customers.getCountryCode());
        map.put(VOICESRVTYPE, customers.getVoicesrvtype());
        map.put(VOICEPROVISION, customers.getVoiceprovision());
        map.put(INTERCOMNO, customers.getIntercomno());
        map.put(INTERCOMGRP, customers.getIntercomgrp());
        map.put(ONLINERENEWALFLAG, customers.getOnlinerenewalflag());
        map.put(VOIPENABLEFLAG, customers.getVoipenableflag());
        map.put(CUSTCATEGORY, customers.getCustcategory());
        map.put(NETWORKTYPE, customers.getNetworktype());

        map.put(FRAMED_IP_NETMASK, customers.getFramedIPNetmask());
        map.put(FRAMED_IPV6_PREFIX, customers.getFramedIPv6Prefix());
        map.put(GATEWAYIP, customers.getGatewayIP());
        map.put(PRIMARY_DNS, customers.getPrimaryDNS());
        map.put(PRIMARY_IPV6_DNS, customers.getPrimaryIPv6DNS());
        map.put(SECONDORY_DNS, customers.getSecondaryDNS());
        map.put(SECONDARY_IPV6_DNS, customers.getSecondaryIPv6DNS());

        map.put(DEFAULTPOOLID, customers.getDefaultpoolid());
        map.put(SERVICEAREA, customers.getServiceareaid());
        map.put(STATUS, customers.getStatus());
//        map.put(INVOICEOPTION, customers.getInvoiceOption());
        map.put(FAILCOUNT, customers.getFailcount());
        if(customers.getLast_password_change() != null)
            map.put(LAST_PASSWORD_CHANGE, customers.getLast_password_change().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put(ACCTNO, customers.getAcctno());
        map.put(CUSTTYPE, customers.getCusttype());
        map.put(PHONE, customers.getPhone());
//        map.put(LASTSTATUSCHANGEDATE, customers.getLastStatusChangeDate());
        map.put(BILLDAY, customers.getBillday());
        map.put(PARTNER, customers.getPartnerid());
//        map.put(CUSTOMERPAYMENTS, customers.getCustomerPayments());
        if(customers.getNextBillDate() != null)
            map.put(NEXTBILLDATE, customers.getNextBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if(customers.getLastBillDate() != null)
            map.put(LASTBILLDATE, customers.getLastBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if(customers.getNextQuotaResetDate() != null)
            map.put(NEXTQUOTARESETDATE, customers.getNextQuotaResetDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.put(RADIUSPROFILES, customers.getRadiusprofileIds());
        map.put(PLANMAPPINGLIST, customers.getPlanMappingList());
        map.put(NEWPASSWORD, customers.getNewpassword());
        map.put(OLDBNGROUTERINTERFACE, customers.getOldBNGRouterinterface());
        map.put(OLDVSINAME, customers.getOldVSIName());
        map.put(BNGROUTERINTERFACE, customers.getBNGRouterinterface());
        map.put(BNGROUTERNAME, customers.getBNGRoutername());
        map.put(IPPREFIXES, customers.getIPPrefixes());
        map.put(IPV6PREFIXES, customers.getIPV6Prefixes());
        map.put(LANIP, customers.getLANIP());
        map.put(LANIPV6, customers.getLANIPV6());
        map.put(LLACCOUNTID, customers.getLLAccountid());
        map.put(LLCONNECTIONTYPE, customers.getLLConnectiontype());
        map.put(LLEXPIRYDATE, customers.getLLExpirydate());
        map.put(LLMEDIUM, customers.getLLMedium());
        map.put(LLSERVICEID, customers.getLLServiceid());
        map.put(MACADDRESS, customers.getMACADDRESS());
        map.put(PEERIP, customers.getPeerip());
        map.put(POOLIP, customers.getPOOLIP());
        map.put(QOS, customers.getQOS());
        map.put(RDEXPORT, customers.getRDExport());
        map.put(RDVALUE, customers.getRDValue());
        map.put(VLANID, customers.getVlan_id());
        map.put(vlanId, customers.getVlan_id());
        map.put(VRFNAME, customers.getVRFName());
        map.put(VSIID, customers.getVSIID());
        map.put(VSINAME, customers.getVSIName());
        map.put(WANIP, customers.getWANIP());
        map.put(WANIPV6, customers.getWANIPV6());
        map.put(BILLENTITYNAME, customers.getBillentityname());
        map.put(ADDPARAM1, customers.getAddparam1());
        map.put(ADDPARAM2, customers.getAddparam2());
        map.put(ADDPARAM3, customers.getAddparam3());
        map.put(ADDPARAM4, customers.getAddparam4());
        map.put(PURCHASEORDER, customers.getPurchaseorder());
        map.put(REMARKS, customers.getRemarks());
        map.put(OLDPASSWORD1, customers.getOldpassword1());
        map.put(OLDPASSWORD2, customers.getOldpassword2());
        map.put(OLDPASSWORD3, customers.getOldpassword3());
        map.put(SELFCAREPWD, customers.getSelfcarepwd());
        map.put(ALLOWEDIPADDRESS, customers.getAllowedIPAddress());
        map.put(OLDWANIP, customers.getOldWANIP());
        map.put(ISDELETED, customers.getIsDeleted());
        map.put(OLDLLACCOUNTID, customers.getOldLLAccountid());
        if(customers.getFirstActivationDate() != null)
            map.put(FIRSTACTIVATIONDATE, customers.getFirstActivationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        map.put(OTP, customers.getOtp());
//        map.put(OTPVALIDATE, customers.getOtpvalidate());
        map.put(ACTIVATIONBYNAME,customers.getActivationByName());
        map.put(MAXCONCURRENTSESSION,customers.getMaxconcurrentsession());
        map.put(LATITUDE, customers.getLatitude());
        map.put(LONGITUDE, customers.getLongitude());
        map.put(URL, customers.getUrl());
        map.put(GIS_CODE, customers.getGis_code());
        map.put(SALESREMARK, customers.getSalesremark());
        map.put(SERVICETYPE, customers.getServicetype());
        map.put(PREVIOUSCAFAPPROVER, customers.getNextTeamHierarchyMapping());
        map.put(NEXTCAFAPPROVER, customers.getNextTeamHierarchyMapping());
        map.put(CAFAPPROVESTATUS, customers.getCafApproveStatus());
        map.put(MVNOID, customers.getMvnoId());
        if (customers.getBuId() != null) {
            map.put(BUID, customers.getBuId());
        }
        map.put(CALENDAR_TYPE, customers.getCalendarType());
        map.put(nasPort, customers.getNasPort());
        map.put(nasPort, customers.getNasPort());
        map.put(framedIp, customers.getFramedIp());
        map.put(framedIpBind, customers.getFramedIpBind());
        map.put(ipPoolNameBind, customers.getIpPoolNameBind());
        map.put(ISNOTIFICATIONENABLE , customers.getIsNotificationEnable());
        map.put(IPV4 , customers.getIpv4());
        map.put(IPV6 , customers.getIpv6());
        map.put(VLAN , customers.getVlan());
        map.put(NAS_IP_ADDRESS , customers.getNasIpAddress());
        map.put(NAS_PORT_ID , customers.getNasPortId());
        map.put(FRAMED_IPV6_ADDRESS , customers.getFramedIpv6Address());
        map.put(EARLY_BILL_DATE,customers.getEarlybilldate());
        map.put(EARLY_BILL_DAYS,customers.getEarlybilldays());
        map.put(EARLY_BILL_DAY,customers.getEarlybillday());
        map.put(MAC_PEOVISION,customers.getMac_provision());
        map.put(MAC_AUTH_ENABLE,customers.getMac_auth_enable());
        map.put(MAC_RETENTION_UNIT,customers.getMacRetentionUnit());
        map.put(MAC_RETENTION_PERIOD,customers.getMacRetentionPeriod());
        map.put(DELEGATEDPREFIX,customers.getDelegatedprefix());
        map.put(FRAMEDROUTE,customers.getFramedroute());
        map.put(BLOCK_NO, customers.getBlockNo());
        map.put(ACCTNO, customers.getAcctno());
        if(Objects.nonNull(customers.getBuId())){
            map.put(RabbitMqConstants.BU_ID,customers.getBuId());
        }
        else{
            map.put(RabbitMqConstants.BU_ID,null);
        }
        map.put(IS_CUSTOMER_CREATED, customers.isCustomerCreated());
        List<Map> custQuotaDtlsPojoList = new ArrayList<>();
        for(CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()){
            if(custPlanMapppingPojo.getStartDate() != null)
                map.put(START_DATE, custPlanMapppingPojo.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if(custPlanMapppingPojo.getEndDate() != null)
                map.put(END_DATE, custPlanMapppingPojo.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if(custPlanMapppingPojo.getExpiryDate() != null)
                map.put(EXPIRY_DATE, custPlanMapppingPojo.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            for(CustQuotaDtlsPojo custQuotaDtlsPojo : custPlanMapppingPojo.getQuotaList()){
                Map<String, Object> quotaMap = new HashMap<>();
                quotaMap.put(ID, custQuotaDtlsPojo.getId());
                quotaMap.put(PLAN_ID, custQuotaDtlsPojo.getPlanId());
                quotaMap.put(QUOTA_TYPE, custQuotaDtlsPojo.getQuotaType());
                quotaMap.put(TOTAL_QUOTA, custQuotaDtlsPojo.getTotalQuota());
                quotaMap.put(USED_QUOTA, custQuotaDtlsPojo.getUsedQuota());
                quotaMap.put(QUOTA_UNIT, custQuotaDtlsPojo.getQuotaUnit());
                quotaMap.put(TIME_TOTAL_QUOTA, custQuotaDtlsPojo.getTimeTotalQuota());
                quotaMap.put(TIME_QUOTA_USED, custQuotaDtlsPojo.getTimeQuotaUsed());
                quotaMap.put(TIME_QUOTA_UNIT, custQuotaDtlsPojo.getTimeQuotaUnit());
                quotaMap.put(IS_DELETE, custQuotaDtlsPojo.getIsDelete());
                quotaMap.put(TOTAL_QUOTA_KB, custQuotaDtlsPojo.getTotalQuotaKB());
                quotaMap.put(USED_QUOTA_KB, custQuotaDtlsPojo.getUsedQuotaKB());
                quotaMap.put(TIME_TOTAL_QUOTA_SEC, custQuotaDtlsPojo.getTimeTotalQuotaSec());
                quotaMap.put(TIME_USED_QUOTA_SEC, custQuotaDtlsPojo.getTimeUsedQuotaSec());
                quotaMap.put(DID_TOTAL_QUOTA, custQuotaDtlsPojo.getDidtotalquota());
                quotaMap.put(DID_USED_QUOTA, custQuotaDtlsPojo.getDidusedquota());
                quotaMap.put(INTERCOM_TOTAL_QUOTA, custQuotaDtlsPojo.getIntercomtotalquota());
                quotaMap.put(INTERCOM_USED_QUOTA, custQuotaDtlsPojo.getIntercomusedquota());
                quotaMap.put(DID_QUOTA_UNIT, custQuotaDtlsPojo.getDidQuotaUnit());
                quotaMap.put(INTERCOM_QUOTA_UNIT, custQuotaDtlsPojo.getIntercomQuotaUnit());
                quotaMap.put(PLAN_NAME, custQuotaDtlsPojo.getPlanName());
//                custQuotaDtlsPojo.getCustPlanMappping().setStartDateString(custPlanMapppingPojo.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                custQuotaDtlsPojo.getCustPlanMappping().setEndDateString(custPlanMapppingPojo.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                custQuotaDtlsPojo.getCustPlanMappping().setExpiryDateString(custPlanMapppingPojo.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                quotaMap.put(CUST_PLAN_MAPPPING, custQuotaDtlsPojo.getCustPlanMappping());
                quotaMap.put(CUST_PACKAGE_ID, custQuotaDtlsPojo.getCustPlanMappping() != null ? custQuotaDtlsPojo.getCustPlanMappping().getId() : null);
                quotaMap.put(CUSTOMER, custQuotaDtlsPojo.getCustomer() != null ? custQuotaDtlsPojo.getCustomer().getId() : null);
                quotaMap.put(CREATED_BY_STAFF_ID, custQuotaDtlsPojo.getCreatedById());
                quotaMap.put(LAST_MODIFIED_BY_STAFF_ID, custQuotaDtlsPojo.getLastModifiedById());
                quotaMap.put(CREATED_BY_NAME, custQuotaDtlsPojo.getCreatedByName());
                quotaMap.put(UPDATED_BY_NAME, custQuotaDtlsPojo.getLastModifiedByName());
                quotaMap.put(USAGE_QUOTA_TYPE, custQuotaDtlsPojo.getUsageQuotaType());
                quotaMap.put(PARENT_QUOTA_TYPE, custQuotaDtlsPojo.getParentQuotaType());
                quotaMap.put(SKIP_QUOTA_UPDATE, custQuotaDtlsPojo.getSkipQuotaUpdate());
                custQuotaDtlsPojoList.add(quotaMap);
            }
            map.put(QUOTA_DTLS, custQuotaDtlsPojoList);
        }

        List<Map> custMacAddresses = new ArrayList<>();
        for(CustMacMapppingPojo custMacMapppingPojo : customers.getCustMacMapppingList().stream().filter(m -> !m.getIsDeleted()).collect(Collectors.toList())){
            Map<String, Object> macAddressMap = new HashMap<>();
            macAddressMap.put(ID, custMacMapppingPojo.getId());
            macAddressMap.put(MAC_ADDRESS, custMacMapppingPojo.getMacAddress());
            macAddressMap.put(CUSTOMER, custMacMapppingPojo.getCustomer());
            macAddressMap.put(CUST_ID, custMacMapppingPojo.getCustid());
            macAddressMap.put(CREATED_BY_STAFF_ID, custMacMapppingPojo.getCreatedById());
            macAddressMap.put(LAST_MODIFIED_BY_STAFF_ID, custMacMapppingPojo.getLastModifiedById());
            macAddressMap.put(IS_DELETE, custMacMapppingPojo.getIsDeleted());
            custMacAddresses.add(macAddressMap);
        }
        List<Map> custLocationsMap = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customers.getCustomerLocations())) {
            for (CustomerLocationMappingDto locationMappingDto: customers.getCustomerLocations()) {
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("id",locationMappingDto.getId());
                locationMap.put("custId",locationMappingDto.getCustId());
                locationMap.put("locationId",locationMappingDto.getLocationId());
                locationMap.put("locationName",locationMappingDto.getLocationName());
                locationMap.put("isDelete",locationMappingDto.getIsDelete());
                locationMap.put("isActive",locationMappingDto.getIsActive());
                locationMap.put("isParentLocation",locationMappingDto.getIsParentLocation());
                locationMap.put("mac",locationMappingDto.getMac());
                locationMap.put("mvnoId",locationMappingDto.getMvnoId());
                custLocationsMap.add(locationMap);
            }
        }
        map.put(CUSTMACMAPPPINGLIST, custMacAddresses);
        map.put(CUSTLOCATIONMAPPPINGLIST, custLocationsMap);

        List<Map> custIpMaping = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customers.getCustIpMappingList())) {
            for (CustIpMapping custIpMapping: customers.getCustIpMappingList()) {
                Map<String, Object> custIpMappings = new HashMap<>();
                custIpMappings.put("id",custIpMapping.getId());
                custIpMappings.put("custId",custIpMapping.getCustid());
                custIpMappings.put("ipAddress",custIpMapping.getIpAddress());
                custIpMappings.put("ipType",custIpMapping.getIpType());
                custIpMaping.add(custIpMappings);
            }
        }
        map.put(CUST_IP_MAPPING_LIST, custIpMaping);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "New customer created from Api Gateway";
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }

    public CustomMessage(Customers customers) {
        Map<String, Object> map = new HashMap<>();
        if(customers.getParentCustomers()!=null) {
            Integer parentCustomerId=customers.getParentCustomers().getId();
            map.put(PARENTCUSTID, parentCustomerId);
        }
        map.put(INVOICE_TYPE,customers.getInvoiceType());
        map.put(ID, customers.getId());
        map.put(TITLE, customers.getTitle());
        map.put(USERNAME, customers.getUsername());
        map.put(RegistrationDate, customers.getRegistrationDate());
        map.put(PASSWORD, customers.getPassword());
        map.put(FIRSTNAME, customers.getFirstname());
        map.put(LASTNAME, customers.getLastname());
        map.put(CUSTNAME, customers.getCustname());
        map.put(CONTACTPERSON, customers.getContactperson());
        map.put(CAFNO, customers.getCafno());
        map.put(EMAIL, customers.getEmail());
        map.put(MACTELFLAG, customers.getMactelflag());
        map.put(MOBILE, customers.getMobile());
        map.put(COUNTRY_CODE,customers.getCountryCode());
        map.put(VOICESRVTYPE, customers.getVoicesrvtype());
        map.put(VOICEPROVISION, customers.getVoiceprovision());
        map.put(INTERCOMNO, customers.getIntercomno());
        map.put(INTERCOMGRP, customers.getIntercomgrp());
        map.put(ONLINERENEWALFLAG, customers.getOnlinerenewalflag());
        map.put(VOIPENABLEFLAG, customers.getVoipenableflag());
        map.put(CUSTCATEGORY, customers.getCustcategory());
        map.put(NETWORKTYPE, customers.getNetworktype());

        map.put(FRAMED_IP_NETMASK, customers.getFramedIPNetmask());
        map.put(FRAMED_IPV6_PREFIX, customers.getFramedIPv6Prefix());
        map.put(GATEWAYIP, customers.getGatewayIP());
        map.put(PRIMARY_DNS, customers.getPrimaryDNS());
        map.put(PRIMARY_IPV6_DNS, customers.getPrimaryIPv6DNS());
        map.put(SECONDORY_DNS, customers.getSecondaryDNS());
        map.put(SECONDARY_IPV6_DNS, customers.getSecondaryIPv6DNS());

        map.put(DEFAULTPOOLID, customers.getDefaultpoolid());
        map.put(STATUS, customers.getStatus());
//        map.put(INVOICEOPTION, customers.getInvoiceOption());
        map.put(FAILCOUNT, customers.getFailcount());
        if(customers.getLast_password_change() != null)
            map.put(LAST_PASSWORD_CHANGE, customers.getLast_password_change().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put(ACCTNO, customers.getAcctno());
        map.put(CUSTTYPE, customers.getCusttype());
        map.put(PHONE, customers.getPhone());
//        map.put(LASTSTATUSCHANGEDATE, customers.getLastStatusChangeDate());
        map.put(BILLDAY, customers.getBillday());
        map.put(PARTNER, customers.getPartner().getId());
//        map.put(CUSTOMERPAYMENTS, customers.getCustomerPayments());
        if(customers.getNextBillDate() != null)
            map.put(NEXTBILLDATE, customers.getNextBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if(customers.getLastBillDate() != null)
            map.put(LASTBILLDATE, customers.getLastBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if(customers.getNextQuotaResetDate() != null)
            map.put(NEXTQUOTARESETDATE, customers.getNextQuotaResetDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.put(PLANMAPPINGLIST, customers.getPlanMappingList());
        map.put(NEWPASSWORD, customers.getNewpassword());
        map.put(OLDBNGROUTERINTERFACE, customers.getOldBNGRouterinterface());
        map.put(OLDVSINAME, customers.getOldVSIName());
        map.put(BNGROUTERINTERFACE, customers.getBNGRouterinterface());
        map.put(BNGROUTERNAME, customers.getBNGRoutername());
        map.put(IPPREFIXES, customers.getIPPrefixes());
        map.put(IPV6PREFIXES, customers.getIPV6Prefixes());
        map.put(LANIP, customers.getLANIP());
        map.put(LANIPV6, customers.getLANIPV6());
        map.put(LLACCOUNTID, customers.getLLAccountid());
        map.put(LLCONNECTIONTYPE, customers.getLLConnectiontype());
        map.put(LLEXPIRYDATE, customers.getLLExpirydate());
        map.put(LLMEDIUM, customers.getLLMedium());
        map.put(LLSERVICEID, customers.getLLServiceid());
        map.put(MACADDRESS, customers.getMACADDRESS());
        map.put(PEERIP, customers.getPeerip());
        map.put(POOLIP, customers.getPOOLIP());
        map.put(QOS, customers.getQOS());
        map.put(RDEXPORT, customers.getRDExport());
        map.put(RDVALUE, customers.getRDValue());
        map.put(VLANID, customers.getVlan_id());
        map.put(vlanId, customers.getVlan_id());
        map.put(VRFNAME, customers.getVRFName());
        map.put(VSIID, customers.getVSIID());
        map.put(VSINAME, customers.getVSIName());
        map.put(WANIP, customers.getWANIP());
        map.put(WANIPV6, customers.getWANIPV6());
        map.put(BILLENTITYNAME, customers.getBillentityname());
        map.put(ADDPARAM1, customers.getAddparam1());
        map.put(ADDPARAM2, customers.getAddparam2());
        map.put(ADDPARAM3, customers.getAddparam3());
        map.put(ADDPARAM4, customers.getAddparam4());
        map.put(PURCHASEORDER, customers.getPurchaseorder());
        map.put(REMARKS, customers.getRemarks());
        map.put(OLDPASSWORD1, customers.getOldpassword1());
        map.put(OLDPASSWORD2, customers.getOldpassword2());
        map.put(OLDPASSWORD3, customers.getOldpassword3());
        map.put(SELFCAREPWD, customers.getSelfcarepwd());
        map.put(ALLOWEDIPADDRESS, customers.getAllowedIPAddress());
        map.put(OLDWANIP, customers.getOldWANIP());
        map.put(ISDELETED, customers.getIsDeleted());
        map.put(OLDLLACCOUNTID, customers.getOldLLAccountid());
        if(customers.getFirstActivationDate() != null)
            map.put(FIRSTACTIVATIONDATE, customers.getFirstActivationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        map.put(OTP, customers.getOtp());
//        map.put(OTPVALIDATE, customers.getOtpvalidate());
        map.put(ACTIVATIONBYNAME,customers.getActivationByName());
        map.put(MAXCONCURRENTSESSION,customers.getMaxconcurrentsession());
        map.put(LATITUDE, customers.getLatitude());
        map.put(LONGITUDE, customers.getLongitude());
        map.put(URL, customers.getUrl());
        map.put(GIS_CODE, customers.getGis_code());
        map.put(SALESREMARK, customers.getSalesremark());
        map.put(SERVICETYPE, customers.getServicetype());
        map.put(PREVIOUSCAFAPPROVER, customers.getNextTeamHierarchyMapping());
        map.put(NEXTCAFAPPROVER, customers.getNextTeamHierarchyMapping());
        map.put(CAFAPPROVESTATUS, customers.getCafApproveStatus());
        map.put(MVNOID, customers.getMvnoId());
        if (customers.getBuId() != null) {
            map.put(BUID, customers.getBuId());
        }
        map.put(CALENDAR_TYPE, customers.getCalendarType());
        map.put(nasPort, customers.getNasPort());
        map.put(nasPort, customers.getNasPort());
        map.put(framedIp, customers.getFramedIp());
        map.put(framedIpBind, customers.getFramedIpBind());
        map.put(ipPoolNameBind, customers.getIpPoolNameBind());
        map.put(ISNOTIFICATIONENABLE , customers.getIsNotificationEnable());
        map.put(IPV4 , customers.getIpv4());
        map.put(IPV6 , customers.getIpv6());
        map.put(VLAN , customers.getVlan());
        map.put(NAS_IP_ADDRESS , customers.getNasIpAddress());
        map.put(NAS_PORT_ID , customers.getNasPortId());
        map.put(FRAMED_IPV6_ADDRESS , customers.getFramedIpv6Address());
        map.put(EARLY_BILL_DATE,customers.getEarlybilldate());
        map.put(EARLY_BILL_DAYS,customers.getEarlybilldays());
        map.put(EARLY_BILL_DAY,customers.getEarlybillday());
        map.put(MAC_PEOVISION,customers.getMac_provision());
        map.put(MAC_AUTH_ENABLE,customers.getMac_auth_enable());
        map.put(MAC_RETENTION_UNIT,customers.getMacRetentionUnit());
        map.put(MAC_RETENTION_PERIOD,customers.getMacRetentionPeriod());
        map.put(DELEGATEDPREFIX,customers.getDelegatedprefix());
        map.put(FRAMEDROUTE,customers.getFramedroute());
        map.put(BLOCK_NO, customers.getBlockNo());
        map.put(ACCTNO, customers.getAcctno());
        if(Objects.nonNull(customers.getBuId())){
            map.put(RabbitMqConstants.BU_ID,customers.getBuId());
        }
        else{
            map.put(RabbitMqConstants.BU_ID,null);
        }
        map.put(IS_CUSTOMER_CREATED, true);
        List<Map> custQuotaDtlsPojoList = new ArrayList<>();
        for(CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()){
            if(custPlanMapppingPojo.getStartDate() != null)
                map.put(START_DATE, custPlanMapppingPojo.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if(custPlanMapppingPojo.getEndDate() != null)
                map.put(END_DATE, custPlanMapppingPojo.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            if(custPlanMapppingPojo.getExpiryDate() != null)
                map.put(EXPIRY_DATE, custPlanMapppingPojo.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            for(CustQuotaDetails custQuotaDtlsPojo : custPlanMapppingPojo.getQuotaList()){
                Map<String, Object> quotaMap = new HashMap<>();
                quotaMap.put(ID, custQuotaDtlsPojo.getId());
                quotaMap.put(PLAN_ID, custQuotaDtlsPojo.getPostpaidPlan().getId());
                quotaMap.put(QUOTA_TYPE, custQuotaDtlsPojo.getQuotaType());
                quotaMap.put(TOTAL_QUOTA, custQuotaDtlsPojo.getTotalQuota());
                quotaMap.put(USED_QUOTA, custQuotaDtlsPojo.getUsedQuota());
                quotaMap.put(QUOTA_UNIT, custQuotaDtlsPojo.getQuotaUnit());
                quotaMap.put(TIME_TOTAL_QUOTA, custQuotaDtlsPojo.getTimeTotalQuota());
                quotaMap.put(TIME_QUOTA_USED, custQuotaDtlsPojo.getTimeQuotaUsed());
                quotaMap.put(TIME_QUOTA_UNIT, custQuotaDtlsPojo.getTimeQuotaUnit());
                quotaMap.put(IS_DELETE, custQuotaDtlsPojo.getIsDelete());
                quotaMap.put(TOTAL_QUOTA_KB, custQuotaDtlsPojo.getTotalQuotaKB());
                quotaMap.put(USED_QUOTA_KB, custQuotaDtlsPojo.getUsedQuotaKB());
                quotaMap.put(TIME_TOTAL_QUOTA_SEC, custQuotaDtlsPojo.getTimeTotalQuotaSec());
                quotaMap.put(TIME_USED_QUOTA_SEC, custQuotaDtlsPojo.getTimeUsedQuotaSec());
                quotaMap.put(DID_TOTAL_QUOTA, custQuotaDtlsPojo.getDidtotalquota());
                quotaMap.put(DID_USED_QUOTA, custQuotaDtlsPojo.getDidusedquota());
                quotaMap.put(INTERCOM_TOTAL_QUOTA, custQuotaDtlsPojo.getIntercomtotalquota());
                quotaMap.put(INTERCOM_USED_QUOTA, custQuotaDtlsPojo.getIntercomusedquota());
                quotaMap.put(DID_QUOTA_UNIT, custQuotaDtlsPojo.getDidQuotaUnit());
                quotaMap.put(INTERCOM_QUOTA_UNIT, custQuotaDtlsPojo.getIntercomQuotaUnit());
                quotaMap.put(PLAN_NAME, custQuotaDtlsPojo.getPostpaidPlan().getName());
                quotaMap.put(CUST_PLAN_MAPPPING, custQuotaDtlsPojo.getCustPlanMappping());
                quotaMap.put(CUST_PACKAGE_ID, custQuotaDtlsPojo.getCustPlanMappping() != null ? custQuotaDtlsPojo.getCustPlanMappping().getId() : null);
                quotaMap.put(CUSTOMER, custQuotaDtlsPojo.getCustomer() != null ? custQuotaDtlsPojo.getCustomer().getId() : null);
                quotaMap.put(CREATED_BY_STAFF_ID, custQuotaDtlsPojo.getCreatedById());
                quotaMap.put(LAST_MODIFIED_BY_STAFF_ID, custQuotaDtlsPojo.getLastModifiedById());
                quotaMap.put(CREATED_BY_NAME, custQuotaDtlsPojo.getCreatedByName());
                quotaMap.put(UPDATED_BY_NAME, custQuotaDtlsPojo.getLastModifiedByName());
                quotaMap.put(USAGE_QUOTA_TYPE, custQuotaDtlsPojo.getUsageQuotaType());
                quotaMap.put(PARENT_QUOTA_TYPE, custQuotaDtlsPojo.getParentQuotaType());
                quotaMap.put(SKIP_QUOTA_UPDATE, custQuotaDtlsPojo.getSkipQuotaUpdate());
                custQuotaDtlsPojoList.add(quotaMap);
            }
            map.put(QUOTA_DTLS, custQuotaDtlsPojoList);
        }

        List<Map> custMacAddresses = new ArrayList<>();
        for(CustMacMappping custMacMapppingPojo : customers.getCustMacMapppingList().stream().filter(m -> !m.getIsDeleted()).collect(Collectors.toList())){
            Map<String, Object> macAddressMap = new HashMap<>();
            macAddressMap.put(ID, custMacMapppingPojo.getId());
            macAddressMap.put(MAC_ADDRESS, custMacMapppingPojo.getMacAddress());
            macAddressMap.put(CUSTOMER, custMacMapppingPojo.getCustomer());
            macAddressMap.put(CUST_ID, custMacMapppingPojo.getCustomer().getId());
            macAddressMap.put(CREATED_BY_STAFF_ID, custMacMapppingPojo.getCreatedById());
            macAddressMap.put(LAST_MODIFIED_BY_STAFF_ID, custMacMapppingPojo.getLastModifiedById());
            macAddressMap.put(IS_DELETE, custMacMapppingPojo.getIsDeleted());
            custMacAddresses.add(macAddressMap);
        }
        List<Map> custLocationsMap = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customers.getCustomerLocations())) {
            for (CustomerLocationMapping locationMappingDto: customers.getCustomerLocations()) {
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("id",locationMappingDto.getId());
                locationMap.put("custId",locationMappingDto.getCustId());
                locationMap.put("locationId",locationMappingDto.getLocationId());
                locationMap.put("locationName",locationMappingDto.getLocationName());
                locationMap.put("isDelete",locationMappingDto.getIsDelete());
                locationMap.put("isActive",locationMappingDto.getIsActive());
                locationMap.put("isParentLocation",locationMappingDto.getIsParentLocation());
                locationMap.put("mac",locationMappingDto.getMac());
                locationMap.put("mvnoId",locationMappingDto.getMvnoId());
                custLocationsMap.add(locationMap);
            }
        }
        map.put(CUSTMACMAPPPINGLIST, custMacAddresses);
        map.put(CUSTLOCATIONMAPPPINGLIST, custLocationsMap);

        List<Map> custIpMaping = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customers.getCustIpMappingList())) {
            for (CustIpMapping custIpMapping: customers.getCustIpMappingList()) {
                Map<String, Object> custIpMappings = new HashMap<>();
                custIpMappings.put("id",custIpMapping.getId());
                custIpMappings.put("custId",custIpMapping.getCustid());
                custIpMappings.put("ipAddress",custIpMapping.getIpAddress());
                custIpMappings.put("ipType",custIpMapping.getIpType());
                custIpMaping.add(custIpMappings);
            }
        }
        map.put(CUST_IP_MAPPING_LIST, custIpMaping);
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "New customer created from Api Gateway";
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;
    }

    public CustomMessage(UpdatePasswordResetDto updatePasswordResetDto , String spanId , String traceId){
        Map<String, Object> map = new HashMap<>();
        map.put(ID , updatePasswordResetDto.getId());
        map.put(MVNOID , updatePasswordResetDto.getMvnoId());
        map.put(PASSWORD, updatePasswordResetDto.getPassword());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Password updated from BSS";
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public CustomMessage(Integer mvnoId , Integer custRefId ){
        Map<String, Object> map = new HashMap<>();
        map.put(CUST_ID , custRefId);
        map.put(MVNOID , mvnoId);
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer REF updated for mvno BSS";
        this.customerData = map;
        this.sourceName = ADOPT_API_GATEWAY;

    }
}

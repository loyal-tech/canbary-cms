package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateCustomerShareDataMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private String email;
    private String mobile;
    private String countryCode;
    private Integer serviceAreaId;
    private Integer networkdevicesId;
    private String status;
    private String custtype;
    private String phone;
    private Integer mvnoId;
    private Long buId;
    private Integer lcoId;
    private Boolean is_from_pwc;
    private Boolean isDeleted;
    private Long oltslotid;
    private Long oltportid;
    private String fullName;
    private Integer parnterId;
    private String planPurchaseType;
    private String serviceAreaName;
    private String partnerName;
    private String calendarType;
    private String dunningCategory;
    private String parentCustUsername;
    private Integer parentCustId;
    private String feasibilityRequired;
    private Long popId;
    private Long oltId;
    private Long masterdbid;
    private Long splitterid;
    private String framedIp;
    private String framedIpBind;
    private String ipPoolNameBind;
    private String nasPort;
    private String valleyType;
    private String customerArea;
    private String custcategory;
    private String contactperson;
    private Integer createdById;
    private Integer lastModifiedById;
    private String serialNumber;
    private Integer serviceId;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
    private  String nextbilldate;
    private List<CustomerAddress> address;
    private  List<CustChargeDetails> indicustChargeDetails;
    private  List<CustChargeDetails> overChargeList;
    private List<DebitDocument> debitDocument;
    private CustomerLedger customerLedgerList;
    private List<CustomerLedgerDtls> customerLedgerDtlsList;
    private  List<CustomerChargeHistory> customerChargeHistories;
    private  List<CustomerDocDetails> customerDocDetails;
    private Boolean istrialplan;
    private String framedIpv6Address;
    private Integer maxconcurrentsession;
    private String delegatedprefix;
    private String nasPortId;
    private Boolean mac_provision;
    private Boolean mac_auth_enable;
    private Integer  macRetentionPeriod;
    private String  macRetentionUnit;
    private String  secondaryDNS;
    private String framedIPNetmask;
    private String framedIPv6Prefix;
    private String  primaryDNS;
    private String  primaryIPv6DNS;
    private String  secondaryIPv6DNS;
    private String blockNo;
    private String vlanId;
    private String gatewayIP;
    private String framedroute;
    private String accountNo;
    private String pan;

    private String customerVrn;

    private String customerNid;

    private Integer renewPlanLimit;

    private String passportNo;

    private String drivingLicence;
    private Integer billday;
    private String quotaResetDate;
    private boolean billDayUpdated;
    private Integer previousBillday;
    private String onuInterface;

    public UpdateCustomerShareDataMessage(Customers customers, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList) {
        id = customers.getId();
        title = customers.getTitle();
        if(customers.getNextBillDate()!=null)
            nextbilldate=customers.getNextBillDate().toString();
        else
            nextbilldate=null;
        customerDocDetails=customers.getCustDocList();
        customerLedgerDtlsList=customers.getLedgerDtls();
        istrialplan = customers.getIstrialplan();
       // customerChargeHistories=customers.getCustomerChargeHistories();
        customerLedgerList=customers.getCustLeger();
        address=customers.getAddressList();
        username = customers.getUsername();
        password = customers.getPassword();
        firstname = customers.getFirstname();
        lastname = customers.getLastname();
        custname = customers.getCustname();
        contactperson = customers.getContactperson();
        email = customers.getEmail();
        mobile = customers.getMobile();
        this.planPurchaseType=customers.getPlanPurchaseType();
        debitDocument=customers.getDebitDocList();
        countryCode = customers.getCountryCode();
        serviceAreaId = customers.getServicearea() != null ? Math.toIntExact(customers.getServicearea().getId()) : null;
        networkdevicesId = customers.getNetworkdevices() != null ? Math.toIntExact(customers.getNetworkdevices().getId()) : null;
        status = customers.getStatus();
        custtype = customers.getCusttype();
        phone = customers.getPhone();
        framedIpBind = customers.getFramedIpBind();
        mvnoId = customers.getMvnoId();
        buId = customers.getBuId();
        lcoId = customers.getLcoId();
        is_from_pwc = customers.getIs_from_pwc();
        isDeleted = customers.getIsDeleted();
        oltportid = customers.getOltportid();
        popId = customers.getPopid() != null ? customers.getPopid() : 0;
        masterdbid = customers.getMasterdbid();
        splitterid = customers.getSplitterid();
        oltId = customers.getOltid();
        framedIp = customers.getFramedIp();
        ipPoolNameBind = customers.getIpPoolNameBind();
        nasPort = customers.getNasPort();
        oltslotid = customers.getOltslotid();
        fullName = customers.getFullName();
        indicustChargeDetails=customers.getIndiChargeList();
        overChargeList= customers.getOverChargeList();
        parnterId = customers.getPartner() != null ? customers.getPartner().getId() : null;
        calendarType = customers.getCalendarType();
        dunningCategory = customers.getDunningCategory();
        parentCustUsername = customers.getParentCustomers() != null ? customers.getParentCustomers().getUsername() : null;
        parentCustId = customers.getParentCustomers() != null ? customers.getParentCustomers().getId() : null;
        feasibilityRequired = customers.getFeasibilityRequired();
        valleyType = customers.getValleyType();
        customerArea = customers.getCustomerArea();
        custcategory = customers.getDunningCategory();
        createdById = customers.getCreatedById();
        lastModifiedById = customers.getLastModifiedById();
        popId = customers.getPopid() != null ? customers.getPopid() : 0;
        masterdbid = customers.getMasterdbid();
        splitterid = customers.getSplitterid();
        oltId = customers.getOltid();
        framedIp = customers.getFramedIp();
        ipPoolNameBind = customers.getIpPoolNameBind();
        nasPort = customers.getNasPort();
        framedIpBind = customers.getFramedIpBind();
        framedIpv6Address = customers.getFramedIpv6Address();
        maxconcurrentsession = customers.getMaxconcurrentsession();
        delegatedprefix = customers.getDelegatedprefix();
        mac_provision = customers.getMac_provision();
        mac_auth_enable = customers.getMac_auth_enable();
        nasPortId = customers.getNasPortId();
        macRetentionPeriod = customers.getMacRetentionPeriod();
        macRetentionUnit = customers.getMacRetentionUnit();
        secondaryDNS = customers.getSecondaryDNS();
        framedIPNetmask = customers.getFramedIPNetmask();
        framedIPv6Prefix = customers.getFramedIPv6Prefix();
        primaryDNS = customers.getPrimaryDNS();
        primaryIPv6DNS = customers.getPrimaryIPv6DNS();
        secondaryIPv6DNS = customers.getSecondaryIPv6DNS();
        blockNo = customers.getBlockNo();
        vlanId = customers.getVlan_id();
        gatewayIP = customers.getGatewayIP();
        framedroute = customers.getFramedroute();
        accountNo = customers.getAcctno();
        customerVrn = customers.getCustomerVrn();
        customerNid = customers.getCustomerNid();
        renewPlanLimit = customers.getRenewPlanLimit();
        passportNo = customers.getPassportNo();
        drivingLicence = customers.getDrivingLicence();
        billday = customers.getBillday();
        if (customers.getPreviousBillday() != null) {
            previousBillday = customers.getPreviousBillday();
        }
        billDayUpdated = customers.isBillDayUpdated();
        if (customers.getPan() != null) {
            pan = customers.getPan();
        } else{
            pan = null;
        }
        if (customers.getNextQuotaResetDate() != null)
            quotaResetDate = customers.getNextQuotaResetDate().toString();
        else
            quotaResetDate =null;
        if(customers.getNextBillDate()!=null)
            nextbilldate = customers.getNextBillDate().toString();
        else
            nextbilldate=null;

        onuInterface = customers.getOnuInterface();

        for (CustPlanMappping planMappping : custPlanMapppingList) {

            this.custPlanMappping.setId(planMappping.getId());
            this.custPlanMappping.setCustid(planMappping.getCustomer().getId());
            this.custPlanMappping.setPlanId(planMappping.getPlanId());
            this.custPlanMappping.setBillTo(planMappping.getBillTo());
            this.custPlanMappping.setIsInvoiceToOrg(planMappping.getIsInvoiceToOrg());
            this.custPlanMappping.setService(planMappping.getService());
            if (planMappping.getPlanGroup() != null) {
                custPlanMappping.setPlangroupid(planMappping.getPlanGroup().getPlanGroupId());
            }
            this.custPlanMappping.setStatus(planMappping.getStatus());
            this.custPlanMappping.setCustPlanStatus(planMappping.getCustPlanStatus());
            this.custPlanMappping.setCustServiceMappingId(planMappping.getCustServiceMappingId());
            this.custPlanMappping.setIsDelete(planMappping.getIsDelete());
            this.custPlanMappping.setSerialNumber(planMappping.getSerialNumber());
            this.custPlanMappping.setSerialNumber(planMappping.getSerialNumber());
            this.custPlanMappping.setIstrialplan(planMappping.getIstrialplan());
            this.custPlanMappping.setStartDateString(planMappping.getStartDate().toString());
            this.custPlanMappping.setEndDateString(planMappping.getEndDate().toString());
            this.custPlanMappping.setExpiryDateString(planMappping.getExpiryDate().toString());
            this.custPlanMapppingList.add(this.custPlanMappping);
        }

        for (CustomerServiceMapping serviceMapping : customerServiceMappingList) {
            this.customerServiceMapping.setId(serviceMapping.getId());
            this.customerServiceMapping.setServiceId(serviceMapping.getServiceId());
            this.customerServiceMapping.setCustId(serviceMapping.getCustId());
            this.customerServiceMapping.setConnectionNo(serviceMapping.getConnectionNo());
            this.customerServiceMapping.setPartner(serviceMapping.getPartner());
            this.customerServiceMapping.setPop(serviceMapping.getPop());
            this.customerServiceMapping.setStaticOrPooledIP(serviceMapping.getStaticOrPooledIP());
            this.customerServiceMapping.setIsDelete(serviceMapping.getIsDelete());
            this.customerServiceMapping.setCreatedById(serviceMapping.getCreatedById());
            this.customerServiceMapping.setLastModifiedById(serviceMapping.getLastModifiedById());
            this.customerServiceMapping.setStatus(serviceMapping.getStatus());
            this.customerServiceMapping.setMvnoId(serviceMapping.getMvnoId());
            this.customerServiceMapping.setStaticOrPooledIP(serviceMapping.getStaticOrPooledIP());
            this.customerServiceMapping.setBuId(serviceMapping.getBuId());
            this.customerServiceMappingList.add(this.customerServiceMapping);
        }
    }

}

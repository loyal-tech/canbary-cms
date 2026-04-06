package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.adopt.apigw.repository.postpaid.CustomerChargeHistoryRepo;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class SaveCustomerDataShareMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private String accountNumber;
    private String createdByName;
    private Boolean istrialplan = false;
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
    private Integer createdById;
    private Integer lastModifiedById;
    private String lastModifiedByName;
    private String serialNumber;
    private Integer serviceId;
    private String ipv4;
    private String ipv6;
    private String vlan;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
    List<CustomerLedgerDtls> customerLedgerDtlsList=new ArrayList<>();
    CustChargeDetails custChargeDetails = new CustChargeDetails();
    private  String nextbilldate;
    private List<CustomerAddress> address = new ArrayList<>();
    private CustomerAddress customerAddress;
    private  List<CustChargeDetails> indicustChargeDetails;
    private  List<CustChargeDetails> overChargeList;
    List<CustPlanMappping>custPlanMapppingdomainList=new ArrayList<>();
    private List<DebitDocument> debitDocument;
    private CustomerLedger customerLedgerList;
    private  List<CustomerChargeHistory> customerChargeHistories = new ArrayList<>();
    private  List<CustomerDocDetails> customerDocDetails = new ArrayList<>();
    private String contactperson;

    private RecordPaymentPojo recordPaymentPojo;

    private Integer billDay;
    private Integer refMvno;

    private Boolean isCaptiveportal;

    private String referenceNo;
    private Integer earlybilldays;
    private Integer earlybillday;

    private LocalDate earlybilldate;

    private String pan;
    private String blockNo;

    private String drivingLicence;

    private String customerVrn;

    private String passportNo;

    private String customerNid;

    private Integer renewPlanLimit;
    private Integer graceDay;

    private Integer departmentId;

    private String currency;
    private boolean billDayUpdated;
    private Integer previousBillday;
    private List<CustChargeInstallment> custChargeInstallments;

    private String onuInterface;



    public SaveCustomerDataShareMessage(Customers customers, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList, List<CustomerChargeHistory> customerChargeHistories, RecordPaymentPojo recordPaymentPojoSend, Integer refMvno,Boolean isCaptiveportal,String referenceNo, List<CustChargeInstallment> custChargeInstallments) {
        id = customers.getId();
        title = customers.getTitle();
        this.refMvno = refMvno;
        accountNumber=customers.getAcctno();
        nextbilldate=customers.getNextBillDate().toString();
        contactperson = customers.getContactperson();
        parnterId = customers.getPartner().getId();
        customerDocDetails=customers.getCustDocList();
        istrialplan = customers.getIstrialplan();
        this.createdByName = customers.getCreatedByName();
        customerLedgerList=customers.getCustLeger();
        this.isCaptiveportal = isCaptiveportal;
        this.referenceNo = referenceNo;
        this.planPurchaseType=customers.getPlanPurchaseType();
        this.pan=customers.getPan();
        this.graceDay=customers.getGraceDay();
        this.currency = customers.getCurrency();
        this.billDayUpdated = customers.isBillDayUpdated();
        this.previousBillday = customers.getPreviousBillday();
        if(customers.getDepartmentId() != null) {
            this.departmentId = customers.getDepartmentId();
        }
        if (customers.getBillday()!=null) {
            this.billDay = customers.getBillday();
        }
//        address=customers.getAddressList();
        for(CustomerAddress add: customers.getAddressList()) {
            CustomerAddress address1 = new CustomerAddress();
            address1.setAddress1(add.getAddress1());
            address1.setAddress2(add.getAddress2());
            address1.setAddressType(add.getAddressType());
            address1.setAddressType(add.getAddressType());
            address1.setId(add.getId());
            address1.setCityId(add.getCityId());
            address1.setCountryId(add.getCountryId());
            address1.setFullAddress(add.getFullAddress());
            address1.setLandmark(add.getLandmark());
            address1.setLandmark1(add.getLandmark1());
            address1.setStateId(add.getStateId());
            address1.setVersion(add.getVersion());
            address1.setPincodeId(add.getPincodeId());
            address1.setAreaId(add.getAreaId());
            address.add(address1);
        }
        username = customers.getUsername();
        password = customers.getPassword();
        firstname = customers.getFirstname();
        lastname = customers.getLastname();
        custname = customers.getCustname();
        email = customers.getEmail();
        mobile = customers.getMobile();
        debitDocument=customers.getDebitDocList();
        countryCode = customers.getCountryCode();
        serviceAreaId = customers.getServicearea() != null ? Math.toIntExact(customers.getServicearea().getId()) : null;
        networkdevicesId = customers.getNetworkdevices() != null ? Math.toIntExact(customers.getNetworkdevices().getId()) : null;
        status = customers.getStatus();
        custtype = customers.getCusttype();
        phone = customers.getPhone();
        mvnoId = customers.getMvnoId();
        buId = customers.getBuId();
        lcoId = customers.getLcoId();
        is_from_pwc = customers.getIs_from_pwc();
        isDeleted = customers.getIsDeleted();
        oltportid = customers.getOltportid();
        oltslotid = customers.getOltslotid();
        fullName = customers.getFullName();
        lastModifiedById=customers.getLastModifiedById();
        lastModifiedByName= customers.getLastModifiedByName();
        ipv4= customers.getIpv4();
        ipv6= customers.getIpv6();
        vlan = customers.getVlan();
        earlybilldays=customers.getEarlybilldays();
        earlybillday=customers.getEarlybillday();
        earlybilldate = customers.getEarlybilldate();
        blockNo = customers.getBlockNo();
        drivingLicence = customers.getDrivingLicence();
        customerVrn = customers.getCustomerVrn();
        passportNo = customers.getPassportNo();
        customerNid = customers.getCustomerNid();
        renewPlanLimit = customers.getRenewPlanLimit();
        if(Objects.nonNull(recordPaymentPojoSend)){
            recordPaymentPojo = recordPaymentPojoSend;
        }
//        indicustChargeDetails
        for (CustChargeDetails custChargeDetails:customers.getIndiChargeList()){
            this.custChargeDetails.setBillTo(custChargeDetails.getBillTo());
            this.custChargeDetails.setId(custChargeDetails.getId());
            this.custChargeDetails.setBillableCustomerId(custChargeDetails.getBillableCustomerId());
            this.custChargeDetails.setCharge_name(custChargeDetails.getCharge_name());
            this.custChargeDetails.setBillTo(custChargeDetails.getBillTo());
            this.custChargeDetails.setActualprice(custChargeDetails.getActualprice());
            this.custChargeDetails.setChargeid(custChargeDetails.getChargeid());
            this.custChargeDetails.setChargetype(custChargeDetails.getChargetype());
            this.custChargeDetails.setBillingCycle(custChargeDetails.getBillingCycle());
            this.custChargeDetails.setConnection_no(custChargeDetails.getConnection_no());
            this.custChargeDetails.setCustPlanMapppingId(custChargeDetails.getCustPlanMapppingId());
            this.custChargeDetails.setIsDeleted(custChargeDetails.getIsDeleted());
            this.custChargeDetails.setDbr(custChargeDetails.getDbr());
            this.custChargeDetails.setDiscount(custChargeDetails.getDiscount());
            this.custChargeDetails.setIppooldtlsid(custChargeDetails.getIppooldtlsid());
            this.custChargeDetails.setIs_reversed(custChargeDetails.getIs_reversed());
            this.custChargeDetails.setIsInvoiceToOrg(custChargeDetails.getIsInvoiceToOrg());
            this.custChargeDetails.setIsUsed(custChargeDetails.getIsUsed());
            this.custChargeDetails.setLastBillDateStr(custChargeDetails.getLastBillDate().toString());
            this.custChargeDetails.setNextBillDateStr(custChargeDetails.getNextBillDate().toString());
            this.custChargeDetails.setNewAmount(custChargeDetails.getNewAmount());
            this.custChargeDetails.setPlanid(custChargeDetails.getPlanid());
            this.custChargeDetails.setLastModifiedById(custChargeDetails.getLastModifiedById());
            this.custChargeDetails.setCreatedByName(custChargeDetails.getCreatedByName());
            this.custChargeDetails.setValidity(custChargeDetails.getValidity());
            this.custChargeDetails.setPrice(custChargeDetails.getPrice());
            indicustChargeDetails.add(this.custChargeDetails);

        }
//        overChargeList
        for (CustChargeDetails custChargeDetails: customers.getOverChargeList()) {
            this.custChargeDetails.setBillTo(custChargeDetails.getBillTo());
            this.custChargeDetails.setId(custChargeDetails.getId());
            this.custChargeDetails.setBillableCustomerId(custChargeDetails.getBillableCustomerId());
            this.custChargeDetails.setCharge_name(custChargeDetails.getCharge_name());
            this.custChargeDetails.setBillTo(custChargeDetails.getBillTo());
            this.custChargeDetails.setActualprice(custChargeDetails.getActualprice());
            this.custChargeDetails.setChargeid(custChargeDetails.getChargeid());
            this.custChargeDetails.setChargetype(custChargeDetails.getChargetype());
            this.custChargeDetails.setBillingCycle(custChargeDetails.getBillingCycle());
            this.custChargeDetails.setConnection_no(custChargeDetails.getConnection_no());
            this.custChargeDetails.setCustPlanMapppingId(custChargeDetails.getCustPlanMapppingId());
            this.custChargeDetails.setIsDeleted(custChargeDetails.getIsDeleted());
            this.custChargeDetails.setDbr(custChargeDetails.getDbr());
            this.custChargeDetails.setDiscount(custChargeDetails.getDiscount());
            this.custChargeDetails.setIppooldtlsid(custChargeDetails.getIppooldtlsid());
            this.custChargeDetails.setIs_reversed(custChargeDetails.getIs_reversed());
            this.custChargeDetails.setIsInvoiceToOrg(custChargeDetails.getIsInvoiceToOrg());
            this.custChargeDetails.setIsUsed(custChargeDetails.getIsUsed());
            this.custChargeDetails.setLastBillDateStr(custChargeDetails.getLastBillDate().toString());
            this.custChargeDetails.setNextBillDateStr(custChargeDetails.getNextBillDate().toString());
            this.custChargeDetails.setNewAmount(custChargeDetails.getNewAmount());
            this.custChargeDetails.setPlanid(custChargeDetails.getPlanid());
            this.custChargeDetails.setLastModifiedById(custChargeDetails.getLastModifiedById());
            this.custChargeDetails.setCreatedByName(custChargeDetails.getCreatedByName());
            this.custChargeDetails.setValidity(custChargeDetails.getValidity());
            this.custChargeDetails.setPrice(custChargeDetails.getPrice());
            overChargeList.add(this.custChargeDetails);
        }
        parnterId = customers.getPartner() != null ? customers.getPartner().getId() : null;
        calendarType = customers.getCalendarType();
        dunningCategory = customers.getDunningCategory();
        parentCustUsername = customers.getParentCustomers() != null ? customers.getParentCustomers().getUsername() : null;
        parentCustId = customers.getParentCustomers() != null ? customers.getParentCustomers().getId() : null;
        feasibilityRequired = customers.getFeasibilityRequired();
        valleyType = customers.getValleyType();
        customerArea = customers.getCustomerArea();
        custcategory = customers.getCustcategory();
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
        onuInterface = customers.getOnuInterface();
      //  custPlanMapppingdomainList=custPlanMapppingList;
        for (CustPlanMappping planMappping : custPlanMapppingList) {
            CustPlanMapppingPojo custPlanMapppingPojo =  new CustPlanMapppingPojo();
            custPlanMapppingPojo.setId(planMappping.getId());
                custPlanMapppingPojo.setDiscount(planMappping.getDiscount());
            custPlanMapppingPojo.setCustid(customers.getId());
            custPlanMapppingPojo.setPlanId(planMappping.getPlanId());
            custPlanMapppingPojo.setBillTo(planMappping.getBillTo());
            custPlanMapppingPojo.setIsInvoiceToOrg(planMappping.getIsInvoiceToOrg());
            custPlanMapppingPojo.setPlanValidityDays(planMappping.getPlanValidityDays());
            custPlanMapppingPojo.setStartDateString(planMappping.getStartDate().toString());
            custPlanMapppingPojo.setEndDateString(planMappping.getEndDate().toString());
            custPlanMapppingPojo.setExpiryDateString(planMappping.getExpiryDate().toString());
            custPlanMapppingPojo.setService(planMappping.getService());
            custPlanMapppingPojo.setBillableCustomerId(planMappping.getBillableCustomerId());
            if (planMappping.getPlanGroup() != null) {
                custPlanMapppingPojo.setPlangroupid(planMappping.getPlanGroup().getPlanGroupId());
                custPlanMapppingPojo.setRenewalId(planMappping.getRenewalId());
            }
            custPlanMapppingPojo.setStatus(planMappping.getStatus());
            custPlanMapppingPojo.setCustPlanStatus(planMappping.getCustPlanStatus());
            custPlanMapppingPojo.setCustServiceMappingId(planMappping.getCustServiceMappingId());
            custPlanMapppingPojo.setIsDelete(planMappping.getIsDelete());
            custPlanMapppingPojo.setSerialNumber(planMappping.getSerialNumber());
            custPlanMapppingPojo.setSerialNumber(planMappping.getSerialNumber());
            custPlanMapppingPojo.setPurchaseFrom(planMappping.getPurchaseFrom());
            custPlanMapppingPojo.setIstrialplan(planMappping.getIstrialplan());
            custPlanMapppingPojo.setInvoiceType(planMappping.getInvoiceType());
            if(planMappping.getVasId() != null){
                custPlanMapppingPojo.setVasId(planMappping.getVasId());
            }
            this.custPlanMapppingList.add(custPlanMapppingPojo);
        }

        for (CustomerServiceMapping serviceMapping : customerServiceMappingList) {
            CustomerServiceMapping customerServiceMapping1 = new CustomerServiceMapping();
            customerServiceMapping1.setId(serviceMapping.getId());
            customerServiceMapping1.setServiceId(serviceMapping.getServiceId());
            customerServiceMapping1.setCustId(serviceMapping.getCustId());
            customerServiceMapping1.setConnectionNo(serviceMapping.getConnectionNo());
            customerServiceMapping1.setPartner(serviceMapping.getPartner());
            customerServiceMapping1.setPop(serviceMapping.getPop());
            customerServiceMapping1.setStaticOrPooledIP(serviceMapping.getStaticOrPooledIP());
            customerServiceMapping1.setIsDelete(serviceMapping.getIsDelete());
            customerServiceMapping1.setCreatedById(serviceMapping.getCreatedById());
            customerServiceMapping1.setLastModifiedById(serviceMapping.getLastModifiedById());
            customerServiceMapping1.setStatus(serviceMapping.getStatus());
            customerServiceMapping1.setInvoiceType(serviceMapping.getInvoiceType());
            customerServiceMapping1.setMvnoId(serviceMapping.getMvnoId());
            customerServiceMapping1.setBuId(serviceMapping.getBuId());
            customerServiceMapping1.setDiscount(serviceMapping.getDiscount());
            customerServiceMapping1.setDiscountType(serviceMapping.getDiscountType());
            customerServiceMapping1.setDiscountExpiryDateString(serviceMapping.getDiscountExpiryDate()!=null?serviceMapping.getDiscountExpiryDate().toString():null);
            this.customerServiceMappingList.add(customerServiceMapping1);
        }
        for(CustomerLedgerDtls customerLedgerDtls:customers.getLedgerDtls()){
            customerLedgerDtls.setCustId(customerLedgerDtls.getCustomer().getId());
            customerLedgerDtls.setCustomer(null);
            customerLedgerDtlsList.add(customerLedgerDtls);

        }
        for (CustomerChargeHistory customerChargeHistory: customerChargeHistories){

            CustomerChargeHistory customerChargeHistory1= new CustomerChargeHistory();
            customerChargeHistory1.setId(customerChargeHistory.getId());
            customerChargeHistory1.setCustomerId(customerChargeHistory.getCustomerId());
            customerChargeHistory1.setPlanId(customerChargeHistory.getPlanId());
            customerChargeHistory1.setChargeId(customerChargeHistory.getChargeId());
            customerChargeHistory1.setTaxId(customerChargeHistory.getTaxId());
            customerChargeHistory1.setChargeAmount(customerChargeHistory.getChargeAmount());
            customerChargeHistory1.setTaxAmount(customerChargeHistory.getTaxAmount());
            customerChargeHistory1.setDiscount(customerChargeHistory.getDiscount());
            customerChargeHistory1.setCustPlanMapppingId(customerChargeHistory.getCustPlanMapppingId());
            customerChargeHistory1.setPlanGroupId (customerChargeHistory.getPlanGroupId());
            customerChargeHistory1.setPlanName (customerChargeHistory.getPlanName());
            customerChargeHistory1.setChargeName  (customerChargeHistory.getChargeName());
            customerChargeHistory1.setCharge_desc  (customerChargeHistory.getCharge_desc());
            customerChargeHistory1.setChargeType  (customerChargeHistory.getChargeType());
            customerChargeHistory1.setBillingCycle  (customerChargeHistory.getBillingCycle());
            customerChargeHistory1.setSaccode  (customerChargeHistory.getSaccode());
            if(customerChargeHistory.getNextBillDate() != null)
                customerChargeHistory1.setNextBillDateString (customerChargeHistory.getNextBillDate().toString());
            if(customerChargeHistory.getLastBillDate() != null)
                customerChargeHistory1.setLastBillDateString  (customerChargeHistory.getLastBillDate().toString());
            if(customerChargeHistory.getCustomerBillDay() != null)
                customerChargeHistory1.setCustomerBillDay (customerChargeHistory.getCustomerBillDay());
            customerChargeHistory1.setIsFirstChargeApply (customerChargeHistory.getIsFirstChargeApply());
            customerChargeHistory1.setIsRoyaltyApply (customerChargeHistory.getIsRoyaltyApply());
            this.customerChargeHistories.add(customerChargeHistory1);
        }
        if(custChargeInstallments != null){
            this.custChargeInstallments = custChargeInstallments;
        }
    }

//    public SaveCustomerDataShareMessage(List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList, List<CustomerChargeHistory> customerChargeHistories,List<CustChargeDetails> custChargeDetails) {
//
//
//        for (CustPlanMappping planMappping : custPlanMapppingList) {
//            this.custPlanMappping.setId();
//            this.custPlanMappping.setPlanId();
//            this.custPlanMappping.setServiceId();
//            this.custPlanMappping.setStartDate();
//            this.custPlanMappping.setEndDate();
//            this.custPlanMappping.setexpiryDate;
//            this.custPlanMappping.setstatus;
//            this.custPlanMappping.setcustomerId;
//            this.custPlanMappping.setofferPrice;
//            this.custPlanMappping.settaxAmount;
//            this.custPlanMappping.setwalletBalUsed;
//            this.custPlanMappping.setpurchaseType;
//            this.custPlanMappping.setonlinePurchaseId;
//            this.custPlanMappping.setpurchaseFrom;
//            this.custPlanMappping.setbillableCustomerId;
//            this.custPlanMappping.setisinvoicestop;
//            this.custPlanMappping.setistrialplan;
//            this.custPlanMappping.setdebitdocid;
//            this.custPlanMappping.setisDelete;
//            this.custPlanMappping.setdiscount;
//            this.custPlanMappping.setplanValidityDays;
//            this.custPlanMappping.setplanGroupId;
//            this.custPlanMappping.setisInvoiceToOrg;
//            this.custPlanMappping.setbillTo;
//            this.custPlanMappping.setnewAmount;
//            this.custPlanMappping.setrenewalId;
//            this.custPlanMappping.setcustRefId;
//            this.custPlanMappping.setnextStaff;
//            this.custPlanMappping.setdbr;
//            this.custPlanMappping.setcustPlanStatus;
//            this.custPlanMappping.setnextTeamHierarchyMappingId;
//            this.custPlanMappping.setisInvoiceCreated;
//            this.custPlanMappping.setoldDiscount;
//            this.custPlanMappping.setgraceDays;
//            this.custPlanMappping.setstopServiceDate;
//            this.custPlanMappping.setcustServiceMappingId;
//            this.custPlanMappping.setgraceDateTime;
//            this.custPlanMappping.setinvoiceType;
//            this.custPlanMappping.settraildebitdocid;
//            this.custPlanMappping.setpromisetopay_renew_count;
//            this.custPlanMappping.setisTrialValidityDays;
//            this.custPlanMappping.settrialPlanValidityCount;
//            this.custPlanMappping.setstartServiceDate;
//            this.custPlanMappping.setserviceHoldDate;
//            this.custPlanMappping.setpromise_to_pay_startdate;
//            this.custPlanMappping.setpromise_to_pay_enddate;
//            this.custPlanMappping.settotalHoldDays;
//            this.custPlanMappping.setdiscountType;
//            this.custPlanMappping.setdiscountExpiryDate;
//            this.custPlanMappping.setdownTimeExpiryDate;
//            this.custPlanMappping.setdownTimeStartDate;
//            this.custPlanMappping.setcprIdForPromiseToPay;
//            this.custPlanMappping.setisHold;
//            this.custPlanMappping.setisVoid;
//            this.custPlanMappping.setserviceHoldBy;
//            this.custPlanMappping.setserviceStartBy;
//            this.custPlanMappping.setisContainsCustomerInvoice;
//            this.custPlanMappping.setcustomerCpr;
//            this.custPlanMappping.setcreditdocid;
//            this.custPlanMappping.setserviceId;
//            this.custPlanMappping.setserialNumber;
//
//        }
//
//
//
//    }


    }

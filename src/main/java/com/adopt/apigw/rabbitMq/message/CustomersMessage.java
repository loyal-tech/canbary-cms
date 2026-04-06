package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.model.postpaid.CustomerLedgerPojo;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.pojo.api.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
@Data
public class CustomersMessage {

    private Integer id;

    private String username;

    private String password;

    private String firstname;

    private String lastname;

    private String email;

    private String title;

    private String custname;

    private String contactperson;

    private String pan;

    private String gst;

    private String aadhar;

    private String status;


    private String acctno;

    private String custtype;

    private String phone;

    private Integer billday;

    private Integer partnerid;

    private String onuid;


    private String address1;

    private String address2;

    private Integer city;

    private Integer state;

    private Integer country;

    private Integer pincode;

    private Integer area;

//    private String pincode;

    private Double outstanding;

    private String oldpassword1;

    private String newpassword;

    private String oldpassword2;

    private String oldpassword3;

    private String selfcarepwd;

    private String dunningSubSector;

    private String dunningSubType;
    private String dunningType;

    private String dunningSector;

    private long popid;

    private String mobile;

    // added country code
    private String countryCode;

    private String cafno;

    private String altmobile;

    private String altphone;

    private String altemail;

    private String fax;

    private Integer resellerid;

    private Integer salesrepid;

    private String voicesrvtype;

    private String didno;

    private String childdidno;

    private String intercomno;

    private String intercomgrp;

    private String custcategory;

    private String networktype;

    private Long serviceareaid;

    private Long networkdevicesid;

    private String BNGRoutername;

    private String IPPrefixes;

    private String IPV6Prefixes;

    private String LANIP;

    private String LANIPV6;

    private String LLAccountid;

    private String LLConnectiontype;

    private String LLExpirydate;

    private String LLMedium;

    private String LLServiceid;

    private String MACADDRESS;

    private String Peerip;

    private String POOLIP;

    private String QOS;

    private String RDExport;

    private String RDValue;

    private String VLANID;

    private String VRFName;

    private String VSIID;

    private String VSIName;

    private String WANIP;

    private String WANIPV6;

    private String billentityname;

    private String addparam1;

    private String addparam2;

    private String addparam3;

    private String addparam4;

    private String purchaseorder;

    private String remarks;

    private String allowedIPAddress;

    private String OldWANIP;

    private String OldLLAccountid;

    private String createDateString;
    private String updateDateString;

    private String latitude;
    private String longitude;
    private String url;
    private String gis_code;
    private String salesremark;
    private String servicetype;

    private String isCustCaf;

    private Integer mvnoId;

    private String tinNo;

    private String passportNo;

    private String dunningCategory;

    private Integer plangroupid;

    private Integer parentCustomerId;

    private String parentCustomerName;

    private String invoiceType;

    private String calendarType;

    private double discount = 0d;

    private Long buId;

    private Integer custPackageId;

    private String planPurchaseType;

    private Long branch;

    private String valleyType;

    private String customerArea;

    private String customerType;

    private String customerSubType;

    private String customerSector;

    private String customerSubSector;

    private Integer lcoId;

    private Boolean is_from_pwc;

    private String leadNo;

    private Double flatAmount;

    private String ezyBillCustomersId;

    private String ezyBillAccountNumber;

    private String paymentOwner;

    private String ezyBillStockId;

    private String feasibility;

    public CustomersMessage(Customers customers) {
        this.id = customers.getId();
        this.username = customers.getUsername();
        this.password = customers.getPassword();
        this.firstname = customers.getFirstname();
        this.lastname = customers.getLastname();
        this.email = customers.getEmail();
        this.title = customers.getTitle();
        this.custname = customers.getCustname();
        this.contactperson = customers.getContactperson();
        this.pan = customers.getPan();
        this.gst = customers.getGst();
        this.aadhar = customers.getAadhar();
        this.status = customers.getStatus();
        this.acctno = customers.getAcctno();
        this.custtype = customers.getCusttype();
        this.phone = customers.getPhone();
        this.partnerid = customers.getParentCustomersId();
        this.onuid = customers.getOnuid();
        this.address1 = customers.getAddress1();
        this.address2 = customers.getAddress2();
        this.city = customers.getCity();
        this.state = customers.getState();
        this.country = customers.getCountry();
        this.pincode = customers.getPincode();
        this.area = customers.getArea();
        this.popid = customers.getPopid();
        this.mobile = customers.getMobile();
        this.serviceareaid = customers.getServicearea().getId();
        this.QOS = customers.getQOS();
        this.mvnoId = customers.getMvnoId();
//        this.plangroupid = customers.getPlangroup().getPlanGroupId();
        this.parentCustomerName = customers.getPartnerName();
        this.invoiceType = customers.getInvoiceType();
        this.calendarType = customers.getCalendarType();
        this.buId = customers.getBuId();
        this.custPackageId = customers.getBillRunCustPackageRelId();
        this.branch = customers.getBranch();
        this.valleyType = customers.getValleyType();
        this.lcoId = customers.getLcoId();
        this.is_from_pwc = customers.getIs_from_pwc();
        this.leadNo = customers.getLeadNo();
        this.flatAmount = customers.getOutStandingAmount();
        this.ezyBillCustomersId = customers.getEzyBillCustomersId();
        this.ezyBillAccountNumber = customers.getEzyBillAccountNumber();
//        this.ezyBillStockId = customers.getEzyBillStockId();
        this.feasibility = customers.getFeasibility();
    }

}

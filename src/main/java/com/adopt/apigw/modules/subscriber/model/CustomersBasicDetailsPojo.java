package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.pojo.api.CustChargeOverrideDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.pojo.api.CustNetworkDetailsDTO;
import com.adopt.apigw.pojo.api.PartnerPojo;
import com.adopt.apigw.pojo.api.StaffUserPojo;

@Data
public class CustomersBasicDetailsPojo {

    private Integer id;
    //Subscriber Details
    private String name;
    private String username;
    private String cafno;
    private String aadhar;
    private String contactperson;
    private String mobile;
    private String phone;
    private String email;
    private String altemail;
    private String altmobile;
    private String altphone;
    private String fax;
    private String gst;
    private String pan;
    private String address;

    //Connection && Network Details
    private boolean connectivity = true;
    private CustNetworkDetailsDTO networkDetails;
    private String onuid;
    private String stroltname;
    private String strslotname;
    private String strportname;
    private String strconntype;
    private Long defaultpoolid;
    private String defaultpool;
    private String onlinerenewalflag;
    private String voipenableflag;
    private String mactelflag;
    private List<CustMacMappping> macAddressModelList = new ArrayList<>();

    //Plan & Usage Details
    private String acctno;
    private Integer partnerId;
    private String partnerName;
    private Integer salesRepId;
    private String salesRepName;
    private List<CustomerPlansModel> planList = new ArrayList<>();
    private CaseCountModel caseCount;
    private String expiryDate;
    private Double maxSession;
    private String ipAddress;
    private String ipPurDate;
    private String ipExpDate;
    private String voicesrvtype;
    private String didno;
    private String intercomgrp;
    private Double outstanding;
    private String childdidno;
    private String intercomno;
    private String remarks;

    //Status
    private String status;

    private String latitude;
    private String longitude;
    private String url;
    private String gis_code;
    private String salesremark;
    private String servicetype;
    private Integer previousCafApprover;
    private Integer nextCafApprover;
    private String serviceareaName;
    private String custtype;
    private String passportNo;
    private Integer custPackagId;
    private String password;
    private Integer nextTeamHierarchyMapping;
    private CustChargeOverrideDTO custChargeOverride;
}

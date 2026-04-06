package com.adopt.apigw.modules.linkacceptance.model;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
public class LinkAcceptanceDTO implements IBaseDto2 {

    @Id
    private Long id;
    private String circuitName;
    private String circuitStatus;
    private Long cafNo;
    private String uploadCAF;
    private String customerName;
    private Long accountNumber;
    private String typeOfLink;  //dropdowm value not clear.
    private Long planService;
    private LocalDate linkInstallationDate;
    private LocalDate linkAcceptanceDate;
    private LocalDate purchaseOrderDate;
//    private String circleName;
//    private LocalDate orderLoginDate;
    private Long partner;  //(Select Partner Dropdown)
    private LocalDate expiryDate;
    private Long distance;
    private String distanceUnit;  //(KM)
    private Long bandwidth;  //(Kbps)
    private String uploadQOS;
    private String downloadQOS;
    private String linkRouterLocation;
    private String linkPortType;
    private Long linkRouterIP;
    private String linkPortOnRouter;
    private Long vLANId;
    private String bandwidthType;
    private String linkRouterName;
    private Long circuitBillingId;
    private String pop;      //(Select POP dropdown);
    private String associatedLevel;
    private String locationLevel1;
    private String locationLevel2;
    private String locationLevel3;
    private String locationLevel4;
    private Long baseStationId1;
    private Long baseStationId2;
//    private String organisationCircle;
//    private String terminationCircle;
//    private String organisationAddress;
    private String terminationAddress;
//    private String organisationAddress2;
//    private String terminationAddress2;
    private String note;
    private String contactPerson;
    private String contactPerson1;
//    private String contactPerson2;
    private String mobileNumber;
    private String mobileNumber1;
//    private String mobileNumber2;
    private String landLineNumber;
    private String landLineNumber1;
//    private String landLineNumber2;
    private String emailId;
    private String emailId1;
//    private String emailId2;
    private String remarks;
//    private Long traiRate;
    private String otcChargesFile;
    private String serviceChargerFile;
    //Items :

    //Add Items
    private String staticOrPooledIP;
    private String chargeTypeFile;
    private String billingCycle;
    private String billingType;
    private String billable; //Circuit Or Account;
    private String billingGroup;  //dropdown
    private String payable; //circuit or account;
    private String enableProcessing; //- Yes or No
    private String deposite;
    private String poNumber;
    private String billRemark;
    private String fullName;
    private String organisation;
    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String state;
    private String country;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private String status;
    private Long buId;
    private Integer custId;
    private String serviceAreaType;  // HO/BO/CO
    private String branch;
    private String connectionType;

    @Override
    @JsonIgnore
    public Long getIdentityKey() {
        return id;
    }

    @Override
    @JsonIgnore
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    @JsonIgnore
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId=mvnoId;
    }
    @Override
    @JsonIgnore
    public Long getBuId() {
        return buId;
    }
}

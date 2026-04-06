package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.pojo.api.CustNetworkDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CustomerListingPojo {
    private Integer id;
    //Subscriber Details
    private String title;
    private String name;
    private String firstname;
    private String lastname;
    private String username;
    private String mobile;
    private String email;

    //Connection && Network Details
    private boolean connectivity = true;
    private CustNetworkDetailsDTO networkDetails;

    //Account details
    private String acctno;
    private Double outstanding;
    
    private Integer previousCafApprover;
    private Integer nextCafApprover;

    //Status
    private String status;
    
    private String custtype;

    private String ConnectionMode;
    
    private String calendarType;

    private Boolean isinvoicestop = false;

    private Boolean istrialplan = false;
    
    private String leadNo;
    
    private Long leadId;
    private Integer nextTeamHierarchyMapping;

    private ServiceArea serviceArea;

    private List<CustomerAddress> custAddressList = new ArrayList<>();

    private String customerAddress;

    private Integer currentAssigneeParentId;

    private String currency;

    private Integer mvnoId;

    public CustomerListingPojo(Integer id, String firstname, String lastname, String username, String mobile,
                               String email, String acctno, Double outstanding, String status, String custtype,
                               String calendarType, Boolean isinvoicestop, Boolean istrialplan, String leadNo,
                               Long leadId, Integer nextTeamHierarchyMapping, ServiceArea serviceArea, String title) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.mobile = mobile;
        this.email = email;
        this.acctno = acctno;
        this.outstanding = outstanding;
        this.status = status;
        this.custtype = custtype;
        this.calendarType = calendarType;
        this.isinvoicestop = isinvoicestop;
        this.istrialplan = istrialplan;
        this.leadNo = leadNo;
        this.leadId = leadId;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.serviceArea = serviceArea;
        this.title = title;
    }

    public CustomerListingPojo(Integer id, String firstname, String lastname, String username, String mobile,
                               String email, String acctno, Double outstanding, String status, String custtype,
                               String calendarType, Boolean isinvoicestop, Boolean istrialplan, String leadNo,
                               Long leadId, Integer nextTeamHierarchyMapping, ServiceArea serviceArea, String title,String currency,Integer mvnoId) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.mobile = mobile;
        this.email = email;
        this.acctno = acctno;
        this.outstanding = outstanding;
        this.status = status;
        this.custtype = custtype;
        this.calendarType = calendarType;
        this.isinvoicestop = isinvoicestop;
        this.istrialplan = istrialplan;
        this.leadNo = leadNo;
        this.leadId = leadId;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.serviceArea = serviceArea;
        this.title = title;
        this.mvnoId = mvnoId;
        this.currency=currency;
    }

}

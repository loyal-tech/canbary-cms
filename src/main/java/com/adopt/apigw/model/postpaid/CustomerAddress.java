package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingManagement;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.SubArea.Domain.SubArea;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLMSUBSCRIBERADDRESSREL")
@EntityListeners(AuditableListener.class)
public class CustomerAddress extends Auditable {
	
	
	/*
CREATE TABLE TBLMSUBSCRIBERADDRESSREL
  (
  	ADDRESSID SERIAL,
    SUBSCRIBERID bigint UNSIGNED,
    ADDRESSTYPE  VARCHAR(16),
    ADDRESS1     VARCHAR(255),
    ADDRESS2     VARCHAR(255),mesd),
    FOREIGN KEY (CITYID) REFERENCES TBLMCITY (CITYID),
    FOREIGN KEY (STATEID) REFERENCES TBLMSTATE (STATEID),
    FOREIGN KEY (COUNTRYID) REFERENCES TBLMCOUNTRY (COUNTRYID)
  );
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESSID", nullable = false, length = 40)
    private Integer id;


    @Column(name = "ADDRESSTYPE", nullable = false, length = 40)
    private String addressType;

    @Column(name = "ADDRESS1", length = 40)
    private String address1;

    @Column(name = "ADDRESS2", length = 40)
    private String address2;

    @Column(name = "LANDMARK", length = 100)
    private String landmark;

    @Column(name = "LANDMARK1", length = 100)
    private String landmark1;


    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "AREAID")
    private Area area;

    @Column(name = "AREAID", insertable = false, updatable = false)
    private Integer areaId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "PINCODEID")
    private Pincode pincode;

    @Column(name = "PINCODEID", insertable = false, updatable = false)
    private Integer pincodeId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "CITYID")
    private City city;

    @Column(name = "CITYID", insertable = false, updatable = false)
    private Integer cityId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "STATEID")
    private State state;

    @Column(name = "STATEID", insertable = false, updatable = false)
    private Integer stateId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "COUNTRYID")
    private Country country;

    @Column(name = "COUNTRYID", insertable = false, updatable = false)
    private Integer countryId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "SUBSCRIBERID")
    private Customers customer;

    @Transient
    private String fullAddress;


    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "version", length = 40)
    private String version;

    @Column(name = "shift_id",nullable = false)
    private Long shiftId;
    @Column(name = "shifted_partner_id")
    private Integer shiftedPartnerId;

    @Column(name= "shifted_service_area_id")
    private Integer shitedServiceAreaId;

    @Transient
    private String requestedByName;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "subareaid")
    private SubArea subarea;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "building_mgmt_id")
    private BuildingManagement buildingManagement;

    @Column(name = "building_number")
    private String buildingNumber;
    @Transient
    private LocalDateTime requestedDate;
    public CustomerAddress() {
    }

    public CustomerAddress(CustomerAddress customerAddress) {
        this.id = customerAddress.getId();
        this.addressType = customerAddress.getAddressType();
        this.address1 = customerAddress.getAddress1();
        this.address2 = customerAddress.getAddress2();
        this.landmark = customerAddress.getLandmark();
        this.landmark1 = customerAddress.getLandmark1();
        this.city = customerAddress.getCity();
        this.cityId = customerAddress.getCityId();
        this.state = customerAddress.getState();
        this.stateId = customerAddress.getStateId();
        this.country = customerAddress.getCountry();
        this.countryId = customerAddress.getCountryId();
        this.customer = customerAddress.getCustomer();
        this.fullAddress = customerAddress.getFullAddress();
        this.isDelete = customerAddress.getIsDelete();
        this.pincode = customerAddress.getPincode();
        this.pincodeId = customerAddress.getPincodeId();
        this.area = customerAddress.getArea();
        this.areaId = customerAddress.getAreaId();
        this.nextTeamHierarchyMappingId = customerAddress.getNextTeamHierarchyMappingId();
        this.nextStaff = customerAddress.getNextStaff();
        this.status = customerAddress.getStatus();
        this.version = customerAddress.getVersion();
        this.subarea=customerAddress.getSubarea();
        this.buildingManagement=customerAddress.getBuildingManagement();

    }

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullAddress = "";
            if (null != this.getAddress1() && !this.getAddress1().isEmpty()) {
                fullAddress = this.getAddress1() + " ";
            }
            if (null != this.getAddress2() && !this.getAddress2().isEmpty()) {
                fullAddress += this.getAddress2() + " ";
            }
            if (null != this.getLandmark() && !this.getLandmark().isEmpty()) {
                fullAddress += this.getLandmark() + " ";
            }
            if (null != this.getLandmark1() && !this.getLandmark1().isEmpty()) {
                fullAddress += this.getLandmark1() + " ";
            }
            if (null != this.getState()) {
                fullAddress += this.getState().getName() + " ";
            }
            if (null != this.getCity()) {
                fullAddress += this.getCity().getName() + " ";
            }
            if (null != this.getPincode()) {
                fullAddress += this.getPincode().getPincode() + " ";
            }
            if (null != this.getArea()) {
                fullAddress += this.getArea().getName() + " ";
            }
            if (null != this.getState()) {
                fullAddress += this.getState().getName() + " ";
            }
            if (null != this.getCountry()) {
                fullAddress += this.getCountry().getName() + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
}

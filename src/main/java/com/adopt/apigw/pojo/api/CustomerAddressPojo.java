package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CustomerAddressPojo extends Auditable {

    private Integer id;

    @NotNull(message = "Please select addresstype")
    private String addressType;

    private String address1;

    private String address2;

    @NotNull(message = "Please enter landmark")
    private String landmark;

    public String getLandmark1() {
        return landmark1;
    }

    public void setLandmark1(String landmark1) {
        this.landmark1 = landmark1;
    }

    private String landmark1;

    private Integer areaId;
    private Integer pincodeId;

    @NotNull(message = "Please Select City")
    private Integer cityId;

    @NotNull(message = "Please Select State")
    private Integer stateId;

    @NotNull(message = "Please Select Country")
    private Integer countryId;

    private Integer customerId;

    private String fullAddress;

    private Boolean isDelete = false;

    private Integer nextTeamHierarchyMappingId;

    private Integer nextStaff;

    private String status;

    private String version;

    private Long shiftId;

    private Integer shiftedPartnerId;

    private Integer shiftedServiceAreaId;

    private String requestedByName;

    private LocalDateTime requestedDate;
    private Long subareaId;
    private Long building_mgmt_id;
    private String buildingNumber;

    private String latitude;

    private String longitude;

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }


    public Integer getPincodeId() {
        return pincodeId;
    }

    public void setPincodeId(Integer pincodeId) {
        this.pincodeId = pincodeId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Integer getNextStaff() { return nextStaff;}

    public void setNextStaff(Integer nextStaff) { this.nextStaff = nextStaff; }

    public String getStatus() { return status;}

    public void setStatus(String status) { this.status = status; }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;

    @Override
    public String toString() {
        return "CustomerAddressPojo [id=" + id + ", addressType=" + addressType + ", address1=" + address1
                + ", address2=" + address2 + ", pincode=" + pincodeId + ", cityId=" + cityId + ", stateId=" + stateId
                + ", countryId=" + countryId + ", customerId=" + customerId + ", fullAddress=" + fullAddress + "]";
    }
}

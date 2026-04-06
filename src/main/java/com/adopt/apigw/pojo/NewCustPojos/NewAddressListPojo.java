package com.adopt.apigw.pojo.NewCustPojos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewAddressListPojo {
    private Integer id;
    private String version;
    @NotNull(message = "Please select addresstype")
    private String addressType;
    private Integer areaId;
    private Integer pincodeId;
    private String fullAddress;
    @NotNull(message = "Please enter landmark")
    private String landmark;
    private String buildingNumber;
    private String createdByName;
    private String cityName;
    private String stateName;
    private String pincode;

    public NewAddressListPojo(Integer id, String version, String addressType, Integer areaId, Integer pincodeId, String landmark,
                              String buildingNumber, String createdByName,String cityName, String stateName, String pincode) {
        this.id = id;
        this.version = version;
        this.addressType = addressType;
        this.areaId = areaId;
        this.pincodeId = pincodeId;
//        this.fullAddress = fullAddress; // this field is transient in table
        this.landmark = landmark;
        this.buildingNumber = buildingNumber;
        this.createdByName = createdByName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.pincode = pincode;
    }
}

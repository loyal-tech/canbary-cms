package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateServiceAreaSharedDataMessage {


    private Long id;

    private String name;

    private String status;


    private Boolean isDeleted = false;


//    private List<NetworkDevices> networkDevicesList = new ArrayList<>();


    private Integer mvnoId;


    private String latitude;


    private String longitude;


    private Long areaId;


    private List<Pincode> pincodeList = new ArrayList<>();


    private Long cityid;

    private Integer createdById;

    private Integer updatedById;
    private String createdByName;
    private String lastModifiedByName;
    private String siteName;
}

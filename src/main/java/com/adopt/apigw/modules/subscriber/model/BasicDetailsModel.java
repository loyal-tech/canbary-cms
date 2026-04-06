package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.modules.CommonList.model.CommonListDTO;

@Data
public class BasicDetailsModel {
    private List<CommonListDTO> titleList;
    private String title;
    private String name;
    private String contactPerson;
    private String aadharNumber;
    private String gst;
    private String pan;
    private String remarks;
}

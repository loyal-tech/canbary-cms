package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.modules.CommonList.model.CommonListDTO;

@Data
public class StatusModel {
    private Integer custId;
    private CommonListDTO currentStatus;
    private List<CommonListDTO> changedStatus;
}

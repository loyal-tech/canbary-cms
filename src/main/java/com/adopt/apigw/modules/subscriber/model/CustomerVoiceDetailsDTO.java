package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class CustomerVoiceDetailsDTO extends UpdateAbstarctDTO {
    private Integer id;
    private String voicesrvtype;
    private String didno;
    private String childdidno;
    private String intercomno;
    private String intercomgrp;
    private String remarks;
    private List<CommonListDTO> commonListVoiceService;
    private List<CommonListDTO> commonListInercomGroup;
}

package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.DiscountMapping;
import com.adopt.apigw.model.postpaid.DiscountPlanMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class UpdateDiscountSharedMessage {
    private Integer id;

    private String name;

    private String desc;

    private String status;

    private Integer mvnoId;

    List<DiscountMapping> discMappingList = new ArrayList<>();


    List<DiscountPlanMapping> planMappingList = new ArrayList<>();

    private Boolean isDelete = false;

    private Long buId;
}

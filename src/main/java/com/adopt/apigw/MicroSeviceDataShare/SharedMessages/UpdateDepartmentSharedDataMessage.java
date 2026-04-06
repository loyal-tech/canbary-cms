package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

import java.util.List;

@Data
public class UpdateDepartmentSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

    private Boolean isDelete;
    private Integer mvnoId;
    private List<Integer> planIds;
}

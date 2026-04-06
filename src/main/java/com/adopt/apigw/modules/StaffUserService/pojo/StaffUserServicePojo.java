package com.adopt.apigw.modules.StaffUserService.pojo;

import lombok.Data;

@Data
public class StaffUserServicePojo {
    private Long id;
    private String prefix;
    private Long fromreceiptnumber;
    private Long toreceiptnumber;
    private int stfmappingId;
    private Boolean isActive = true;
    private Boolean isDeleted = false;
}

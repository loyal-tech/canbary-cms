package com.adopt.apigw.modules.CustomerDBR.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerDBRPojo {

    private Double dbr;
    private Double pendingamt;
    private LocalDate startdate;
    private String Month;
    private LocalDate date;
    private Double cumm_revenue;
    private String remark;
    private Boolean isContainsMultipleService;
    private String serviceName;
}

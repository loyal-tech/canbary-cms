package com.adopt.apigw.model.postpaid;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerLedgerAllInfoPojo {
    private Integer custId;
    private String username;
    private String custname;
    private String plan;
    private String address;
    private String zonename;
    private LocalDate lastrenewDate;
    private Long duration;
    private String status;
    private CustomerLedgerInfoPojo customerLedgerInfoPojo;
}

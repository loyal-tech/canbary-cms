package com.adopt.apigw.pojo;

import com.adopt.apigw.modules.CustomerDBR.pojo.CustomerDBRPojo;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDBRResponse {
    public List<CustomerDBRPojo> customerDBRPojos;
    public Double outstandingPending;
    public Double outstandingDbr;
    public Double outstandingRevenue;
}

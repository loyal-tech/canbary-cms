package com.adopt.apigw.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CDATAPojo {

    private String serialNumber;

    private String manufacturer;

    private String model;

    private Integer customerId;    //this param is going to be used when we need to fetch all the services

    private String custUserName;

    private String custFirstName;

    private String password;

    private Integer custIdForReactivation;    // this param uses when the service is reactivated

    private Integer customerServiceMappingId;    //this param uses when the service is added or resume


}

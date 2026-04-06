package com.adopt.apigw.modules.Customers;

import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.pojo.api.CustNetworkDetailsDTO;
import lombok.Data;
import org.apache.commons.math3.ml.neuralnet.Network;

import java.util.List;

@Data
public class CustomerShowDTO {

    private Integer id;

    private String username;

    private String firstname;

    private String lastname;

    private String status;

    private String acctno;

    private String serviceareaname;

    private String serviceArea;
    private String mobile;

    private String name;

    private String email;

    private Boolean connectivity;

    private Double outstanding;

    private String custtype;

    private String calendarType;

    private String ConnectionMode;

    private CustNetworkDetailsDTO networkDetails;

    private boolean isinvoicestop;








}

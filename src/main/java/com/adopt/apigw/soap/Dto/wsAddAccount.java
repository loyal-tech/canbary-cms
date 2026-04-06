package com.adopt.apigw.soap.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class wsAddAccount {

    private String actionItem;
    private String requestId;
    private String userName;
    private String password;
    private String serviceId;
    private List<Item> item;

}




package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadStatusMessage {

    private Integer id;

    private Long leadId;

//    private String title;
//
//    private String username;
//
//    private String password;
//
//    private String firstname;
//
//    private String lastname;
    
    private String status;
    

}

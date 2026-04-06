package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerMessage {

    private Integer id;

    private String name;

    private String status;
    
    private Boolean isDelete;
}

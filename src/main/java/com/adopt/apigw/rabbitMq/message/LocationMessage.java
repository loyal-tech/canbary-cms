package com.adopt.apigw.rabbitMq.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationMessage {

    private Long locationMasterId;

    private String name;

    private String checkItem;

    private String status;

    private long mvnoId;

    private String locationIdentifyAttribute;

    private Timestamp lastmodifiedDate;

    private String locationIdentifyValue;



}

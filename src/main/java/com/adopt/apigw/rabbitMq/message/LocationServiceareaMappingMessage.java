package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationServiceareaMappingMessage {

    private Long id;

    private Long serviceAreaId;

    private Long locationId;


}

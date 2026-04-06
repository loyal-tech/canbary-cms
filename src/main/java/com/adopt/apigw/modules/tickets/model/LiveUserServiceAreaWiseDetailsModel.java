package com.adopt.apigw.modules.tickets.model;

import lombok.Data;

@Data
public class LiveUserServiceAreaWiseDetailsModel {

    private Long serviceAreaId;
    private Long serviceAreaCount;
    private Long oltId;
    private Long oltCount;
    private Long slotId;
    private Long slotCount;
}

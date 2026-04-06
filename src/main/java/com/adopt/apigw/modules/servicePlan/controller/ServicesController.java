package com.adopt.apigw.modules.servicePlan.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.modules.servicePlan.model.ServicesDTO;
import com.adopt.apigw.modules.servicePlan.service.ServicesService;

public class ServicesController  extends ExBaseAbstractController<ServicesDTO> {
    private static String MODULE = " [ServicesController] ";
    @Autowired
    private ServicesService service;

    public ServicesController(ServicesService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServicesController]";
    }

}

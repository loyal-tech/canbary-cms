package com.adopt.apigw.modules.linkacceptance.controller;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.adopt.apigw.modules.linkacceptance.service.LinkAcceptanceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.LINKACCEPTANCE)
public class LinkAcceptanceController extends ExBaseAbstractController2<LinkAcceptanceDTO> {
    public LinkAcceptanceController(LinkAcceptanceService linkAcceptanceService) {
        super(linkAcceptanceService);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

}

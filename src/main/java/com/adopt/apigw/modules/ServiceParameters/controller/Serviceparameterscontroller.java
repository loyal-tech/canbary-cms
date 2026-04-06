package com.adopt.apigw.modules.ServiceParameters.controller;

import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Region.model.RegionDTO;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.modules.ServiceParameters.model.ServiceParametersDTO;
import com.adopt.apigw.modules.ServiceParameters.service.ServiceParametersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.SERVICE_PARAMETERS)
public class Serviceparameterscontroller extends ExBaseAbstractController2<ServiceParametersDTO> {
    public Serviceparameterscontroller(ServiceParametersService service) {
        super(service);
    }
    private static final Logger logger = LoggerFactory.getLogger(Serviceparameterscontroller.class);
    private static String SUBMODULE = " [Serviceparameterscontroller] ";
    @Autowired
    ServiceParametersService serviceParametersService;

    //@PreAuthorize("validatePermission(\"" + MenuConstants.SERVICE + "\")")
    @GetMapping("/all")
    public GenericDataDTO getAllWithoutPagination (@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getAllWithoutPagination()] ";
        logger.info(getModuleNameForLog() + "--" + "Fetching AllWithoutPagination .Data[" + SUBMODULE.toString() + "]");
        try {
            List<ServiceParameter> list = serviceParametersService.findall();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        }

        return genericDataDTO;

    }

//    @Override
    public String getModuleNameForLog() {
        return "[Serviceparameterscontroller]";
    }
}

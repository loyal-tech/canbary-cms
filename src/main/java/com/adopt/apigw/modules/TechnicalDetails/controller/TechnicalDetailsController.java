package com.adopt.apigw.modules.TechnicalDetails.controller;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.SubBusinessVertical.Controller.SubBusinessVerticalController;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.adopt.apigw.modules.TechnicalDetails.domain.TechnicalDetails;
import com.adopt.apigw.modules.TechnicalDetails.model.TechnicalDetailsDto;
import com.adopt.apigw.modules.TechnicalDetails.repository.TechnicalDetailsRepository;
import com.adopt.apigw.modules.TechnicalDetails.service.TechnicalDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TECHNICAL_DETAILS)
public class TechnicalDetailsController extends ExBaseAbstractController2<TechnicalDetailsDto> {
    public TechnicalDetailsController(TechnicalDetailsService service) {
        super(service);
    }

    @Autowired
    TechnicalDetailsService technicalDetailsService;

    private static final Logger logger= LoggerFactory.getLogger(TechnicalDetailsController.class);

    @Override
    public GenericDataDTO getAllWithoutPagination (Integer mvnoId) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<TechnicalDetails> list = technicalDetailsService.getAll();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}

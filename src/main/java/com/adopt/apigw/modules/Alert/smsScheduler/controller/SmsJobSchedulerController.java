package com.adopt.apigw.modules.Alert.smsScheduler.controller;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Alert.smsScheduler.SchedularDTO.SmsSchedulerDTO;
import com.adopt.apigw.modules.Alert.smsScheduler.service.SmsSchedulerService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class SmsJobSchedulerController {
    private static final Logger logger = LoggerFactory.getLogger(SmsJobSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SmsSchedulerService smsSchedulerService;

    @PostMapping("/scheduleSms")
    public GenericDataDTO scheduleSms(@Valid @RequestBody List<SmsSchedulerDTO> scheduleSmsRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<SmsSchedulerDTO> list = smsSchedulerService.scheduleSms(scheduleSmsRequest);
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }
}

package com.adopt.apigw.modules.Alert.smsScheduler.job;

import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.adopt.apigw.modules.Alert.communication.service.CommunicationService;
import com.adopt.apigw.modules.Alert.smsScheduler.SchedularDTO.SmsSchedulerDTO;
import com.adopt.apigw.modules.Alert.smsScheduler.service.SmsSchedulerService;

@Component
public class SmsJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(SmsJob.class);

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    SmsSchedulerService smsSchedulerService;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String destination = jobDataMap.getString("destination");
        String source = jobDataMap.getString("source");
        String message = jobDataMap.getString("message");
        String templateId = jobDataMap.getString("templateId");
        communicationService.sendSMS(destination,message,source,templateId);
    }
}

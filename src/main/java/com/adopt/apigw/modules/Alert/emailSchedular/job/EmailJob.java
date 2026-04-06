package com.adopt.apigw.modules.Alert.emailSchedular.job;

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
import com.adopt.apigw.modules.Alert.emailSchedular.SchedularDTO.SchedulerDTO;
import com.adopt.apigw.modules.Alert.emailSchedular.service.SchedulerService;

@Component
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    SchedulerService schedulerService;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("email");
        SchedulerDTO scheduler=schedulerService.getSchedulaerByJobId(jobExecutionContext.getJobDetail().getKey().getName().toString());
        scheduler.setStatus(true);
        SchedulerDTO tempScheduler = schedulerService.saveEntity(scheduler);
        communicationService.sendMail(recipientEmail,body,subject,tempScheduler);
    }
}

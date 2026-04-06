package com.adopt.apigw.modules.Alert.smsScheduler.service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Alert.emailSchedular.SchedularDTO.SchedulerDTO;
import com.adopt.apigw.modules.Alert.smsScheduler.SchedularDTO.SmsSchedulerDTO;
import com.adopt.apigw.modules.Alert.smsScheduler.domain.SmsScheduler;
import com.adopt.apigw.modules.Alert.smsScheduler.job.SmsJob;
import com.adopt.apigw.modules.Alert.smsScheduler.mapper.SmsSchedularMapper;
import com.adopt.apigw.modules.Alert.smsScheduler.repository.SmsSchedularRepository;
import com.adopt.apigw.modules.Notification.domain.Notification;
import com.adopt.apigw.modules.Notification.mapper.NotificationMapper;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.repository.NotificationRepository;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.Template.model.TemplateDTO;
import com.adopt.apigw.modules.Template.service.TemplateService;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.mysema.commons.lang.URLEncoder;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SmsSchedulerService extends ExBaseAbstractService<SmsSchedulerDTO, SmsScheduler, Long> {

    @Autowired
    SmsSchedularRepository smsSchedularRepository;

    @Autowired
    SmsSchedularMapper smsSchedularMapper;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SmsSchedulerService smsSchedulerService;

    @Autowired
    NotificationService notificationService;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NotificationRepository notificationRepository;


    public SmsSchedulerService(SmsSchedularRepository repository, SmsSchedularMapper mapper) {
        super(repository, mapper);
        this.smsSchedularMapper =mapper;
        this.smsSchedularRepository =repository;
    }

    public SmsSchedulerDTO getSchedulaerByJobId(String jobId){
        return smsSchedularMapper.domainToDTO(smsSchedularRepository.findByJobId(jobId), new CycleAvoidingMappingContext());
    }

    public List<SmsSchedulerDTO> scheduleSms(List<SmsSchedulerDTO> scheduleSmsRequest){
        List<SmsSchedulerDTO> SmsResponseList= new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        now = now.plusMinutes(1l);
        try {
            List<LocalDateTime> tempDateList=new ArrayList<>();
            for(SmsSchedulerDTO sms:scheduleSmsRequest){
                sms.setScheduleTimeZone(ZoneId.of("Asia/Kolkata"));
                ZonedDateTime dateTime = ZonedDateTime.of(sms.getScheduleTime(), sms.getScheduleTimeZone());
                if(dateTime.isBefore(ZonedDateTime.now())) {
                    now = now.plusSeconds(2l);
                    dateTime = ZonedDateTime.of(now, sms.getScheduleTimeZone());
                    sms.setScheduleTime(now);
                }
                LocalDateTime tempFinalDateTime = sms.getScheduleTime();
                if(tempDateList.stream().anyMatch(ele->ele.isEqual(tempFinalDateTime))){
                    now = now.plusSeconds(2l);
                    dateTime = ZonedDateTime.of(now, sms.getScheduleTimeZone());
                    sms.setScheduleTime(now);
                }
                tempDateList.add(sms.getScheduleTime());
                JobDetail jobDetail = buildJobDetail(sms);
                Trigger trigger = buildJobTrigger(jobDetail, dateTime);
                scheduler.scheduleJob(jobDetail, trigger);
                SmsSchedulerDTO scheduleSmsResponse = new SmsSchedulerDTO(sms.getDestination(),sms.getSource(),sms.getScheduleTime(),false,
                        jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Sms Scheduled Successfully!",sms.getTemplateId());
                if(sms.getId()!=null){
                    scheduleSmsResponse.setId(sms.getId());
                    scheduleSmsResponse.setSendedAt(null);
                    scheduleSmsResponse.setError(null);
                }
                scheduleSmsResponse.setMessage(sms.getMessage());
                SmsSchedulerDTO smsSchedulerDTO = smsSchedulerService.saveEntity(scheduleSmsResponse);
                SmsResponseList.add(smsSchedulerDTO);
            }
        } catch (SchedulerException ex) {
            SmsSchedulerDTO scheduleSmsResponse = new SmsSchedulerDTO(false,
                    "Error scheduling Sms. Please try later!");
            SmsResponseList.add(scheduleSmsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SmsResponseList;
    }

    public void sendSMS(List<CustomersPojo> customers, Long notificationId) throws Exception {
        List<SmsSchedulerDTO> schedulerDTOList = new ArrayList<>();
        VelocityEngine ve  = new VelocityEngine();;
        VelocityContext context = new VelocityContext();;
        if(customers!=null && customers.size()>0) {
            NotificationDTO notificationDTO =notificationMapper.domainToDTO(notificationRepository.findById(notificationId).get(),new CycleAvoidingMappingContext());
            for (CustomersPojo customers1 : customers) {
                SmsSchedulerDTO schedulerDTO=new SmsSchedulerDTO();
                schedulerDTO.setDestination(customers1.getMobile());
                schedulerDTO.setJobGroup("broadcast");
                schedulerDTO.setScheduleTime(LocalDateTime.now());
                schedulerDTO.setSource("ADOPT");
                schedulerDTO.setTemplateId(notificationDTO.getTemplate_id());
                context.put("userName", customers1.getUsername());
                context.put("complainNo", "9999");
                context.put("password",customers1.getPassword());
                StringWriter writer = new StringWriter();
                ve.evaluate(context, writer, "", notificationDTO.getSms_body());
                schedulerDTO.setMessage(writer.toString());
                schedulerDTOList.add(schedulerDTO);
            }
        }
        this.scheduleSms(schedulerDTOList);
    }

    public void previousSchedules(){
        List<SmsSchedulerDTO> smsSchedulerDTOList =new ArrayList<>();
        List<SmsScheduler> smsSchedulerList = smsSchedularRepository.findByStatusOrIsSended(false,false);
        for (SmsScheduler Sms: smsSchedulerList){
            if(Sms.getStatus()==false || Sms.getIsSended()==false){
                SmsSchedulerDTO smsSchedulerDTO = this.smsSchedularMapper.domainToDTO(Sms, new CycleAvoidingMappingContext());

                smsSchedulerDTOList.add(smsSchedulerDTO);
            }
        }
        this.scheduleSms(smsSchedulerDTOList);
    }

    private JobDetail buildJobDetail(SmsSchedulerDTO scheduleSmsRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("destination", scheduleSmsRequest.getDestination());
        jobDataMap.put("source", scheduleSmsRequest.getSource());
        jobDataMap.put("message", scheduleSmsRequest.getMessage());
        jobDataMap.put("templateId", scheduleSmsRequest.getTemplateId());

        return JobBuilder.newJob(SmsJob.class)
                .withIdentity(UUID.randomUUID().toString(), scheduleSmsRequest.getJobGroup())
                .withDescription("Send Sms Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "Sms-triggers")
                .withDescription("Send Sms Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}

package com.adopt.apigw.modules.Alert.emailSchedular.service;

import com.adopt.apigw.modules.Teams.mapper.TeamsMapper;
import com.adopt.apigw.modules.Template.mapper.TemplateMapper;
import com.adopt.apigw.modules.Template.repository.TemplateRepository;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Alert.emailSchedular.SchedularDTO.SchedulerDTO;
import com.adopt.apigw.modules.Alert.emailSchedular.domain.Scheduler;
import com.adopt.apigw.modules.Alert.emailSchedular.job.EmailJob;
import com.adopt.apigw.modules.Alert.emailSchedular.mapper.SchedularMapper;
import com.adopt.apigw.modules.Alert.emailSchedular.repository.SchedularRepository;
import com.adopt.apigw.modules.Template.model.TemplateDTO;
import com.adopt.apigw.modules.Template.service.TemplateService;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SchedulerService extends ExBaseAbstractService<SchedulerDTO, Scheduler, Long> {

    @Autowired
    SchedularRepository schedularRepository;

    @Autowired
    SchedularMapper schedularMapper;

    @Autowired
    private org.quartz.Scheduler scheduler;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    TemplateService templateService;
    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TemplateRepository templateRepository;


    public SchedulerService(SchedularRepository repository, SchedularMapper mapper) {
        super(repository, mapper);
        this.schedularMapper = mapper;
        this.schedularRepository = repository;
    }

    public SchedulerDTO getSchedulaerByJobId(String jobId) {
        return schedularMapper.domainToDTO(schedularRepository.findByJobId(jobId), new CycleAvoidingMappingContext());
    }

    public List<SchedulerDTO> scheduleEmail(List<SchedulerDTO> scheduleEmailRequest) {
        List<SchedulerDTO> emailResponseList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        now = now.plusMinutes(2l);
        try {
            List<LocalDateTime> tempDateList = new ArrayList<>();
            for (SchedulerDTO email : scheduleEmailRequest) {
                email.setScheduleTimeZone(ZoneId.of("Asia/Kolkata"));
                ZonedDateTime dateTime = ZonedDateTime.of(email.getScheduleTime(), email.getScheduleTimeZone());
                if (dateTime.isBefore(ZonedDateTime.now())) {
                    now = now.plusSeconds(2l);
                    dateTime = ZonedDateTime.of(now, email.getScheduleTimeZone());
                    email.setScheduleTime(now);
                }
                LocalDateTime tempFinalDateTime = email.getScheduleTime();
                if (tempDateList.stream().anyMatch(ele -> ele.isEqual(tempFinalDateTime))) {
                    now = now.plusSeconds(2l);
                    dateTime = ZonedDateTime.of(now, email.getScheduleTimeZone());
                    email.setScheduleTime(now);
                }
                tempDateList.add(email.getScheduleTime());
                JobDetail jobDetail = buildJobDetail(email);
                Trigger trigger = buildJobTrigger(jobDetail, dateTime);
                scheduler.scheduleJob(jobDetail, trigger);
                SchedulerDTO scheduleEmailResponse = new SchedulerDTO(email.getEmail(), email.getSubject(), email.getScheduleTime(), false,
                        jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
                if (email.getId() != null) {
                    scheduleEmailResponse.setId(email.getId());
                    scheduleEmailResponse.setSendedAt(null);
                    scheduleEmailResponse.setError(null);
                }
                scheduleEmailResponse.setBody(email.getBody());
                SchedulerDTO schedulerDTO = schedulerService.saveEntity(scheduleEmailResponse);
                emailResponseList.add(schedulerDTO);
            }
        } catch (SchedulerException ex) {
            SchedulerDTO scheduleEmailResponse = new SchedulerDTO(false,
                    "Error scheduling email. Please try later!");
            emailResponseList.add(scheduleEmailResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailResponseList;
    }

    public void sendEmail(List<Customers> customers,Long template_id,String subejct) throws Exception {
        List<SchedulerDTO> schedulerDTOList = new ArrayList<>();
        VelocityEngine ve  = new VelocityEngine();;
        VelocityContext context = new VelocityContext();;
        if(customers!=null && customers.size()>0) {

            TemplateDTO template = templateMapper.domainToDTO(templateRepository.findById(template_id).get(),new CycleAvoidingMappingContext());
            for (Customers customers1 : customers) {
                SchedulerDTO schedulerDTO=new SchedulerDTO();
                schedulerDTO.setEmail(customers1.getEmail());
                schedulerDTO.setJobGroup("broadcast");
                schedulerDTO.setScheduleTime(LocalDateTime.now());
                schedulerDTO.setSubject(subejct);
                if (template_id == 1) {
                    context.put("username", customers1.getUsername());
                    StringWriter writer = new StringWriter();
                    ve.evaluate(context, writer, "", template.getFile());
                    schedulerDTO.setBody(writer.toString());
                }
                schedulerDTOList.add(schedulerDTO);
            }
        }
        this.scheduleEmail(schedulerDTOList);
    }

    public void previousSchedules() {
        List<SchedulerDTO> schedulerDTOList = new ArrayList<>();
        List<Scheduler> schedulerList = schedularRepository.findByStatusOrIsSended(false, false);
        for (Scheduler email : schedulerList) {
            System.out.println(email.toString());
            if (email.getStatus() == false || email.getIsSended() == false) {
                SchedulerDTO schedulerDTO = this.schedularMapper.domainToDTO(email, new CycleAvoidingMappingContext());
                schedulerDTOList.add(schedulerDTO);
            }
        }
        this.scheduleEmail(schedulerDTOList);
    }

    private JobDetail buildJobDetail(SchedulerDTO scheduleEmailRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), scheduleEmailRequest.getJobGroup())
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }
}

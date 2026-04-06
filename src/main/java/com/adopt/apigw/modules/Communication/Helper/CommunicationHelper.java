package com.adopt.apigw.modules.Communication.Helper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.Notification.domain.Notification;
import com.adopt.apigw.modules.Notification.mapper.NotificationMapper;
import com.adopt.apigw.modules.Notification.repository.NotificationRepository;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.adopt.apigw.modules.Alert.communication.config.EmailWorkerThread;
import com.adopt.apigw.modules.Alert.communication.config.SmsWorkerThread;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Repository.CommunicationRepository;
import com.adopt.apigw.modules.Communication.domain.Communication;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.Queing.CommunicationPoolExecutor;
import com.adopt.apigw.spring.SpringContext;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CommunicationHelper {

    private CommunicationPoolExecutor executor;
    private CommunicationRepository communicationRepository;
    private NotificationService notificationService;
    @Autowired
            private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;

    VelocityEngine ve = new VelocityEngine();
    VelocityContext context = new VelocityContext();
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

//    @Transactional
    public void generateCommunicationDetails(Long notificationId, List<Map<String, String>> mapList) throws Exception {
        this.notificationService = SpringContext.getBean(NotificationService.class);
        this.communicationRepository = SpringContext.getBean(CommunicationRepository.class);
        this.executor = SpringContext.getBean(CommunicationPoolExecutor.class);

        Notification notification = notificationRepository.findById(notificationId).get();
        NotificationDTO notificationDTO = notificationMapper.domainToDTO(notification,new CycleAvoidingMappingContext());
        if (notificationDTO == null) {
            throw new RuntimeException("Notification not found");
        }
        if (notificationDTO.getSms_enabled()) {
            List<Communication> communicationList = new ArrayList<>();
            mapList.forEach(data -> {
                Communication communication = new Communication();
                if (null != notificationDTO.getSms_body()) {
                    String smsMessage = this.populateVelocity(data, notificationDTO.getSms_body());
                    communication.setSmsMessage(smsMessage);
                }
                communication.setDestination(data.get(CommunicationConstant.DESTINATION));
                communication.setChannel(CommunicationConstant.COMMUNICATION_CHANNEL_SMS);
                communication.setTemplateId(notificationDTO.getTemplate_id());
                communication.setSource(CommunicationConstant.SOURCE);
                communication.setUuid(UUID.randomUUID().toString());
                communicationList.add(communication);
            });
            communicationRepository.saveAll(communicationList);
          //  this.createSmsThreads(communicationList);
        }
        if (notificationDTO.getEmail_enabled()) {
            List<Communication> communicationList = new ArrayList<>();
            mapList.forEach(data -> {
                Communication communication = new Communication();
                if (null != notificationDTO.getEmail_body()) {
                    String emailMessage = this.populateVelocity(data, notificationDTO.getEmail_body());
                    communication.setEmailBody(emailMessage);
                }
                communication.setEmail(data.get(CommunicationConstant.EMAIL));
                communication.setChannel(CommunicationConstant.COMMUNICATION_CHANNEL_EMAIL);
                communication.setSubject(notificationDTO.getName());
                communication.setIs_sended(false);
                communication.setUuid(UUID.randomUUID().toString());
                communicationList.add(communication);
            });
            communicationRepository.saveAll(communicationList);
           // this.createEmailThreads(communicationList);
        }
    }

    public String populateVelocity(Map<String, String> map, String body) {
        if (map.containsKey(CommunicationConstant.USERNAME)) {
            context.put(CommunicationConstant.USERNAME, map.get(CommunicationConstant.USERNAME));
        }
        if (map.containsKey(CommunicationConstant.COMPLAIN_NO)) {
            context.put(CommunicationConstant.COMPLAIN_NO, map.get(CommunicationConstant.COMPLAIN_NO));
        }
        if (map.containsKey(CommunicationConstant.PASSWORD)) {
            context.put(CommunicationConstant.PASSWORD, map.get(CommunicationConstant.PASSWORD));
        }
        if (map.containsKey(CommunicationConstant.CONTACT_NO)) {
            context.put(CommunicationConstant.CONTACT_NO, map.get(CommunicationConstant.CONTACT_NO));
        }
        if (map.containsKey(CommunicationConstant.WHATSAPP_NO_1)) {
            context.put(CommunicationConstant.WHATSAPP_NO_1, map.get(CommunicationConstant.WHATSAPP_NO_1));
        }
        if (map.containsKey(CommunicationConstant.WHATSAPP_NO_2)) {
            context.put(CommunicationConstant.WHATSAPP_NO_2, map.get(CommunicationConstant.WHATSAPP_NO_2));
        }
        if (map.containsKey(CommunicationConstant.CONTACT_EMAIL)) {
            context.put(CommunicationConstant.CONTACT_EMAIL, map.get(CommunicationConstant.CONTACT_EMAIL));
        }
        if (map.containsKey(CommunicationConstant.PLAN_NAME)) {
            context.put(CommunicationConstant.PLAN_NAME, map.get(CommunicationConstant.PLAN_NAME));
        }
        if (map.containsKey(CommunicationConstant.USAGE)) {
            context.put(CommunicationConstant.USAGE, map.get(CommunicationConstant.USAGE));
        }
        if (map.containsKey(CommunicationConstant.EXPIRY)) {
            context.put(CommunicationConstant.EXPIRY, map.get(CommunicationConstant.EXPIRY));
        }
        if (map.containsKey(CommunicationConstant.DATE)) {
            context.put(CommunicationConstant.DATE, map.get(CommunicationConstant.DATE));
        }
        if (map.containsKey(CommunicationConstant.GSTIN)) {
            context.put(CommunicationConstant.GSTIN, map.get(CommunicationConstant.GSTIN));
        }
        if (map.containsKey(CommunicationConstant.IP)) {
            context.put(CommunicationConstant.IP, map.get(CommunicationConstant.IP));
        }
        if (map.containsKey(CommunicationConstant.AMOUNT)) {
            context.put(CommunicationConstant.AMOUNT, map.get(CommunicationConstant.AMOUNT));
        }
        if (map.containsKey(CommunicationConstant.CHARGE_NAME)) {
            context.put(CommunicationConstant.CHARGE_NAME, map.get(CommunicationConstant.CHARGE_NAME));
        }
        if (map.containsKey(CommunicationConstant.REGISTRATION_DATE)) {
            context.put(CommunicationConstant.REGISTRATION_DATE, map.get(CommunicationConstant.REGISTRATION_DATE));
        }
        if (null != map && 0 < map.size()) {
            if (map.containsKey(CommunicationConstant.OTP)) {
                context.put(CommunicationConstant.OTP, map.get(CommunicationConstant.OTP));
            }

            StringWriter writer = new StringWriter();
            ve.evaluate(context, writer, "", body);
            return writer.toString();
        }
        return null;
    }

//    public void createSmsThreads(List<Communication> communicationList) {
//        communicationList.forEach(data -> {
//            executor.execute(new SmsWorkerThread(data, restTemplateBuilder));
//        });
//    }

//    public void createEmailThreads(List<Communication> communicationList) {
//        communicationList.forEach(data -> {
//            executor.execute(new EmailWorkerThread(data));
//        });
//    }
}

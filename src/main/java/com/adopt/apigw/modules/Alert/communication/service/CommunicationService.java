package com.adopt.apigw.modules.Alert.communication.service;


import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.service.common.ClientServiceSrv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Alert.communication.config.EmailConfig;
import com.adopt.apigw.modules.Alert.communication.config.EmailWorkerThread;
import com.adopt.apigw.modules.Alert.communication.config.SMSConfig;
import com.adopt.apigw.modules.Alert.emailSchedular.SchedularDTO.SchedulerDTO;
import com.adopt.apigw.modules.Alert.emailSchedular.service.SchedulerService;
import com.adopt.apigw.modules.Alert.singeltonThreadPool.SMSCommunicationSingeltonThreadPool;

@Service
public class CommunicationService {
    private String MODULE = " [Communication Service] ";
    private static String successMessage = " [SENDED] ";
    private static String errorMessage = " [ERROR] ";
    @Autowired
    private SMSCommunicationSingeltonThreadPool singletonThreadPool;
    @Autowired
    private EmailConfig emailConfig;
    @Autowired
    SMSConfig smsConfig;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private SchedulerService schedulerService;
    RestTemplateBuilder restTemplateBuilder=new RestTemplateBuilder();

    public String sendMail(String to, String body, String subject, SchedulerDTO schedulerDTO) {
        String SUBMODULE = MODULE + " [sendMail()] ";
        String response = "";
        try {
                emailConfig.setTo(to);
                emailConfig.setBody(body);
                emailConfig.setSubject(subject);
                Integer mvnoId = schedulerDTO.getMvnoId();
                String defaultPort = clientServiceSrv.getValueByName(ClientServiceConstant.PORT,mvnoId);
                String defaultHost = clientServiceSrv.getValueByName(ClientServiceConstant.HOST,mvnoId);
                String defaultUserName = clientServiceSrv.getValueByName(ClientServiceConstant.USERNAME,mvnoId);
                String defaultPassword = clientServiceSrv.getValueByName(ClientServiceConstant.PASSWORD,mvnoId);
                //singletonThreadPool.addThreadToPool(new EmailWorkerThread(defaultPort, defaultHost, defaultUserName, defaultPassword, emailConfig,schedulerService,schedulerDTO));
                response = "Send done";

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            response = " [Error]";
        }
        return response;
    }

    public String sendSMS(String destination, String message, String source,String templateId) {
        String SUBMODULE = MODULE + " [sendMail()] ";
        String response = "";
        try {
            smsConfig.setSource(source);
            smsConfig.setDestination(destination);
            smsConfig.setMessage(message);
            smsConfig.setTemplateId(templateId);
            //singletonThreadPool.addThreadToPool(new SMSConfigThread(smsConfig,restTemplateBuilder));
            response = "Send done";

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            response = " [Error]";
        }
        return response;
    }
}

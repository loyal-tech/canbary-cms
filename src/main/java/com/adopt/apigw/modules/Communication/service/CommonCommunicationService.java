package com.adopt.apigw.modules.Communication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.Alert.communication.config.EmailWorkerThread;
import com.adopt.apigw.modules.Alert.communication.config.SmsWorkerThread;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Repository.CommunicationRepository;
import com.adopt.apigw.modules.Communication.domain.Communication;
import com.adopt.apigw.modules.Communication.dto.CommunicationDTO;
import com.adopt.apigw.modules.Communication.mapper.CommunicationMapper;
import com.adopt.apigw.modules.Queing.CommunicationPoolExecutor;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonCommunicationService extends ExBaseAbstractService<CommunicationDTO, Communication, Long> {

    @Autowired
    private CommunicationRepository communicationRepository;
    @Autowired
    private CommunicationPoolExecutor executor;
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    public CommonCommunicationService(CommunicationRepository repository, CommunicationMapper mapper) {
        super(repository, mapper);
    }

    public void previousSchedules() {
        List<Communication> tempCommunicationListEmail = new ArrayList<>();
        List<Communication> tempCommunicationListSMS = new ArrayList<>();
        List<Communication> communicationList = communicationRepository.getBySendedAndError();
        communicationList.forEach(data -> {
            if (data.getChannel().equalsIgnoreCase(CommunicationConstant.COMMUNICATION_CHANNEL_EMAIL)) {
                tempCommunicationListEmail.add(data);
            }
            if (data.getChannel().equalsIgnoreCase(CommunicationConstant.COMMUNICATION_CHANNEL_SMS)) {
                tempCommunicationListSMS.add(data);
            }
        });
      //  this.createEmailThreads(tempCommunicationListEmail);
      //  this.createSmsThreads(tempCommunicationListSMS);
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

    @Override
    public String getModuleNameForLog() {
        return "[Communication Service]";
    }
}

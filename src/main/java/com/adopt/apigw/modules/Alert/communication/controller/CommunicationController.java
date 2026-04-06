package com.adopt.apigw.modules.Alert.communication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Alert.communication.service.CommunicationService;
import com.adopt.apigw.modules.Alert.singeltonThreadPool.SMSCommunicationSingeltonThreadPool;

@RestController
@RequestMapping(path = "/api/v1/sms")
public class CommunicationController {

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private SMSCommunicationSingeltonThreadPool threadPool;

    @GetMapping("/getActiveThreads")
    public String getActiveThread() {
        while (true) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ApplicationLogger.logger.debug("SEND SMS --> " + threadPool.getActiveThreads());
                }
            });
            return threadPool.getActiveThreads();
        }
    }
}

package com.adopt.apigw.service.common;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.PaymentLinkMessage;
import com.adopt.apigw.rabbitMq.message.PaymentSuccess;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.spring.LoggedInUser;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerCafAssignmentService {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private SubscriberService subscriberService;

    private void sendCustPaymentLinkMessage(String message, String customerName, Double paymentAmount, String url, Integer mvnoId, String countryCode, String mobileNumber, String emailId,Long staffId) {
        try {
            {
                String currencySymbol = String.valueOf(clientServiceRepository.findByNameAndMvnoId("CURRENCY_SYMBOL",mvnoId));
                if (!currencySymbol.isEmpty()) {
                    Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_PAYMENT_LINK);
                    if (optionalTemplate.isPresent()) {
                        if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                            // Set message in queue to send notification after opt generated successfully.
                            Long buId = null;
                            if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
                                buId = getBUIdsFromCurrentStaff().get(0);
                            }
                            PaymentLinkMessage paymentLinkMessage = new PaymentLinkMessage(message, customerName, currencySymbol, paymentAmount, url, mvnoId, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, countryCode, mobileNumber, emailId,buId,staffId);
                            Gson gson = new Gson();
                            gson.toJson(paymentLinkMessage);
                            kafkaMessageSender.send(new KafkaMessageData(paymentLinkMessage,PaymentLinkMessage.class.getSimpleName()));
//                            messageSender.send(paymentLinkMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendCustPaymentSuccessMessage(String message, String customerName, Double paymentAmount, String paymentMode, String url, Integer mvnoId, String countryCode, String mobileNumber, String emailId, Integer userId, String reciptNo, String paymentDate,Long staffId) {
        try {
            {
                String currencySymbol = String.valueOf(RabbitMqConstants.CURRENCY_SYMBOLE);
                Customers customers=subscriberService.get(userId,mvnoId);
                Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_PAYMENT_SUCCESS);
                if (optionalTemplate.isPresent()) {
                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                        // Set message in queue to send notification after opt generated successfully.
                        Long buId = null;
                        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
                            buId = getBUIdsFromCurrentStaff().get(0);
                        }
                        PaymentSuccess paymentSuccessMessage = new PaymentSuccess(message, customerName, currencySymbol, paymentAmount, paymentMode, mvnoId, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, countryCode, mobileNumber, emailId, userId, reciptNo, paymentDate,buId,null,null,staffId,customers,null,null);
                        Gson gson = new Gson();
                        gson.toJson(paymentSuccessMessage);
                        kafkaMessageSender.send(new KafkaMessageData(paymentSuccessMessage,PaymentSuccess.class.getSimpleName()));
//                        messageSender.send(paymentSuccessMessage, RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public List<Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }
}

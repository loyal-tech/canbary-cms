package com.adopt.apigw.modules.PaymentConfig.service;


import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.PaymentConfig.entity.PaymentConfig;
import com.adopt.apigw.modules.PaymentConfig.model.ChangeStatusDTO;
import com.adopt.apigw.modules.PaymentConfig.model.PaymentConfigDTO;
import com.adopt.apigw.modules.PaymentConfig.model.SendPaymentConfigDTO;
import com.adopt.apigw.modules.PaymentConfig.repository.PaymentConfigRepository;
import com.adopt.apigw.modules.PaymentConfigMapping.entity.PaymentConfigMapping;
import com.adopt.apigw.modules.PaymentConfigMapping.repository.PaymentConfigMappingRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.PaymentConfigMessage;
import com.adopt.apigw.service.common.CaptivePortalCustomerService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentConfigService {

    @Autowired
    private PaymentConfigRepository paymentConfigRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private PaymentConfigMappingRepository paymentConfigMappingRepository;


    private static final Logger log = LoggerFactory.getLogger(PaymentConfigMapping.class);

    /**@Author Dhaval Khalasi
     * This is Index function that handle
     * CRUD of payment gateway config
     * **/
    public  void handleRecievePaymentConfig(PaymentConfigMessage message){
        try {
            log.info("Payment config message come in cms");
            if (message.getFlag().equalsIgnoreCase(CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.CREATE)) {
                log.info("Payment config message with CREATE flag");
                savePaymentConfig(message);
            }
            if (message.getFlag().equalsIgnoreCase(CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.UPDATE)) {
                log.info("Payment config message with UPDATE flag");
                updatePaymentConfig(message);
            }
            if (message.getFlag().equalsIgnoreCase(CommonConstants.PAYMENT_CONFIG_RABBITMQ_FLAG.DELETE)) {
                log.info("Payment config message with DELETE flag");
                deletePaymentConfig(message);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    /**@Author Dhaval Khalasi
     * This is method for save config
     * **/
    public void savePaymentConfig(PaymentConfigMessage message){
       PaymentConfig paymentConfig = DtoToDomain(message.getPaymentConfigDTO());
       paymentConfigRepository.save(paymentConfig);
       paymentConfigMappingRepository.saveAll(paymentConfig.getPaymentConfigMappingList());
    }
    /**@Author Dhaval Khalasi
     * This is method for update payment gateway config
     * **/
    public void updatePaymentConfig(PaymentConfigMessage message){
        SendPaymentConfigDTO paymentConfigDTO = message.getPaymentConfigDTO();
        Optional<PaymentConfig> paymentConfig = paymentConfigRepository.findById(paymentConfigDTO.getPaymentConfigId());
        if(paymentConfig.isPresent()){
            log.info("payment config is found so first delete old record and then save new record");
            paymentConfigRepository.delete(paymentConfig.get());
            paymentConfigMappingRepository.deleteInBatch(paymentConfig.get().getPaymentConfigMappingList());
            PaymentConfig newPaymentConfig = DtoToDomain(paymentConfigDTO);
            paymentConfigRepository.save(newPaymentConfig);
            paymentConfigMappingRepository.saveAll(newPaymentConfig.getPaymentConfigMappingList());

        }
        else{
            log.info("payment config is not found so save new record");
            PaymentConfig savingPaymentConfig = DtoToDomain(paymentConfigDTO);
            paymentConfigRepository.save(savingPaymentConfig);
            paymentConfigMappingRepository.saveAll(savingPaymentConfig.getPaymentConfigMappingList());
        }
    }

    /**@Author Dhaval Khalasi
     * This is method for delete payment config
     * **/
    public void deletePaymentConfig(PaymentConfigMessage message){
        SendPaymentConfigDTO paymentConfigDTO = message.getPaymentConfigDTO();
        Optional<PaymentConfig> paymentConfig = paymentConfigRepository.findById(paymentConfigDTO.getPaymentConfigId());
        if(paymentConfig.isPresent()){
            log.info("payment config is delete successfully");
            paymentConfigRepository.delete(paymentConfig.get());
            paymentConfigMappingRepository.deleteInBatch(paymentConfig.get().getPaymentConfigMappingList());
        }
        else{
            log.info("payment config is not found");
        }
    }
    /**@Author Dhaval Khalasi
     * This is method convert dto to entity
     * **/
    public  PaymentConfig DtoToDomain(SendPaymentConfigDTO paymentConfigDTO){
        PaymentConfig paymentConfig = new PaymentConfig();
        if(paymentConfigDTO.getPaymentConfigId() != null){
            paymentConfig.setPaymentConfigId(paymentConfigDTO.getPaymentConfigId());
        }
        paymentConfig.setPaymentConfigName(paymentConfigDTO.getPaymentConfigName());
        paymentConfig.setPaymentConfigMappingList(paymentConfigDTO.getPaymentConfigMappingList());
        paymentConfig.setMvnoId(paymentConfigDTO.getMvnoId());
        paymentConfig.setCreateDate(LocalDateTime.now());
        if(paymentConfigDTO.getIsDelete() == null){
            paymentConfig.setIsDelete(false);
        }
        else{
            paymentConfig.setIsDelete(paymentConfigDTO.getIsDelete());
        }
        if(paymentConfigDTO.getIsActive() != null){
            paymentConfig.setIsActive(paymentConfigDTO.getIsActive());
        }
        else{
            paymentConfig.setIsActive(false);
        }
        return  paymentConfig;

    }
    /**@Author
     * Dhaval Khalasi
     * This will be get payment gateway parameter list in key value
     * **/
    public HashMap<String , String> getPaymentGatewayParameter(String paymentGatewayName , Integer mvnoId){
        HashMap<String , String> parameterByPaymentGatewayName = new HashMap<>();
        List<PaymentConfig> paymentConfigList = paymentConfigRepository.findAllByPaymentConfigNameEqualsIgnoreCaseAndMvnoId(paymentGatewayName , mvnoId.longValue());
        if(!paymentConfigList.isEmpty()){
           PaymentConfig paymentConfig1 = paymentConfigList.get(0);
           for(PaymentConfigMapping paymentConfigMapping : paymentConfig1.getPaymentConfigMappingList()){
               parameterByPaymentGatewayName.put(paymentConfigMapping.getPaymentParameterName() , paymentConfigMapping.getPaymentParameterValue());
           }

        }
        else{
            throw new RuntimeException("No Payment Gateway Configuration found with name and mvnoid");
        }
        return parameterByPaymentGatewayName;

    }






}

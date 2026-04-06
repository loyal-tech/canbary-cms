package com.adopt.apigw.modules.CustomerMacMgmt.Service;


import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.QCustMacMappping;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CustMacMappingMessage;
import com.adopt.apigw.repository.postpaid.CustMacMapppingRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CacheService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CustMacMgmtService {
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;
    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CacheService cacheService;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);


    public GenericDataDTO saveMacMapping(List<CustMacMappping> custMacMappping){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            List<CustMacMappping> savedMacMapping = new ArrayList<>();
//            Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(custMacMappping.get(0).getCustomer().getId());
            List<String> macList = custMacMappping.stream().map(CustMacMappping::getMacAddress).collect(Collectors.toList());
            boolean isExist = custMacMapppingRepository.existsByMacAddressInAndIsDeletedIsFalse(macList);
            if(isExist)
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Mac is already available and in-use, please use alternate mac",null);
            for(CustMacMappping currnetCustMacMappping : custMacMappping){
                if(custMacMappping.get(0).getCustomer() != null) {

//                    CustMacMappping savedCustmacmapping = custMacMapppingRepository.save(currnetCustMacMappping);
                    if(!currnetCustMacMappping.getIsDeleted()) {
                        savedMacMapping.add(currnetCustMacMappping);
                    }
//                    CustMacMappingMessage message = new CustMacMappingMessage(currnetCustMacMappping, custMacMappping.get(0).getCustomer().getMvnoId(),custMacMappping.get(0).getCustomer().getUsername(),currnetCustMacMappping.getCustsermappingid());
//                    kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
//                    executorService.submit(() -> kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName())));

                } else {
                    System.out.println("Customer not available");
                }
//                List<CustMacMappping> reservedCustMacMapping = custMacMapppingRepository.findByMacAddressAndIsDeletedIsFalseAndMacAddressIsNotNull(currnetCustMacMappping.getMacAddress());
//                if(reservedCustMacMapping != null && reservedCustMacMapping.size() > 0){
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Mac is already available and in-use, please use alternate mac",null);
//                }else{
//                    if(customers != null) {
//                        CustMacMappping savedCustmacmapping = custMacMapppingRepository.save(currnetCustMacMappping);
//                        savedMacMapping.add(savedCustmacmapping);
//                        CustMacMappingMessage message = new CustMacMappingMessage(currnetCustMacMappping, customers.getMvnoId(),customers.getUsername(),currnetCustMacMappping.getCustsermappingid());
//                        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
//                    } else {
//                        System.out.println("Customer not available");
//                    }
//                    if(customers!=null){
//                        if(!checkMaximumConcurrecyReached(customers, currnetCustMacMappping)){
//                            CustMacMappping savedCustmacmapping = custMacMapppingRepository.save(currnetCustMacMappping);
//                            savedMacMapping.add(savedCustmacmapping);
//                            CustMacMappingMessage message = new CustMacMappingMessage(currnetCustMacMappping, customers.getMvnoId(),customers.getUsername(),currnetCustMacMappping.getCustsermappingid());
//                            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
//                            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
//                        }else {
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Your concurrency is reached up to the limit",null);
//                        }
//                    }
//                }
            }
            List<CustMacMappping> savedEntities = custMacMapppingRepository.saveAll(savedMacMapping);
            savedEntities.forEach(savedCustMacMapping -> {
                CustMacMappingMessage message = new CustMacMappingMessage(
                        savedCustMacMapping,
                        savedCustMacMapping.getCustomer().getMvnoId(),
                        savedCustMacMapping.getCustomer().getUsername(),
                        savedCustMacMapping.getCustsermappingid()
                );
//                executorService.submit(() -> {
                    try {
                        kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
                    } catch (Exception e) {
                        System.err.println("Failed to send Kafka message for mapping ID: " + savedCustMacMapping.getCustsermappingid());
                        e.printStackTrace();
                    }
//                });
            });
            //add custmacmappingData in cache
            String cacheKey = cacheKeys.CUSTMACMAPPING + savedEntities.get(0).getId();
            cacheService.saveOrUpdateInCacheAsync(Collections.singletonList(savedEntities),cacheKey);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Saved Successfully");
//            List<CustMacMappping> custMacMapppings = custMacMapppingRepository.findByCustomerIdAndIsDeletedIsFalse(custMacMappping.get(0).getCustomer().getId());
            genericDataDTO.setDataList(savedEntities);
            return genericDataDTO;
        }catch (CustomValidationException e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Error while adding mac."+e.getMessage());
        }
        catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Error : "+HttpStatus.EXPECTATION_FAILED+" "+ e.getMessage());
        }
        return genericDataDTO;
    }



    public GenericDataDTO updateMacMapping(CustMacMappping custMacMappping){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            //first check if the macmapping is available or not to update
            CustMacMappping existedMacMapping = custMacMapppingRepository.findById(custMacMappping.getId()).orElse(null);

            if(existedMacMapping!=null){
                if(!existedMacMapping.getMacAddress().equalsIgnoreCase(custMacMappping.getMacAddress())){
                    List<CustMacMappping> updatedMacCheck = custMacMapppingRepository.findByMacAddressAndIsDeletedIsFalseAndMacAddressIsNotNull(custMacMappping.getMacAddress());
                    if(updatedMacCheck != null && updatedMacCheck.size() > 0){
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Mac is already available and in-use, please use alternate mac to update mac address",null);
                    }else{
                        Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(custMacMappping.getCustomer().getId());
                        if(customers!=null){
                            existedMacMapping.setMacAddress(custMacMappping.getMacAddress());
                            existedMacMapping.setCustomer(custMacMappping.getCustomer());
                            existedMacMapping.setCustsermappingid(custMacMappping.getCustsermappingid());
                            existedMacMapping.setDeleteFlag(custMacMappping.getDeleteFlag());
                            CustMacMappingMessage message = new CustMacMappingMessage(existedMacMapping, customers.getMvnoId(), customers.getUsername(),existedMacMapping.getCustsermappingid());
                            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
                            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
                            custMacMapppingRepository.save(existedMacMapping);
                            String cacheKey = cacheKeys.CUSTMACMAPPING + existedMacMapping.getId();
                            cacheService.saveOrUpdateInCacheAsync(Collections.singletonList(existedMacMapping),cacheKey);
                            genericDataDTO.setResponseCode(HttpStatus.OK.value());
                            genericDataDTO.setResponseMessage("Updated Successfully");
                            genericDataDTO.setData(existedMacMapping);
                        }
                        return genericDataDTO;
                    }
                }else{
                    Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(custMacMappping.getCustomer().getId());
                    if(customers!=null){
                        existedMacMapping.setMacAddress(custMacMappping.getMacAddress());
                        existedMacMapping.setCustomer(custMacMappping.getCustomer());
                        existedMacMapping.setCustsermappingid(custMacMappping.getCustsermappingid());
                        existedMacMapping.setDeleteFlag(custMacMappping.getDeleteFlag());
                        CustMacMappingMessage message = new CustMacMappingMessage(existedMacMapping, customers.getMvnoId(), customers.getUsername(),existedMacMapping.getCustsermappingid());
                        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
                        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));

                        custMacMapppingRepository.save(existedMacMapping);
                        String cacheKey = cacheKeys.CUSTMACMAPPING + existedMacMapping.getId();
                        cacheService.saveOrUpdateInCacheAsync(Collections.singletonList(existedMacMapping),cacheKey);
                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
                        genericDataDTO.setResponseMessage("Updated Successfully");
                        genericDataDTO.setData(existedMacMapping);
                    }
                    return genericDataDTO;
                }
            }
        }catch (CustomValidationException e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Error while adding mac."+e.getMessage());
        }
        catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Error : "+HttpStatus.EXPECTATION_FAILED+" "+ e.getMessage());
        }
        return genericDataDTO;
    }


    public GenericDataDTO deleteMacMapping(Integer custMacMappingId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            //first check if the macmapping is available or not to delete
            CustMacMappping existedMacMapping = custMacMapppingRepository.findById(custMacMappingId).orElse(null);
            if (existedMacMapping != null) {
                existedMacMapping.setDeleteFlag(true);
                CustMacMappingMessage message = new CustMacMappingMessage(existedMacMapping, existedMacMapping.getCustomer().getMvnoId(), existedMacMapping.getCustomer().getUsername(),existedMacMapping.getCustsermappingid());
                //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
                kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
//                custMacMapppingRepository.save(existedMacMapping);
                custMacMapppingRepository.deleteByCustomeridAndMacAddress(existedMacMapping.getCustomer().getId().longValue(), existedMacMapping.getMacAddress());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("deleted Successfully");
                genericDataDTO.setData(existedMacMapping);
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Error while adding mac." + e.getMessage());
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Error : " + HttpStatus.EXPECTATION_FAILED + " " + e.getMessage());
        }
        return null;
    }


    public GenericDataDTO findAllByCustId(Integer custId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<CustMacMappping> custMacMapppings = custMacMapppingRepository.findByCustomerIdAndIsDeletedIsFalse(custId);
            if(!custMacMapppings.isEmpty()){
                genericDataDTO.setDataList(custMacMapppings);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Records fetch Successfully");
            }else{
                genericDataDTO.setDataList(null);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("No Records Found !!");
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setDataList(null);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            ApplicationLogger.logger.error("Error while adding mac." + e.getMessage());
        } catch (Exception e) {
            genericDataDTO.setDataList(null);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            ApplicationLogger.logger.error("Error : " + HttpStatus.EXPECTATION_FAILED + " " + e.getMessage());
        }
        return genericDataDTO;
    }


    public boolean checkMaximumConcurrecyReached(Customers customers, CustMacMappping currnetCustMacMappping){
        Integer maxConcurrentSession = customers.getMaxconcurrentsession();
        List<CustMacMappping> custMacMapppings =  custMacMapppingRepository.findByCustomerIdAndIsDeletedIsFalse(customers.getId());
        if(maxConcurrentSession == null || maxConcurrentSession == 0) {
            Integer custsermappingid = currnetCustMacMappping.getCustsermappingid();
            List<String> maxSessions = custPlanMappingRepository.getMaxConcurrencybyPlanId(custsermappingid);
            if (maxSessions != null && !maxSessions.isEmpty()) {
                maxConcurrentSession = maxSessions.stream()
                        .map(Integer::valueOf)
                        .max(Integer::compareTo)
                        .orElse(1);
            } else {
                maxConcurrentSession = 1;
            }

        }
        if(maxConcurrentSession <= custMacMapppings.size()){
            return true;
        }else{
            return false;
        }
    }

    public List<CustMacMappping> getMacbyCustId(String mac, Customers customers) {
        return custMacMapppingRepository.findAllByNormalizedMacAndCustomer(normalizeMacAddress(mac), customers.getId());
    }

    public String normalizeMacAddress(String macAddress) {
        if(macAddress != null)
            return macAddress.replace(":", "").replace("-", "").replace(".", "");
        return macAddress;
    }

    public CustMacMappping getCustMacMappingByCustServiceMapping(Integer custsermappingid){
        try {
            CustMacMappping custMacMappping =custMacMapppingRepository.findByCustsermappingidAndAndIsDeletedFalse(custsermappingid);
            if(custMacMappping!=null){
                return  custMacMappping;
            }else{
                return null;
            }
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    public GenericDataDTO getMacCountForCustomer(Integer custId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<CustMacMappping> custMacMapppings = custMacMapppingRepository.findByCustomerIdAndIsDeletedIsFalse(custId);
            if(!custMacMapppings.isEmpty()){
                genericDataDTO.setData(custMacMapppings.size());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Customer MAC count fetched successfully");
            }else{
                genericDataDTO.setData(0);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("No MAC present with given customer");
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setDataList(null);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            ApplicationLogger.logger.error("Error while find MAC for given customer" + e.getMessage());
        } catch (Exception e) {
            genericDataDTO.setDataList(null);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            ApplicationLogger.logger.error("Error : " + HttpStatus.EXPECTATION_FAILED + " " + e.getMessage());
        }
        return genericDataDTO;
    }

    public boolean isMacExistsExceptCustomer(String mac, Integer custId) {
        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
        BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
        booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.eq(mac)).and(qCustMacMappping.customer.id.ne(custId)).and(qCustMacMappping.isDeleted.ne(true));
        return custMacMapppingRepository.exists(booleanExpression);
    }
}

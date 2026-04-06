package com.adopt.apigw.modules.Customers.Services;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustIpMapping;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustIPMessage;
import com.adopt.apigw.repository.common.CustIpMappingRepo;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CustIpMgmtService {


    @Autowired
    CustIpMappingRepo custIpMappingRepo;

    @Autowired
    MessageSender messageSender;


    @Autowired
    CustomersService customersService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;



    public List<CustIpMapping> getCustomerIps (Integer custIds){
        return custIpMappingRepo.getAllByCustid(custIds);
    }


    public List<CustIpMapping> update(List<CustIpMapping> custIpMappingList) {
        List<CustIpMapping> oldCustIpMappings = getCustomerIps(custIpMappingList.get(0).getCustid());
        custIpMappingList.forEach(custIpMapping -> {
            custIpMapping.setCreatedById(customersService.getLoggedInUserId());
            custIpMapping.setLastModifiedById(customersService.getLoggedInUserId());
            custIpMapping.setCreatedByName(customersService.getLoggedInUser().getFirstName());
            custIpMapping.setLastModifiedByName(customersService.getLoggedInUser().getFirstName());
        });
        if (!oldCustIpMappings.isEmpty()) {
            custIpMappingRepo.deleteInBatch(oldCustIpMappings);
            custIpMappingRepo.saveAll(custIpMappingList);
            CustIPMessage custIPMessage = new CustIPMessage(custIpMappingList);
            //messageSender.send(custIPMessage, RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_UPDATE_RADIUS_MESSAGE);
            kafkaMessageSender.send(new KafkaMessageData(custIPMessage,custIPMessage.getClass().getSimpleName(),"CUSTOMER_IP_TO_UPDATE_RADIUS"));
        } else {
            custIpMappingRepo.saveAll(custIpMappingList);
            CustIPMessage custIPMessage = new CustIPMessage(custIpMappingList);
            //messageSender.send(custIPMessage, RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_UPDATE_RADIUS_MESSAGE);
            kafkaMessageSender.send(new KafkaMessageData(custIPMessage,custIPMessage.getClass().getSimpleName(),"CUSTOMER_IP_TO_UPDATE_RADIUS"));
        }
        return custIpMappingList;
    }


    public String delete(Integer id){
        String deletedIp = "";
        try{
            CustIpMapping custIpMapping = custIpMappingRepo.findById(id).orElse(null);
            if(custIpMapping!=null){
                deletedIp = custIpMapping.getIpAddress();
                custIpMappingRepo.deleteById(id);
                ApplicationLogger.logger.info("IP : "+deletedIp+" deleted successfully");
                List<CustIpMapping> custIpMappings = new ArrayList<>();
                custIpMappings.add(custIpMapping);
                CustIPMessage custIPMessage = new CustIPMessage(custIpMappings);
                //messageSender.send(custIPMessage,RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_DELETE_RADIUS_MESSAGE);
                kafkaMessageSender.send(new KafkaMessageData(custIPMessage,custIPMessage.getClass().getSimpleName(),"CUSTOMER_IP_TO_DELETE_RADIUS"));
            }else{
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No Data Exist To Delete!!", null);
            }

        }catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error("Unable to delete Ip : "+deletedIp+" ", APIConstants.ERROR_TAG, ex.getStackTrace());
            throw ex;
        }
        return  deletedIp;
    }


    public boolean isIpExists(String ip,Integer custid) {
        if(custid != null)
            return custIpMappingRepo.existsByIpAddressAndCustidNot(ip, custid);
        return custIpMappingRepo.existsByIpAddressIn(Collections.singletonList(ip));
    }

    public List<CustIpMapping> save(List<CustIpMapping> custIpMappingList) {
        try {

            List<String> ipsToCheck = new ArrayList<>();
            ipsToCheck = custIpMappingList.stream().map(CustIpMapping::getIpAddress).collect(Collectors.toList());
//            List<CustIpMapping> custIpMappings = custIpMappingRepo.getAllByIpAddressInAndCustidNot(ipsToCheck,custIpMappingList.get(0).getCustid());
            List<String> existingIpAddresses = custIpMappingRepo.getIpAddressesByIpAddressInAndCustidNot(ipsToCheck, custIpMappingList.get(0).getCustid());
            if (existingIpAddresses.isEmpty()) {
                Integer createdById = customersService.getLoggedInUserId();

                custIpMappingList.forEach(custIpMapping -> {
                    custIpMapping.setCreatedById(createdById);
                    custIpMapping.setLastModifiedById(createdById);
                    custIpMapping.setCreatedByName(customersService.getLoggedInUser().getFirstName());
                    custIpMapping.setLastModifiedByName(customersService.getLoggedInUser().getFirstName());
                });
                custIpMappingRepo.saveAll(custIpMappingList);
                CustIPMessage custIPMessage = new CustIPMessage(custIpMappingList);
                //messageSender.send(custIPMessage, RabbitMqConstants.QUEUE_SEND_CUSTOMER_IP_TO_SAVE_RADIUS_MESSAGE);
                kafkaMessageSender.send(new KafkaMessageData(custIPMessage,custIPMessage.getClass().getSimpleName(),"CUSTOMER_IP_TO_SAVE_RADIUS"));
            } else {
               /* StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                for (CustIpMapping custIpMapping : existingIpAddresses) {
                    stringBuilder.append(custIpMapping.getIpAddress());
                    stringBuilder.append(", ");
                }*/
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(String.join(", ", existingIpAddresses));
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ips : " + stringBuilder + "is already is exit, Please try with diffrent Ip", null);
            }

        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error("Unable save Ips for the customer", APIConstants.ERROR_TAG, ex.getStackTrace());
            throw ex;
        }
        return custIpMappingList;
    }
}

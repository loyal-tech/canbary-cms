package com.adopt.apigw.service;


import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustPayDTOMessage;
import com.adopt.apigw.repository.common.CustomerPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Service
public class CustomerPaymentService {

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    PaymentGatewayService paymentGatewayService;


    @Autowired
    PartnerPaymentService partnerPaymentService;

    public void  saveCustomerPayment(CustPayDTOMessage customerPayment){
        try{
            if(customerPayment!=null){

                CustomerPayment newCustomerPayment = convertMessageToEntity(customerPayment);

                CustomerPayment savedCustomerPayment = customerPaymentRepository.save(newCustomerPayment);

                CustPayDTOMessage sendCustPayDTO = convertEntityToMessage(savedCustomerPayment);
                kafkaMessageSender.send(new KafkaMessageData(sendCustPayDTO,CustPayDTOMessage.class.getSimpleName()));
//                messageSender.send(sendCustPayDTO, RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void  updateCustomerPayment(CustPayDTOMessage customerPayment){
        try{
            CustomerPayment  savedCustomerPayment = customerPaymentRepository.findById(customerPayment.getId()).orElse(null);
            if(savedCustomerPayment!=null){
                savedCustomerPayment.setStatus(customerPayment.getStatus());
                savedCustomerPayment.setPaymentDate(getDate(customerPayment.getPaymentDate()));
                savedCustomerPayment.setTransactionDate(getDate(customerPayment.getTransactionDate()));
                CustomerPayment updatedCustomerPayment = customerPaymentRepository.save(savedCustomerPayment);
                CustPayDTOMessage updateCustPayDTOMessage = convertEntityToMessage(updatedCustomerPayment);
                kafkaMessageSender.send(new KafkaMessageData(updateCustPayDTOMessage,CustPayDTOMessage.class.getSimpleName()));
//                messageSender.send(updateCustPayDTOMessage, RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION);
                if(updatedCustomerPayment.getStatus().equalsIgnoreCase("Success")){
                    if(updatedCustomerPayment.getCustId()!=null){
                        paymentGatewayService.InitiateAddonOrRenewAfterPaymentSuccess(updatedCustomerPayment);
                    }else if(updatedCustomerPayment.getPartnerPaymentId()!=null){
                        partnerPaymentService.updatePartnerOnlinePayment(updatedCustomerPayment);
                    }
                }else if(updatedCustomerPayment.getStatus().equalsIgnoreCase("Failed")){
                    if(updatedCustomerPayment.getPartnerPaymentId()!=null){
                        partnerPaymentService.updatePartnerOnlinePayment(updatedCustomerPayment);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public CustomerPayment convertMessageToEntity(CustPayDTOMessage custPayDTOMessage){
        CustomerPayment payment = new CustomerPayment();

        payment.setId(custPayDTOMessage.getId());
        payment.setPayment(custPayDTOMessage.getPayment());
        payment.setCustId(custPayDTOMessage.getCustId());
        payment.setMvnoid(custPayDTOMessage.getMvnoid());
        payment.setMerchantName(custPayDTOMessage.getMerchantName());
        payment.setAccountNumber(custPayDTOMessage.getAccountNumber());
        payment.setStatus(custPayDTOMessage.getStatus());
        payment.setPaymentDate(getDate(custPayDTOMessage.getPaymentDate()));
        payment.setTransactionDate(getDate(custPayDTOMessage.getTransactionDate()));
        payment.setBuid(custPayDTOMessage.getBuid());
        payment.setCustomerUsername(custPayDTOMessage.getCustomerUsername());
        payment.setCreditDocumentId(custPayDTOMessage.getCreditDocumentId());
        payment.setPlanId(custPayDTOMessage.getPlanId());
        payment.setPaymentLink(custPayDTOMessage.getPaymentLink());
        payment.setOrderId(custPayDTOMessage.getOrderId());
        payment.setIsFromCaptive(custPayDTOMessage.getIsFromCaptive());
        payment.setPgTransactionId(custPayDTOMessage.getPgTransactionId());
        payment.setPaymentLink(custPayDTOMessage.getPaymentLink());
        payment.setChecksum(custPayDTOMessage.getChecksum());
        payment.setPartnerId(custPayDTOMessage.getPartnerId());
        payment.setPartnerPaymentId(custPayDTOMessage.getPartnerPaymentId());
        return payment;
    }

    public CustPayDTOMessage convertEntityToMessage(CustomerPayment customerPayment){
        CustPayDTOMessage payment = new CustPayDTOMessage();

        payment.setId(customerPayment.getId());
        payment.setPayment(customerPayment.getPayment());
        payment.setCustId(customerPayment.getCustId());
        payment.setMvnoid(customerPayment.getMvnoid());
        payment.setMerchantName(customerPayment.getMerchantName());
        payment.setAccountNumber(customerPayment.getAccountNumber());
        payment.setStatus(customerPayment.getStatus());
        payment.setPaymentDate(customerPayment.getPaymentDate().toString());
        payment.setTransactionDate(customerPayment.getTransactionDate().toString());
        payment.setBuid(customerPayment.getBuid());
        payment.setCustomerUsername(customerPayment.getCustomerUsername());
        payment.setCreditDocumentId(customerPayment.getCreditDocumentId());
        payment.setPlanId(customerPayment.getPlanId());
        payment.setPaymentLink(customerPayment.getPaymentLink());
        payment.setOrderId(customerPayment.getOrderId());
        payment.setIsFromCaptive(customerPayment.getIsFromCaptive());
        payment.setPgTransactionId(customerPayment.getPgTransactionId());
        payment.setPaymentLink(customerPayment.getPaymentLink());
        payment.setChecksum(customerPayment.getChecksum());
        payment.setPartnerId(customerPayment.getPartnerId());
        payment.setPartnerPaymentId(customerPayment.getPartnerPaymentId());
        return payment;
    }
    public LocalDateTime getDate(String dateString){
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        // Parse the string to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

        return dateTime;
    }
}

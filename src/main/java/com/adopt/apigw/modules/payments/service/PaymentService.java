package com.adopt.apigw.modules.payments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adopt.apigw.modules.payments.domain.Payment;
import com.adopt.apigw.modules.payments.model.PaymentCallback;
import com.adopt.apigw.modules.payments.model.PaymentDTO;
import com.adopt.apigw.modules.payments.model.PaymentStatus;
import com.adopt.apigw.modules.payments.repository.PaymentRepository;
import com.adopt.apigw.modules.placeOrder.Util.PaymentUtil;

import java.util.Date;
import java.util.List;

@Service
public class PaymentService {

        @Autowired
        private PaymentRepository paymentRepository;

        public PaymentDTO proceedPayment(PaymentDTO paymentDTO) {
            PaymentUtil paymentUtil = new PaymentUtil();
//            paymentDTO = paymentUtil.populatepaymentDTO(paymentDTO);
            savepaymentDTO(paymentDTO);
            return paymentDTO;
        }

        public String payuCallback(PaymentCallback paymentResponse) {
            String msg = "Transaction failed.";
            Payment payment = paymentRepository.findByTxnId(paymentResponse.getTxnid());
            if(payment != null) {
                PaymentStatus paymentStatus = null;
                if(paymentResponse.getStatus().equals("failure")){
                    paymentStatus = PaymentStatus.Failed;
                }else if(paymentResponse.getStatus().equals("success")) {
                    paymentStatus = PaymentStatus.Success;
                    msg = "Transaction success";
                }
                payment.setPaymentStatus(paymentStatus);
                payment.setMihpayId(paymentResponse.getMihpayid());
                payment.setMode(paymentResponse.getMode());
                paymentRepository.save(payment);
            }
            return msg;
        }

        private void savepaymentDTO(PaymentDTO paymentDTO) {
            Payment payment = new Payment();
            payment.setAmount(Double.parseDouble(paymentDTO.getAmount()));
            payment.setEmail(paymentDTO.getEmail());
            payment.setName(paymentDTO.getName());
            payment.setPaymentDate(new Date());
            payment.setPaymentStatus(PaymentStatus.Pending);
            payment.setPhone(paymentDTO.getPhone());
            payment.setProductInfo(paymentDTO.getProductInfo());
            payment.setTxnId(paymentDTO.getTxnId());
            payment.setCommand(paymentDTO.getCommand());
            paymentRepository.save(payment);
        }


    }
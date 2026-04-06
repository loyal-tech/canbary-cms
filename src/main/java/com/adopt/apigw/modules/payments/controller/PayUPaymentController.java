package com.adopt.apigw.modules.payments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.modules.payments.model.PaymentCallback;
import com.adopt.apigw.modules.payments.model.PaymentDTO;
import com.adopt.apigw.modules.payments.model.PaymentMode;
import com.adopt.apigw.modules.payments.service.PaymentService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/payu")
public class PayUPaymentController {
    private static String MODULE = " [PayUPaymentController] ";
    @Autowired
    private PaymentService paymentService;
    private static final String PAYMENT_FORM="payments/paymentView";
    private static final String VERIFY_PAYMENT="payments/verifypayment";

    @GetMapping(path = "/payment")
    public String viewPaymentPage() {
        return PAYMENT_FORM;
    }

    @GetMapping(path = "/verify-payment")
    public String verifyPayment(){
        return VERIFY_PAYMENT;
    }

    /*@GetMapping
    public String viewPaymentPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("paymentview");
        return model;
    }*/

    @PostMapping(path = "/payment-details")
    public @ResponseBody PaymentDTO proceedPayment(@RequestBody PaymentDTO paymentDTO){
        System.out.println("PaymentController.proceedPayment() : "+paymentDTO);
        PaymentDTO detail = paymentService.proceedPayment(paymentDTO);
        System.out.println("Data : "+detail);
        return detail;
    }

    @PostMapping(path = "/payment-response")
    public @ResponseBody String payuCallback(@RequestParam String mihpayid, @RequestParam String status, @RequestParam PaymentMode mode, @RequestParam String txnid, @RequestParam String hash){
        PaymentCallback paymentCallback = new PaymentCallback();
        paymentCallback.setMihpayid(mihpayid);
        paymentCallback.setTxnid(txnid);
        paymentCallback.setMode(mode);
        paymentCallback.setHash(hash);
        paymentCallback.setStatus(status);
        System.out.println("Data 2 : "+paymentService.payuCallback(paymentCallback));;
        return paymentService.payuCallback(paymentCallback);
    }
}

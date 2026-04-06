package com.adopt.apigw.modules.subscriber.service;

import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.service.common.CustomersService;

import java.util.HashSet;

public class InvoiceCreationThread implements Runnable {

    private CustomersPojo customersPojo;
    private CustomersService customersService;

    private Integer loggedInUserId;
    private String createdByName;

    private String updateByName;

    HashSet<Integer> oldDebitDocId;

    String creditDocumentId;

    String isFromFlutterWave;

    boolean isCAFCustomer = false;

    String paymentOwner;

    Integer paymentOwnerId;

    String invoiceType;

    Boolean isCancelRegenerate;

    Integer renewalId;

    String type;


    public InvoiceCreationThread(CustomersPojo customersPojo, CustomersService customersService,String invoiceType,Boolean isCancelRegenerate, Integer renewalId, String type) {
        this.customersPojo = customersPojo;
        this.customersService = customersService;
        this.loggedInUserId = customersService.getLoggedInUserId();
        this.invoiceType=invoiceType;
        this.renewalId = renewalId;
        this.type = type;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";//customersPojo.getCreatedByName();
            this.updateByName="";
        }
        if (customersPojo.getOldDebitDocId() != null) {
            this.oldDebitDocId = customersPojo.getOldDebitDocId();
        }
        if(customersPojo.getIsFromFlutterWave() != null){
            this.isFromFlutterWave = customersPojo.getIsFromFlutterWave();
        }
        if(customersPojo.getCreditDocumentId() != null){
            this.creditDocumentId = customersPojo.getCreditDocumentId();
        }
        if(customersPojo.getStatus().equalsIgnoreCase("NewActivation")) {
            this.isCAFCustomer = true;
        }
        if(customersPojo.getPaymentOwner() != null){
            this.paymentOwner = customersPojo.getPaymentOwner();
        }
        if (customersPojo.getPaymentOwnerId() !=null){
            this.paymentOwnerId = customersPojo.getPaymentOwnerId();
        }
        if(customersPojo.getCustPackageId() != null) {
            customersPojo.setCustPackageId(customersPojo.getCustPackageId());
        }
        this.isCancelRegenerate=isCancelRegenerate;
    }

    @Override
    public void run() {
        generateAndEmailInvoice(this.customersPojo);
    }

    public void generateAndEmailInvoice(CustomersPojo customersPojo) {
        try {

            if(customersPojo.getCreditDocumentId() != null && customersPojo.getIsFromFlutterWave() != null){
                customersService.generatePrepaidInvoiceForFlutterWave(customersPojo , customersPojo.getCustPackageId() , loggedInUserId , oldDebitDocId , createdByName,updateByName , customersPojo.getCreditDocumentId() , customersPojo.getIsFromFlutterWave());
            }
            else {
                customersService.generatePrepaidInvoice(customersPojo,customersPojo.getCustPackageId(), loggedInUserId, oldDebitDocId , createdByName,updateByName, isCAFCustomer, paymentOwner,paymentOwnerId,invoiceType,isCancelRegenerate, renewalId, type);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package com.adopt.apigw.modules.subscriber.service;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.service.common.CustomersService;

public class InvoiceThread implements Runnable {

    private String date;
    private Customers customersPojo;
    private CustomersService customersService;
    private String paymentOwner;

    private Integer loggedInUserId;

    private String createdByName;

    private String updateByName;

    private Integer paymentOwnerId;

    private String invoiceType;

    private Integer renewalId;

    public InvoiceThread(String date, Customers customers, CustomersService customersService , String paymentOwner,Integer paymentOwnerId,String invoiceType) {
        this.date = date;
        this.customersPojo = customers;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.customersService = customersService;
        this.paymentOwner = paymentOwner;
        this.invoiceType=invoiceType;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();//customersPojo.getCreatedByName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
            } else {
                    this.createdByName="";//customersPojo.getCreatedByName();
                    this.updateByName="";
                 }
        this.paymentOwnerId = paymentOwnerId;
    }


    public InvoiceThread(String date, Customers customers, CustomersService customersService , String paymentOwner,Integer paymentOwnerId,String invoiceType, Integer renewalId) {
        this.date = date;
        this.customersPojo = customers;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.customersService = customersService;
        this.paymentOwner = paymentOwner;
        this.invoiceType=invoiceType;
        this.renewalId = renewalId;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();//customersPojo.getCreatedByName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";//customersPojo.getCreatedByName();
            this.updateByName="";
        }
        this.paymentOwnerId = paymentOwnerId;
    }

    @Override
    public void run() {
        generateAndEmailInvoice(this.date, this.customersPojo,loggedInUserId , this.paymentOwner,this.paymentOwnerId,invoiceType, this.renewalId);
    }

    public void generateAndEmailInvoice(String date, Customers customers,Integer loggedInUserId ,  String paymentOwner,Integer paymentOwnerId,String invoiceType, Integer renewalId) {
        try {
            Thread.sleep(2000);
            ApplicationLogger.logger.info("..........................................Billings Started.................................................................");
            customersService.generateAndEmailInvoice(date, customers,loggedInUserId,paymentOwner,createdByName,updateByName,paymentOwnerId,invoiceType, renewalId);
            ApplicationLogger.logger.info("..........................................Billing Ended.................................................................");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

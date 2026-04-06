package com.adopt.apigw.modules.subscriber.service;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.service.common.CustomersService;

import java.util.HashSet;
import java.util.List;

public class ChargeThread implements Runnable {

    private List<Integer> custChargeIdList;
    private CustomersService customersService;

    private Integer custid;

    private Long inventoryMappingId;

    private Integer loggedInUserId;
    private String createdByName;

    private String updateByName;

    HashSet<Integer> oldDebitDocId;

    private Long inventoryItemId;

    private Long planId;
    private String itemCondition;

    private Boolean isCaf=false;

    private String paymentOwner;

    private Integer paymentOwnerId;

    private Boolean isCancelRegenerate;

    private String type;

    public ChargeThread(Integer custId, List<Integer> custChargeIdList, CustomersService customersService, Long inventoryMappingId, String paymentOwner,Integer paymentOwnerId, String type) {
        this.custid = custId;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.custChargeIdList = custChargeIdList;
        this.customersService = customersService;
        this.inventoryMappingId = inventoryMappingId;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";
            this.updateByName="";
        }
        if (paymentOwner !=null){
            this.paymentOwner = paymentOwner;
        }else {
            this.paymentOwner="";
        }

        if (paymentOwnerId !=null){
            this.paymentOwnerId = paymentOwnerId;
        }else {
            this.paymentOwnerId = -1;
        }
        this.type = type;
    }

    public ChargeThread(Integer custId, List<Integer> custChargeIdList, CustomersService customersService, Long inventoryMappingId, String paymentOwner,Integer paymentOwnerId) {
        this.custid = custId;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.custChargeIdList = custChargeIdList;
        this.customersService = customersService;
        this.inventoryMappingId = inventoryMappingId;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";
            this.updateByName="";
        }
        if (paymentOwner !=null){
            this.paymentOwner = paymentOwner;
        }else {
            this.paymentOwner="";
        }

        if (paymentOwnerId !=null){
            this.paymentOwnerId = paymentOwnerId;
        }else {
            this.paymentOwnerId = -1;
        }
    }

    public ChargeThread(Boolean isCaf,Integer custId, List<Integer> custChargeIdList, CustomersService customersService, Long inventoryMappingId, String paymentOwner, Integer paymentOwnerId,String type) {
        this.custid = custId;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.custChargeIdList = custChargeIdList;
        this.customersService = customersService;
        this.inventoryMappingId = inventoryMappingId;
        this.isCaf=isCaf;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";
            this.updateByName="";
        }
        if (paymentOwner !=null){
            this.paymentOwner = paymentOwner;
        }else {
            this.paymentOwner="";
        }
        if (paymentOwnerId !=null){
            this.paymentOwnerId = paymentOwnerId;
        }else {
            this.paymentOwnerId=-1;
        }
        this.type =type;
    }

    public ChargeThread(Integer custId, CustomersService customersService, Long inventoryMappingId, Long inventoryItemId, String itemCondition,Long planId,Integer paymentOwnerId) {
        this.custid = custId;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.customersService = customersService;
        this.inventoryMappingId = inventoryMappingId;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";
            this.updateByName="";
        }
        this.inventoryItemId = inventoryItemId;
        this.itemCondition = itemCondition;
        this.planId=planId;
        if (paymentOwnerId !=null){
            this.paymentOwnerId = paymentOwnerId;
        }else {
            this.paymentOwnerId=-1;
        }
    }

    public ChargeThread(Integer custId, List<Integer> custChargeIdList, CustomersService customersService, Long inventoryMappingId, HashSet<Integer> oldDebitDocId, Integer paymentOwnerId, boolean isCancelRegenerate, String type) {
        this.custid = custId;
        this.loggedInUserId=customersService.getLoggedInUserId();
        this.custChargeIdList = custChargeIdList;
        this.customersService = customersService;
        this.inventoryMappingId = inventoryMappingId;
        this.paymentOwnerId = paymentOwnerId;
        this.isCancelRegenerate = isCancelRegenerate;
        if(oldDebitDocId != null)
            this.oldDebitDocId = oldDebitDocId;
        if(customersService.getLoggedInUser() != null) {
            this.createdByName=customersService.getLoggedInUser().getFirstName();
            this.updateByName=customersService.getLoggedInUser().getFirstName();
        } else {
            this.createdByName="";
            this.updateByName="";
        }
        this.paymentOwnerId = paymentOwnerId;
        this.isCancelRegenerate = isCancelRegenerate;
        this.type = type;
    }

    @Override
    public void run() {

        billingProcess(this.custChargeIdList);

    }

    public void billingProcess(List<Integer> custChargeIdList) {
        try {
            if (inventoryMappingId > 0) {
                customersService.genrateBillrunForChargeForInventory(custid,loggedInUserId, inventoryMappingId, inventoryItemId, itemCondition, createdByName, updateByName,planId,paymentOwnerId);
            }
            else
            {
                String chargeIdList="(";
                for(int i=0;i<custChargeIdList.size();i++)
                {
                    chargeIdList+=custChargeIdList.get(i).toString();
                    if(i!=custChargeIdList.size()-1)
                        chargeIdList+=", ";
                }
                chargeIdList+=")";

                try {
                    Thread.sleep(2000);
                    if(oldDebitDocId != null)
                        customersService.genrateBillrunForChargeForInvoice(custid, custChargeIdList, oldDebitDocId,loggedInUserId,paymentOwner,paymentOwnerId,isCancelRegenerate,createdByName, type);
                    else {
                        if(!isCaf)
                            customersService.genrateBillrunForCharge(custid, custChargeIdList, loggedInUserId, createdByName, updateByName, paymentOwner,paymentOwnerId,isCancelRegenerate, type);
                        else
                            customersService.genrateBillrunForCharge(isCaf,custid, custChargeIdList, loggedInUserId, createdByName, updateByName,paymentOwner,paymentOwnerId,isCancelRegenerate, type);
                    }
                } catch (InterruptedException e) {
                    ApplicationLogger.logger.error("ChargeThread billingprocess " + e.getMessage(), e);
                    e.printStackTrace();
                } catch (Exception e) {
                    ApplicationLogger.logger.error("ChargeThread billingprocess " + e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("ChargeThread billingprocess " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}

package com.adopt.apigw.modules.custAccountProfile;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveCustAccountProfileSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateCustAccountProfileSharedDataMessage;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustAccountProfileService {

    @Autowired
    CustAccountProfileRepository custAccountProfileRepository;

    public void saveCustAccountProfileEntity(SaveCustAccountProfileSharedDataMessage saveCustAccountProfileSharedDataMessage) throws Exception {
        try {
            CustAccountProfile custAccountProfile = new CustAccountProfile();
            custAccountProfile.setId(saveCustAccountProfileSharedDataMessage.getId());
            custAccountProfile.setName(saveCustAccountProfileSharedDataMessage.getName());
            custAccountProfile.setPrefix(saveCustAccountProfileSharedDataMessage.getPrefix());
            custAccountProfile.setType(saveCustAccountProfileSharedDataMessage.getType());
            custAccountProfile.setStartFrom(saveCustAccountProfileSharedDataMessage.getStartFrom());
            custAccountProfile.setYear(saveCustAccountProfileSharedDataMessage.isYear());
            custAccountProfile.setMonth(saveCustAccountProfileSharedDataMessage.isMonth());
            custAccountProfile.setDay(saveCustAccountProfileSharedDataMessage.isDay());
            custAccountProfile.setStatus(saveCustAccountProfileSharedDataMessage.getStatus());
            custAccountProfile.setIsDelete(saveCustAccountProfileSharedDataMessage.isDelete());
            custAccountProfile.setMvnoId(saveCustAccountProfileSharedDataMessage.getMvnoId());
            custAccountProfile.setCreatedByName(saveCustAccountProfileSharedDataMessage.getCreatedByName());
            custAccountProfile.setLastModifiedByName(saveCustAccountProfileSharedDataMessage.getLastModifiedByName());
            custAccountProfile.setCreatedById(saveCustAccountProfileSharedDataMessage.getCreatedById());
            custAccountProfile.setLastModifiedById(saveCustAccountProfileSharedDataMessage.getLastModifiedById());

            custAccountProfileRepository.save(custAccountProfile);
            ApplicationLogger.logger.info("Cust Account Profile created successfully with name " + custAccountProfile.getName());
        }catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create Cust Account Profile with name " + saveCustAccountProfileSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void updateCustAccountProfileEntity(UpdateCustAccountProfileSharedDataMessage updateCustAccountProfileSharedDataMessage) throws Exception {
        try {
           CustAccountProfile custAccountProfile =  custAccountProfileRepository.findByProfileId(updateCustAccountProfileSharedDataMessage.getId()).orElseThrow(null);
           if(custAccountProfile!=null) {
               custAccountProfile.setId(updateCustAccountProfileSharedDataMessage.getId());
               custAccountProfile.setName(updateCustAccountProfileSharedDataMessage.getName());
               custAccountProfile.setPrefix(updateCustAccountProfileSharedDataMessage.getPrefix());
               custAccountProfile.setType(updateCustAccountProfileSharedDataMessage.getType());
               custAccountProfile.setStartFrom(updateCustAccountProfileSharedDataMessage.getStartFrom());
               custAccountProfile.setYear(updateCustAccountProfileSharedDataMessage.isYear());
               custAccountProfile.setMonth(updateCustAccountProfileSharedDataMessage.isMonth());
               custAccountProfile.setDay(updateCustAccountProfileSharedDataMessage.isDay());
               custAccountProfile.setStatus(updateCustAccountProfileSharedDataMessage.getStatus());
               custAccountProfile.setIsDelete(updateCustAccountProfileSharedDataMessage.isDelete());
               custAccountProfile.setMvnoId(updateCustAccountProfileSharedDataMessage.getMvnoId());
               custAccountProfile.setCreatedByName(updateCustAccountProfileSharedDataMessage.getCreatedByName());
               custAccountProfile.setLastModifiedByName(updateCustAccountProfileSharedDataMessage.getLastModifiedByName());
               custAccountProfile.setCreatedById(updateCustAccountProfileSharedDataMessage.getCreatedById());
               custAccountProfile.setLastModifiedById(updateCustAccountProfileSharedDataMessage.getLastModifiedById());

               custAccountProfileRepository.save(custAccountProfile);
           }
            ApplicationLogger.logger.info("Cust Account Profile updated successfully with name " + custAccountProfile.getName());
        }catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update Cust Account Profile with name " + updateCustAccountProfileSharedDataMessage.getName(), e.getMessage());
        }
    }
}

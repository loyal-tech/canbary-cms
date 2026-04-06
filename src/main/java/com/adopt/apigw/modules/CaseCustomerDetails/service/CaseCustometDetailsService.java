package com.adopt.apigw.modules.CaseCustomerDetails.service;


import com.adopt.apigw.modules.CaseCustomerDetails.model.CaseCustomerDetails;
import com.adopt.apigw.modules.CaseCustomerDetails.repository.CaseCustomerDetailsRepository;
import com.adopt.apigw.modules.tickets.model.CaseDTO;
import com.adopt.apigw.rabbitMq.message.CloseTicketCheckMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaseCustometDetailsService{

    @Autowired
    CaseCustomerDetailsRepository caseCustomerDetailsRepository;

    public void saveCaseCustomerDetails(CloseTicketCheckMessage closeTicketCheckMessage){
        CaseDTO caseDTO = new CaseDTO();
        Integer custId = null;

        custId = closeTicketCheckMessage.getCustomerId();

        CaseCustomerDetails caseCustomerDetails = new CaseCustomerDetails();

        caseCustomerDetails.setCustomerId(custId);
        caseCustomerDetails.setCaseNumber(closeTicketCheckMessage.getCaseNumber());
        caseCustomerDetails.setCaseStatus(closeTicketCheckMessage.getStatus());
        caseCustomerDetails.setCaseId(closeTicketCheckMessage.getCaseId());

        caseCustomerDetailsRepository.save(caseCustomerDetails);

    }


    public void updateCaseCustomerDetails(CloseTicketCheckMessage closeTicketCheckMessage){
        CaseDTO caseDTO = new CaseDTO();
        Integer custId = null;
        custId = closeTicketCheckMessage.getCustomerId();

        List<CaseCustomerDetails> caseCustomerDetailsList = new ArrayList<>();
        caseCustomerDetailsList = caseCustomerDetailsRepository.findByCaseId(closeTicketCheckMessage.getCaseId());
        if(caseCustomerDetailsList.size()>0){
            for(CaseCustomerDetails caseCustomerDetails : caseCustomerDetailsList){
                caseCustomerDetails.setCaseStatus(closeTicketCheckMessage.getStatus());
                caseCustomerDetailsRepository.save(caseCustomerDetails);
            }

        }




    }
}

package com.adopt.apigw.modules.InventoryManagement.CommonInterfaces;

import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InventoryClientService {


    @Autowired
    private final InventoryClient inventoryClient;

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    private CustomersRepository customersRepository;

    public InventoryClientService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }


    public String getManufacturerName(String Token, Integer customerId,String connectionNumber){
        return  inventoryClient.getManufacturerName(Token,customerId,connectionNumber);
    }
    public String getVerifiedManufacturerName(String token,Integer customerId, String connectionNumber){
        return getManufacturerName(token,customerId,connectionNumber);
    }



    public boolean getProductVarifiedWithCDATAManufacturer(String Token, Integer customerId,String connectionNumber, String manufacturerName){
        return  inventoryClient.getProductVarifiedWithCDATAManufacturer(Token,customerId,connectionNumber,manufacturerName);
    }
    public boolean verifyAndInitiateCdataCreateRequest(String token,Integer customerId, String connectionNumber){

        // get checked C-DATA manufacturer system configuration.
        // TODO: pass mvnoID manually 6/5/2025
        ClientService clientService = clientServiceRepository.getByNameAndMvnoId(CommonConstants.CDATA_CONSTANTS.CDATA_MANUFACTURER, getMvnoIdFromCurrentStaff(null));

        return getProductVarifiedWithCDATAManufacturer(token,customerId,connectionNumber,clientService.getValue());
    }


    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
    public Integer getMvnoIdFromCurrentStaff(Integer custId) {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            if(custId!=null){
                mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);

            }
//            else {
//                SecurityContext securityContext = SecurityContextHolder.getContext();
//                if (null != securityContext.getAuthentication()) {
//                    if(securityContext.getAuthentication().getPrincipal() != null)
//                        mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//                }
//            }
        } catch (Exception e) {
//            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

}

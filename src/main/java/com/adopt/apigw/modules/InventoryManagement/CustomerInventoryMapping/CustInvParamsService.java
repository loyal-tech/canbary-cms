package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.rabbitMq.message.CustInvParamsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CustInvParamsService {

    private final Logger log = LoggerFactory.getLogger(CustInvParamsService.class);

    @Autowired
    private CustInvParamsRepo custInvParamsRepo;

    @Autowired
    private CustInvParamsMapper custInvParamsMapper;

    public List<CustInvParams> saveCustInvParams(CustInvParamsMessage message) {
        log.info("Staring Save Cust Inventory Parameters from Kafka");
        List<CustInvParamsDto> custInvParamsDtos = message.getCustInvParams();
        if(!CollectionUtils.isEmpty(custInvParamsDtos)) {
           try {
               List<CustInvParams> custInvParams = custInvParamsMapper.dtoToDomain(custInvParamsDtos, new CycleAvoidingMappingContext());
               log.info("Ending Save Cust Inventory Parameters from Kafka");
               return custInvParamsRepo.saveAll(custInvParams);
           } catch (Exception ex) {
               log.error("Error to save Customer Inventory Mapping: "+ex.getMessage()+" for CustId: "+message);
           }
        }
        return null;
    }

    public List<CustInvParams> updateCustInvParams(CustInvParamsMessage message) {
        if(message.getCustSerMapId() != null) {
            List<CustInvParamsDto> custInvParamsDtos = message.getCustInvParams();
            if(!CollectionUtils.isEmpty(custInvParamsDtos)) {
                try {
                    List<CustInvParams> custInvParams = custInvParamsRepo.findAllByCustInvId(message.getCustSerMapId());
                    if(!CollectionUtils.isEmpty(custInvParams))
                        custInvParamsRepo.deleteInBatch(custInvParams);
                    List<CustInvParams> newCustInvParams = custInvParamsMapper.dtoToDomain(custInvParamsDtos, new CycleAvoidingMappingContext());
                    return custInvParamsRepo.saveAll(newCustInvParams);
                } catch (Exception ex) {
                    log.error("Error to update Customer Inventory Mapping: "+ex.getMessage()+" for CustId: "+message);
                }
            }
        }

        return null;
    }
}

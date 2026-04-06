package com.adopt.apigw.service.common;

import com.adopt.apigw.model.common.BatchPaymentMapping;
import com.adopt.apigw.model.common.QBatchPayment;
import com.adopt.apigw.model.common.QBatchPaymentMapping;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.pojo.api.BatchPaymentMappingPojo;
import com.adopt.apigw.pojo.api.CreditPojo;
import com.adopt.apigw.repository.common.BatchPaymentMappingRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchPaymentMappingService extends AbstractService<BatchPaymentMapping, BatchPaymentMappingPojo, Long> {

    @Autowired
    private BatchPaymentMappingRepository batchPaymentMappingRepository;

    @Override
    protected JpaRepository<BatchPaymentMapping, Long> getRepository() {
        return batchPaymentMappingRepository;
    }

    public void getBatchPaymentMappingSummary(Long batchId)
    {
        QBatchPaymentMapping batchPaymentMapping=QBatchPaymentMapping.batchPaymentMapping;
        BooleanExpression expression=batchPaymentMapping.batchPayment.id.eq(batchId);
        List<BatchPaymentMapping> batchPaymentMappingList= (List<BatchPaymentMapping>) batchPaymentMappingRepository.findAll(expression);
    }

    public CreditPojo convertCreditDocumentIntoCreditPojo(BatchPaymentMapping mapping)
    {
        CreditPojo creditPojo=new CreditPojo();
        if(mapping!=null)
        {
            creditPojo.setMappingId(mapping.getId());
            creditPojo.setCreditDocumentId(mapping.getCreditDocument().getId());
            creditPojo.setAmount(mapping.getCreditDocument().getAmount());
            creditPojo.setPaymode(mapping.getCreditDocument().getPaymode());
            creditPojo.setCustomerName(mapping.getCreditDocument().getCustomer().getCustname());
            creditPojo.setPaymentdate(mapping.getCreditDocument().getPaymentdate());
            creditPojo.setStatus(mapping.getCreditDocument().getStatus());
            creditPojo.setBatchPaymentId(mapping.getBatchPayment().getId());
            creditPojo.setTdsAmount(mapping.getCreditDocument().getTdsamount());
            creditPojo.setAbbsAmount(mapping.getCreditDocument().getAbbsAmount());
        }
        return creditPojo;
    }
}

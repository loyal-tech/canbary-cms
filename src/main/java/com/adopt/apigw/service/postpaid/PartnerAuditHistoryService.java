package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.model.postpaid.PartnerAuditHistory;
import com.adopt.apigw.model.postpaid.QPartnerAuditHistory;
import com.adopt.apigw.repository.postpaid.PartnerAuditHistoryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class PartnerAuditHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(PartnerAuditHistoryService.class);

    @Autowired
    private PartnerAuditHistoryRepository auditHistoryRepository;

    public PartnerAuditHistory savePartnerAudit(Partner partner) {
        PartnerAuditHistory partnerAuditHistory = new PartnerAuditHistory();
        partnerAuditHistory.setPartnerId(partner.getId());
        partnerAuditHistory.setLastAuditdate(partner.getLastbilldate().atTime(LocalTime.now()));
        partnerAuditHistory.setCreatedate(LocalDateTime.now());
        partnerAuditHistory.setNewCustomerCount(partner.getNewCustomerCount());
        partnerAuditHistory.setRenewCustomerCount(partner.getRenewCustomerCount());
        partnerAuditHistory.setTotalCustomerCount(partner.getTotalCustomerCount());
        partnerAuditHistory.setMvnoId(partner.getMvnoId());
        partnerAuditHistory.setBuId(partner.getBuId());
        return auditHistoryRepository.save(partnerAuditHistory);
    }

    public List<PartnerAuditHistory> fetch(Integer partnerId) {
        QPartnerAuditHistory qPartnerAuditHistory = QPartnerAuditHistory.partnerAuditHistory;
        BooleanExpression expression = qPartnerAuditHistory.isNotNull().and(qPartnerAuditHistory.isActive.eq(true)).and(qPartnerAuditHistory.isDelete).eq(false);
        if(partnerId != null) {
            expression = expression.and(qPartnerAuditHistory.partnerId.eq(partnerId));
        }
        return  (List<PartnerAuditHistory>) auditHistoryRepository.findAll(expression);
    }
}

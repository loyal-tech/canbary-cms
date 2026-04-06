package com.adopt.apigw.modules.tickets.service;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.tickets.domain.CaseDocDetails;
import com.adopt.apigw.modules.tickets.domain.QCaseDocDetails;
import com.adopt.apigw.modules.tickets.mapper.CaseDocDetailsMapper;
import com.adopt.apigw.modules.tickets.model.CaseDocDetailsDTO;
import com.adopt.apigw.modules.tickets.repository.CaseDocDetailsRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaseDocDetailsService extends ExBaseAbstractService<CaseDocDetailsDTO, CaseDocDetails, Long> {

    @Autowired
    CaseDocDetailsRepository repository;

    public CaseDocDetailsService(CaseDocDetailsRepository repository, CaseDocDetailsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return " [CaseDocDetailsService] ";
    }

    public CaseDocDetails downloadDocument(Long docId, Long caseId) throws Exception {
        QCaseDocDetails qCaseDocDetails = QCaseDocDetails.caseDocDetails;
        BooleanExpression booleanExpression = qCaseDocDetails.ticketId.eq(caseId).and(qCaseDocDetails.docId.eq(docId)).and(qCaseDocDetails.docStatus.eq(SubscriberConstants.ACTIVE));
        Optional<CaseDocDetails> caseDocDetails = repository.findOne(booleanExpression);
        return caseDocDetails.orElse(null);


    }
}

package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.model.postpaid.DebitDocumentTAXRel;
import com.adopt.apigw.repository.postpaid.DebitDocumentTAXRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebitDocumentTAXRelService {

    @Autowired
    DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    public List<DebitDocumentTAXRel> getTotalTaxByType(Integer debitDocumentID) {
        return debitDocumentTAXRelRepository.getTotalTaxByType(debitDocumentID);
    }
}

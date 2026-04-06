package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.DunningRuleActionMapper;
import com.adopt.apigw.model.postpaid.DunningRuleAction;
import com.adopt.apigw.pojo.api.DunningRuleActionPojo;
import com.adopt.apigw.repository.postpaid.DunningRuleActionRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DunningRuleActionService extends AbstractService<DunningRuleAction, DunningRuleActionPojo, Integer> {

    @Autowired
    private DunningRuleActionRepository entityRepository;
    @Autowired
    private DunningRuleActionMapper dunningRuleActionMapper;

    @Override
    protected JpaRepository<DunningRuleAction, Integer> getRepository() {
        return entityRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Ledger");
        List<DunningRuleActionPojo> dunningRuleActionPojoList = entityRepository.findAll().stream()
                .map(data -> dunningRuleActionMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, DunningRuleActionPojo.class, dunningRuleActionPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<DunningRuleActionPojo> dunningRuleActionPojoList = entityRepository.findAll().stream()
                .map(data -> dunningRuleActionMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, DunningRuleActionPojo.class, dunningRuleActionPojoList, null);
    }
}

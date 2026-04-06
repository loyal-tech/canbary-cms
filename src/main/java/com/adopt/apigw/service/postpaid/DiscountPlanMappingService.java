package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.DiscountPlanMappingMapper;
import com.adopt.apigw.model.postpaid.DiscountMapping;
import com.adopt.apigw.pojo.api.DiscountMappingPojo;
import com.adopt.apigw.repository.postpaid.DiscountMappingRepository;
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
public class DiscountPlanMappingService extends AbstractService<DiscountMapping, DiscountMappingPojo, Integer> {

    @Autowired
    private DiscountMappingRepository entityRepository;
    @Autowired
    private DiscountPlanMappingMapper mapper;

    @Override
    protected JpaRepository<DiscountMapping, Integer> getRepository() {
        return entityRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Discount Plan Mapping");
        List<DiscountMappingPojo> discountMappingPojoList = entityRepository.findAll().stream()
                .map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, DiscountMappingPojo.class, discountMappingPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<DiscountMappingPojo> discountMappingPojoList = entityRepository.findAll().stream()
                .map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, DiscountMappingPojo.class, discountMappingPojoList, null);
    }
}

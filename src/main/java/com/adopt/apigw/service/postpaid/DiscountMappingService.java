package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.DiscountMappingMapper;
import com.adopt.apigw.model.postpaid.DiscountPlanMapping;
import com.adopt.apigw.pojo.api.DiscountPlanMappingPojo;
import com.adopt.apigw.repository.postpaid.DiscountPlanMappingRepo;
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
public class DiscountMappingService extends AbstractService<DiscountPlanMapping, DiscountPlanMappingPojo, Integer> {

    @Autowired
    private DiscountPlanMappingRepo entityRepository;
    @Autowired
    private DiscountMappingMapper discountMappingMapper;

    @Override
    protected JpaRepository<DiscountPlanMapping, Integer> getRepository() {
        return entityRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Discount Mapping");
        List<DiscountPlanMappingPojo> discountPlanMappingPojoList = entityRepository.findAll().stream()
                .map(data -> discountMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, DiscountPlanMappingPojo.class, discountPlanMappingPojoList, null);
    }
    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<DiscountPlanMappingPojo> discountPlanMappingPojoList = entityRepository.findAll().stream()
                .map(data -> discountMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, DiscountPlanMappingPojo.class, discountPlanMappingPojoList, null);
    }
}

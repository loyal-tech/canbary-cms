package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.mapper.postpaid.TaxTypeTierMapper;
import com.adopt.apigw.model.postpaid.TaxTypeTier;
import com.adopt.apigw.pojo.api.TaxTypeTierPojo;
import com.adopt.apigw.repository.postpaid.TaxTypeTierRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaxTypeTierService extends AbstractService<TaxTypeTier, TaxTypeTierPojo, Integer> {

    @Autowired
    private TaxTypeTierRepository entityRepository;
    @Autowired
    private TaxTypeTierMapper taxTypeTierMapper;

    @Override
    protected JpaRepository<TaxTypeTier, Integer> getRepository() {
        return entityRepository;
    }

    public Page<TaxTypeTier> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Charges");
        List<TaxTypeTierPojo> taxTypeTierPojos = entityRepository.findAll().stream()
                .map(data -> taxTypeTierMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, TaxTypeTierPojo.class, taxTypeTierPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<TaxTypeTierPojo> taxTypeTierPojos = entityRepository.findAll().stream()
                .map(data -> taxTypeTierMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, TaxTypeTierPojo.class, taxTypeTierPojos, null);
    }
}

package com.adopt.apigw.modules.servicePlan.service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.mapper.ServicesMapper;
import com.adopt.apigw.modules.servicePlan.model.ServicesDTO;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

@Service
public class ServicesService extends ExBaseAbstractService<ServicesDTO, Services, Long> {



    public ServicesService(ServiceRepository repository, ServicesMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServicesService]";
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Services");
        createExcel(workbook, sheet, ServicesDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, ServicesDTO.class, null,mvnoId);
    }
}

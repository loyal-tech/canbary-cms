package com.adopt.apigw.modules.NetworkDevices.service.SlotService;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.NetworkDevices.domain.OLTPortDetails;
import com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper.OltPortMapper;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.NetworkDTO;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTPortDTO;
import com.adopt.apigw.modules.NetworkDevices.repository.OltPortRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

@Service
public class OltPortService extends ExBaseAbstractService<OLTPortDTO, OLTPortDetails, Long> {

    public OltPortService(OltPortRepository repository, OltPortMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[OltPortService]";
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("OltPort");
        createExcel(workbook, sheet, OLTPortDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, NetworkDTO.class, null,mvnoId);
    }

//    @Override
//    public GenericDataDTO search(GenericSearchDTO filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        return null;
//    }
}

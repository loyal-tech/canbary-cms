package com.adopt.apigw.modules.NetworkDevices.service.SlotService;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper.NetworkMapper;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.NetworkDTO;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkService extends ExBaseAbstractService<NetworkDTO, NetworkDevices, Long> {
    public NetworkService(NetworkDeviceRepository repository, NetworkMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[Network Service]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Network");
        createExcel(workbook, sheet, NetworkDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, NetworkDTO.class, null,mvnoId);
    }
}

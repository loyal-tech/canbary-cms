package com.adopt.apigw.modules.NetworkDevices.service.SlotService;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.NetworkDevices.domain.Oltslots;
import com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper.OLTSlotMapper;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.adopt.apigw.modules.NetworkDevices.repository.SloatRepository.OLTSlotRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OLTSlotService extends ExBaseAbstractService<OLTSlotDetailDTO, Oltslots, Long> {
    public OLTSlotService(OLTSlotRepository repository, OLTSlotMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    OLTSlotRepository slotRepository;

    @Autowired
    OLTSlotMapper mapper;

    @Override
    public String getModuleNameForLog() {
        return " [OLTSlotService()] ";
    }

    public GenericDataDTO search(GenericSearchDTO filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        List<GenericDataDTO> temp = new ArrayList<GenericDataDTO>();
        return (GenericDataDTO) temp;
    }

    public List<OLTSlotDetailDTO> getEntityByNetworkId(Long networkdevice_id) {
        List<Oltslots> oltslotsList = slotRepository.findAllByNetworkDevices_Id(networkdevice_id);
        return oltslotsList.stream().map(data ->
                mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }


    public boolean duplicateVerifyAtSaveInSloat(String name,Integer deviceId)throws Exception
    {
        boolean flag=false;
        Integer count=slotRepository.duplicateVerifyAtSave(deviceId,name);
        if(count==0){
            flag=true;
        }
        return flag;
    }
    public boolean duplicateVerifyEditInSloat(String name,Integer deviceId,Integer sloatId)throws Exception
    {
        boolean flag=false;
        Integer count=slotRepository.duplicateVerifyAtEdit(deviceId,name,sloatId);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        if(id!=null){
            Integer count=slotRepository.deleteVerifySlot(id);
            if(count==0){
                flag=true;
            }
        }
    return flag;
    }


    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("OltSlot");
        createExcel(workbook, sheet, OLTSlotDetailDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, OLTSlotDetailDTO.class, null,mvnoId);
    }
}
